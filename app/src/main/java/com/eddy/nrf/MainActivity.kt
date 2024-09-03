package com.eddy.nrf

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH_ADVERTISE
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
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
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.eddy.nrf.Utils.checkAllPermission
import com.eddy.nrf.databinding.ActivityMainBinding
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothLeAdvertiser: BluetoothLeAdvertiser? = null
    private lateinit var enableBluetoothLauncher: ActivityResultLauncher<Intent>

    private lateinit var gattServer: BluetoothGattServer

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bleInitialize()

        bluetoothAdapter = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
        Log.d("BluetoothAdapter", "Device name: ${bluetoothAdapter.name}")

        binding.btnStart.setOnClickListener { handleStartButtonClick() }

        binding.btnStop.setOnClickListener {
            bluetoothLeAdvertiser?.stopAdvertising(advertiseCallback)
            binding.tvStatus.text = "광고 중지"
        }

        binding.btnStopSend.setOnClickListener { stopSendingResponses() }
        setupGattServer()
    }

    private fun handleStartButtonClick() {
        if (bluetoothAdapter.isEnabled) {
            startAdvertising()
        } else {
            if (!checkAllPermission(BLUETOOTH_CONNECT)) {
                Toast.makeText(this, "블루투스가 꺼져있어 광고를 시작할 수 없습니다.", Toast.LENGTH_SHORT).show()
                return
            }
            requestBluetoothEnable()
        }
    }

    private fun setupGattServer() {
        gattServer = (getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager)
            .openGattServer(this, gattServerCallback)
        val service = BluetoothGattService(
            Utils.HEART_RATE_UUID,
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )
        val characteristic = BluetoothGattCharacteristic(
            Utils.HEART_RATE_MEASUREMENT,
            BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        )
        service.addCharacteristic(characteristic)
        gattServer?.addService(service)
    }

    private fun bleInitialize() {
        if (!hasAllPermissions()) {
            requestBlePermissions()
        }
    }

    private fun hasAllPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            checkAllPermission(
                ACCESS_FINE_LOCATION,
                BLUETOOTH_SCAN,
                BLUETOOTH_CONNECT,
                BLUETOOTH_ADVERTISE
            )
        } else {
            checkAllPermission(ACCESS_FINE_LOCATION)
        }
    }

    private fun requestBlePermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(ACCESS_FINE_LOCATION, BLUETOOTH_SCAN, BLUETOOTH_CONNECT, BLUETOOTH_ADVERTISE)
        } else {
            arrayOf(ACCESS_FINE_LOCATION)
        }
        ActivityCompat.requestPermissions(this, permissions, getRequestPermissionCode())
    }

    private fun getRequestPermissionCode() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        PERMISSION_REQUEST_CODE_S
    } else {
        PERMISSION_REQUEST_CODE
    }

    private fun startAdvertising() {
        bluetoothLeAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
        bluetoothLeAdvertiser?.let { advertiser ->
            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .setConnectable(true)
                .build()

            val data = AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .addServiceUuid(Utils.HEART_RATE_P_UUID)
                .build()

            Log.d("BluetoothAdvertise", "Advertise Data: $data")

            if (!checkAllPermission(BLUETOOTH_ADVERTISE)) {
                Toast.makeText(this, "권한이 없어 광고를 시작할 수 없습니다.", Toast.LENGTH_SHORT).show()
                requestBlePermissions()
                return
            }

            advertiser.startAdvertising(settings, data, advertiseCallback)
        }
    }// 멤버 변수로 Handler와 Runnable 정의

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private val gattServerCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)

            var message = ""
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    message = "Device connected: ${device.address}"
                    binding.tvStatus.text = "STATE_CONNECTED"
                }

                BluetoothProfile.STATE_DISCONNECTED -> {
                    message = "Device disconnected: ${device.address}"
                    binding.tvDeviceInfo.text = message
                    binding.tvStatus.text = "STATE_DISCONNECTED " + newState.toString()
                }

                else -> {
                    message = "Unknown state"
                    binding.tvStatus.text = newState.toString()
                }
            }
            Log.d("GattServer", message)
        }

        override fun onNotificationSent(device: BluetoothDevice?, status: Int) {
            //Todo 얜 아예 안찍힘
            super.onNotificationSent(device, status)
            Log.d("GattServer", "onNotificationSent")

            val responseValue = ByteArray(10)
            responseValue[0] = Random.nextInt(0, 10).toByte() // 예시 값 0x2A

            gattServer?.sendResponse(
                device,
                1,
                BluetoothGatt.GATT_SUCCESS,
                1,
                responseValue
            )
        }

        override fun onCharacteristicReadRequest(
            device: BluetoothDevice,
            requestId: Int,
            offset: Int,
            characteristic: BluetoothGattCharacteristic
        ) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic)

            val responseValue = ByteArray(10)
            responseValue[0] = Random.nextInt(0, 10).toByte() // 예시 값 0x2A

            Log.d("GattServer", responseValue[0].toString())
            // 클라이언트에게 전송
            gattServer?.sendResponse(
                device,
                requestId,
                BluetoothGatt.GATT_SUCCESS,
                offset,
                responseValue
            )

        }
    }


    // 필요 시 전송을 중지하는 메서드
    fun stopSendingResponses() {
        handler.removeCallbacks(runnable)
    }


    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            Log.d("BluetoothAdvertise", "Advertising started successfully")
            binding.tvStatus.text = "광고 중"
        }

        override fun onStartFailure(errorCode: Int) {
            Log.e("BluetoothAdvertise", "Advertising failed with error code: $errorCode")
        }
    }

    private fun requestBluetoothEnable() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        enableBluetoothLauncher.launch(enableBtIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!checkAllPermission(BLUETOOTH_ADVERTISE)) {
            Toast.makeText(this, "권한이 없어 광고를 중단할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        bluetoothLeAdvertiser?.stopAdvertising(advertiseCallback)
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE_S = 101
        private const val PERMISSION_REQUEST_CODE = 100
    }
}
