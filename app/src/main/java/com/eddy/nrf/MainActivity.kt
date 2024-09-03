package com.eddy.nrf

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH_ADVERTISE
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.eddy.nrf.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothLeAdvertiser: BluetoothLeAdvertiser? = null

    private lateinit var enableBluetoothLauncher: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bleInitialize()

        binding.btnAnim.setOnClickListener {

            val intent = Intent(this, AnimActivity::class.java)
            startActivity(intent)
        }

        // BluetoothManager에서 BluetoothAdapter 가져오기
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        Log.d("name", bluetoothAdapter.name.toString())

        binding.btnStart.setOnClickListener {
            if (bluetoothAdapter.isEnabled) {
                startAdvertising()
            } else {
                // 블루투스가 꺼져있으면 켜는 과정 필요
                if (ActivityCompat.checkSelfPermission(
                        this,
                        BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(this, "블루투스가 꺼져있어 광고를 시작할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
//                bluetoothAdapter.enable() -> deprecated
// 사용자에게 Bluetooth 활성화 요청 다이얼로그가 표시됨.
                // 블루투스 활성화 요청
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBluetoothLauncher.launch(enableBtIntent)
            }
        }

        val gattServer: BluetoothGattServer? =
            bluetoothManager.openGattServer(this, gattServerCallback)

        // 4. GATT 서비스를 추가합니다 (서비스 및 특성 정의).
        val service = BluetoothGattService(
            Utils.HEART_RATE_UUID,
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )

        // 서비스에 특성을 추가합니다.
        val characteristic = BluetoothGattCharacteristic(
            Utils.HEART_RATE_MEASUREMENT,
            BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        )
        service.addCharacteristic(characteristic)

        // 서버에 서비스를 추가합니다.
        gattServer?.addService(service)




    }

    //연결에 대한 콜백
    private val gattServerCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // 클라이언트가 서버에 연결되었을 때
                Log.d("GattServer", "Device connected: ${device.address}")
                binding.tvDeviceInfo.text = device.address
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // 클라이언트가 서버에서 연결이 끊어졌을 때
                Log.d("GattServer", "Device disconnected: ${device.address}")
                binding.tvDeviceInfo.text = "연결 실패"
            }//address는 그냥 받아지는데 name은 왜 퍼미션 체크를 또해야지????
        }

        // 여기에서 onServiceAdded, onCharacteristicReadRequest 등 다른 콜백들도 처리할 수 있습니다.
    }

    private fun bleInitialize() {
        // 런타임 권한 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
            ) {
                requestBlePermissions()
                return
            }
        } else {
            if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestBlePermissions()
                return
            }
        }
    }

    private fun requestBlePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(
                arrayOf(
                    BLUETOOTH_SCAN,
                    BLUETOOTH_CONNECT,
                    BLUETOOTH_ADVERTISE,
                    ACCESS_FINE_LOCATION
                ), PERMISSION_REQUEST_CODE_S
            )
        } else {
            requestPermissions(
                arrayOf(ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun startAdvertising() {
        bluetoothLeAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
        bluetoothLeAdvertiser?.let { advertiser ->

            // 광고 설정
            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .setConnectable(true)  // 연결 가능한 상태로 광고
                .build()

            // 광고 데이터 설정 (기기 식별을 위한 UUID 등)
            val data = AdvertiseData.Builder()
                .setIncludeDeviceName(true)  // 기기 이름 포함
//                .addServiceData(
//                    ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),
//                    "yujin".toByteArray(
//                        Charset.forName("UTF-8")
//                    )
//                )
                .addServiceUuid(Utils.HEART_RATE_P_UUID) // 예시 UUID
                .build()


            Log.d("BluetoothAdvertise", data.toString())
            Log.d("BluetoothAdvertise", data.includeDeviceName.toString())

            // 광고 시작
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

                Toast.makeText(this, "권한이 없어 광고를 시작할 수 없습니다.", Toast.LENGTH_SHORT).show()
                requestBlePermissions()
                advertiser.startAdvertising(settings, data, advertiseCallback)

                return
            }
            advertiser.startAdvertising(settings, data, advertiseCallback)
        }
    }

    // 광고 콜백
    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            Log.d("BluetoothAdvertise", "Advertising started successfully")

            binding.tvStatus.text = "광고 중"

        }

        override fun onStartFailure(errorCode: Int) {
            Log.e("BluetoothAdvertise", "Advertising failed with error code: $errorCode")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 광고 중단
        if (ActivityCompat.checkSelfPermission(
                this,
                BLUETOOTH_ADVERTISE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "권한이 없어 광고를 중단할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        bluetoothLeAdvertiser?.stopAdvertising(advertiseCallback)
    }

    companion object {
        val ADVERTISE_SUCCESS = 0
        val ADVERTISE_FAILED_DATA_TOO_LARGE = 1
        val ADVERTISE_FAILED_TOO_MANY_ADVERTISERS = 2
        val ADVERTISE_FAILED_ALREADY_STARTED = 3
        val ADVERTISE_FAILED_INTERNAL_ERROR = 4
        val ADVERTISE_FAILED_FEATURE_UNSUPPORTED = 5
        private var PERMISSION_REQUEST_CODE_S = 101
        private var PERMISSION_REQUEST_CODE = 100
    }
}
