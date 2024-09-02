package com.eddy.nrf

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.nio.ByteBuffer
import java.nio.ByteOrder


//class MainActivity : AppCompatActivity() {
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//
////        example1()
//    }





class MainActivity : AppCompatActivity() {
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothLeScanner: BluetoothLeScanner? = null
    private var isScanning = false
    private var mScanCallback: ScanCallback? = null
    private val mBluetoothDevices = ArrayList<BluetoothDevice>()
    private var scanResultList: ArrayList<String>? = null
    private val mHandler = Handler()

    private var mBluetoothLeAdvertiser: BluetoothLeAdvertiser? = null
    private var isAdvertising = false
    private var mAdvertiseCallback: AdvertiseCallback? = null

    private var dialogPermission: Dialog? = null
    private val REQUEST_ENABLE_BT = 1


    private var textView_info: TextView? = null
    private var listView_scanResult: ListView? = null
    private var editText_data: TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        checkBluetoothLowEnergyFeature()
        initBluetoothService()
        initScanAndAdvertiseCallback()
        initUI()
    }

    private fun checkBluetoothLowEnergyFeature() {
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initBluetoothService() {
        val bluetoothManager =
            getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.bt_not_supported, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initScanAndAdvertiseCallback() {
        mScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                super.onScanResult(callbackType, result)
                mBluetoothDevices.clear()
                scanResultList!!.clear()
                saveScanResult(result)
                setAndUpdateListView()
            }

            override fun onBatchScanResults(results: List<ScanResult>) {
                super.onBatchScanResults(results)
                mBluetoothDevices.clear()
                scanResultList!!.clear()
                for (result: ScanResult in results) {
                    saveScanResult(result)
                }
                setAndUpdateListView()
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Toast.makeText(
                    this@MainActivity,
                    "Error scanning devices: $errorCode",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        mAdvertiseCallback = object : AdvertiseCallback() {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
                super.onStartSuccess(settingsInEffect)
            }

            override fun onStartFailure(errorCode: Int) {
                super.onStartFailure(errorCode)
            }
        }
    }

    private fun saveScanResult(result: ScanResult) {
        if (result.scanRecord != null && hasManufacturerData(result.scanRecord)) {
            var tempValue = unpackPayload(
                result.scanRecord!!
                    .getManufacturerSpecificData(MainActivity.Companion.GENERAL)
            )
            tempValue = tempValue.substring(1, tempValue.length)
            if (!mBluetoothDevices.contains(result.device)) {
                mBluetoothDevices.add(result.device)
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                scanResultList!!.add(
                    result.device.name
                            + " rssi:" + result.rssi + "\r\n"
                            + result.device.address + "\r\n"
                            + tempValue
                )
            }
        }
    }

    private fun hasManufacturerData(record: ScanRecord?): Boolean {
        val data = record!!.manufacturerSpecificData
        return (data != null && data[MainActivity.Companion.GENERAL] != null)
    }

    private fun unpackPayload(data: ByteArray?): String {
        val buffer = ByteBuffer.wrap(data)
            .order(ByteOrder.LITTLE_ENDIAN)
        buffer.get()
        val b = ByteArray(buffer.limit())
        for (i in 0 until buffer.limit()) {
            b[i] = buffer[i]
        }
        try {
            return (String(b, charset("UTF-8")))
        } catch (e: Exception) {
            return " Unable to unpack."
        }
    }

    private fun setAndUpdateListView() {
        val listAdapter: ListAdapter = ArrayAdapter(
            baseContext,
            R.layout.simple_expandable_list_item_1, scanResultList
        )
        listView_scanResult!!.adapter = listAdapter
    }

    private fun initUI() {
        textView_info = findViewById(R.id.textView_info)
        listView_scanResult = findViewById(R.id.listView_scanResult)
        scanResultList = ArrayList()
        setAndUpdateListView()
        editText_data = findViewById(R.id.editText_data)
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermission()
        } else {
            checkBluetoothEnableThenScanAndAdvertising()
        }
    }

    private fun checkPermission() {
        val permission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            checkBluetoothEnableThenScanAndAdvertising()
        } else {
            showDialogForPermission()
        }
    }

    private fun showDialogForPermission() {
        val dialogBuilder = AlertDialog.Builder(this@MainActivity)
        dialogBuilder.setTitle(resources.getString(R.string.dialog_permission_title))
        dialogBuilder.setMessage(resources.getString(R.string.dialog_permission_message))
        dialogBuilder.setPositiveButton(
            resources.getString(R.string.dialog_permission_ok)
        ) { dialogInterface, i ->
            ActivityCompat.requestPermissions(
                this@MainActivity,
                MainActivity.Companion.PERMISSIONS_ACCESS,
                MainActivity.Companion.REQUEST_ACCESS_FINE_LOCATION
            )
        }
        dialogBuilder.setNegativeButton(
            resources.getString(R.string.dialog_permission_no)
        ) { dialogInterface, i ->
            Toast.makeText(
                this@MainActivity,
                resources.getString(R.string.dialog_permission_toast_negative),
                Toast.LENGTH_LONG
            ).show()
        }
        if (dialogPermission == null) {
            dialogPermission = dialogBuilder.create()
        }
        if (!dialogPermission!!.isShowing) {
            dialogPermission!!.show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MainActivity.Companion.REQUEST_ACCESS_FINE_LOCATION -> {
                if ((grantResults.size > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    checkBluetoothEnableThenScanAndAdvertising()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        resources.getString(R.string.dialog_permission_toast_negative),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun checkBluetoothEnableThenScanAndAdvertising() {
        if (mBluetoothAdapter!!.isEnabled) {
            startScanLeDevice()
            startAdvertising()
        } else {
            openBluetoothSetting()
        }
    }

    private fun startScanLeDevice() {
        if (isScanning) {
            return
        }
        mHandler.postDelayed({ stopScanLeDevice() }, MainActivity.Companion.SCAN_PERIOD)

        isScanning = true
        mBluetoothLeScanner = mBluetoothAdapter!!.bluetoothLeScanner
        var reportDelay = 0
        if (mBluetoothAdapter!!.isOffloadedScanBatchingSupported) {
            reportDelay = 1000
        }
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(reportDelay.toLong())
            .build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_SCAN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mBluetoothLeScanner.startScan(null, settings, mScanCallback)
        textView_info!!.text = resources.getString(R.string.bt_scanning)
    }

    private fun stopScanLeDevice() {
        if (isScanning) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            mBluetoothLeScanner!!.stopScan(mScanCallback)
            isScanning = false
            textView_info!!.text = resources.getString(R.string.bt_stop_scan)
        }
    }

    private fun startAdvertising() {
        if (isAdvertising) {
            return
        }
        isAdvertising = true
        mBluetoothLeAdvertiser = mBluetoothAdapter!!.bluetoothLeAdvertiser
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setConnectable(false)
            .setTimeout(0)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .build()
        val data = AdvertiseData.Builder()
            .addManufacturerData(
                MainActivity.Companion.GENERAL,
                buildPayload(editText_data!!.text.toString())
            )
            .build()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADVERTISE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mBluetoothLeAdvertiser.startAdvertising(settings, data, mAdvertiseCallback)
    }

    private fun buildPayload(value: String): ByteArray {
        val flags = 0x8000000.toByte()
        var b = byteArrayOf()
        try {
            b = value.toByteArray(charset("UTF-8"))
        } catch (e: Exception) {
            return b
        }
        val max = 26 //如果加device name最大是16個字，不加是26(不含flag)
        val capacity: Int
        if (b.size <= max) {
            capacity = b.size + 1
        } else {
            capacity = max + 1
            System.arraycopy(b, 0, b, 0, max)
        }
        val output: ByteArray
        output = ByteBuffer.allocate(capacity)
            .order(ByteOrder.LITTLE_ENDIAN) //GATT APIs expect LE order
            .put(flags) //Add the flags byte
            .put(b)
            .array()
        return output
    }

    private fun openBluetoothSetting() {
        val bluetoothSettingIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        startActivityForResult(bluetoothSettingIntent, REQUEST_ENABLE_BT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BT) {
            checkBluetoothEnableThenScanAndAdvertising()
        }
    }

    fun btnClick(v: View) {
        when (v.id) {
            R.id.button_scan -> startScanLeDevice()
            R.id.button_stop -> stopScanLeDevice()
            R.id.button_save -> stopAndRestartAdvertising()
        }
    }

    private fun stopAndRestartAdvertising() {
        if (isAdvertising) {
            mBluetoothLeAdvertiser = mBluetoothAdapter!!.bluetoothLeAdvertiser
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_ADVERTISE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback)
            isAdvertising = false
        }
        startAdvertising()
    }

    companion object {
        val GENERAL: Int = 0xFFFF

        private val SCAN_PERIOD: Long = 60000
        private val PERMISSIONS_ACCESS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private val REQUEST_ACCESS_FINE_LOCATION = 1
    }
}