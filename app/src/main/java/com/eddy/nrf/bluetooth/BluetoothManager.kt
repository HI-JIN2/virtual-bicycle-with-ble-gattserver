package com.eddy.nrf.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.ParcelUuid
import android.util.Log
import com.eddy.nrf.presentation.ui.BikeViewModel
import com.eddy.nrf.utils.Util.byteArrayToHexArray
import com.eddy.nrf.utils.Util.floatToByteArray
import com.eddy.nrf.utils.Uuid
import java.nio.ByteBuffer
import java.util.Arrays


class BluetoothServiceManager(
    private val viewModel: BikeViewModel,
    private val context: Context,
) {

    private val bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private var bluetoothGattServer: BluetoothGattServer? = null

    private val registeredDevices = mutableSetOf<BluetoothDevice>()

    private val heartRateNotificationHandler = android.os.Handler()

    //    private val heartRateHandler = Handler()
    private var distance: Float = 15F
    private var speed: Float = 20F
    private var gear: Byte = 1


    @SuppressLint("MissingPermission")
    fun initializeBluetooth() {
        val bluetoothAdapter = bluetoothManager.adapter
        if (!checkBluetoothSupport(bluetoothAdapter)) {
            return //블루투스 안되는 기기 거르기
        }

        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        context.registerReceiver(bluetoothReceiver, filter)

        if (!bluetoothAdapter.isEnabled) {
            bluetoothAdapter.enable()
        } else {
            startAdvertising()
            startServer()
            heartRateNotificationHandler.post(heartRateRunnable)
        }
    }


    @SuppressLint("MissingPermission")
    private fun startAdvertising() {
        val bluetoothLeAdvertiser: BluetoothLeAdvertiser? =
            bluetoothManager.adapter.bluetoothLeAdvertiser
        // 광고 시작 로직
        bluetoothLeAdvertiser?.let {
            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build()

            val data = AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(false)
                .addServiceUuid(ParcelUuid(Uuid.HEART_RATE_SERVICE))
                .build()

            it.startAdvertising(settings, data, advertiseCallback)
        } ?: Log.w(TAG, "Failed to create advertiser")
    }

    //광고 종료
    @SuppressLint("MissingPermission")
    private fun stopAdvertising() {
        val bluetoothLeAdvertiser: BluetoothLeAdvertiser? =
            bluetoothManager.adapter.bluetoothLeAdvertiser
        bluetoothLeAdvertiser?.let {
            it.stopAdvertising(advertiseCallback)
        } ?: Log.w(TAG, "Failed to create advertiser")
    }

    @SuppressLint("MissingPermission")
    private fun startServer() {
        // GATT 서버 시작 로직
        bluetoothGattServer = bluetoothManager.openGattServer(context, gattServerCallback)

        bluetoothGattServer?.addService(BluetoothCreateService.createHeartRateService())
            ?: Log.w(TAG, "Unable to create GATT server")

    }

    //GATT서버 종료
    @SuppressLint("MissingPermission")
    private fun stopServer() {
        bluetoothGattServer?.close()
    }


    //광고에 대한 콜백
    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            Log.i(TAG, "LE Advertise Started.")
        }

        override fun onStartFailure(errorCode: Int) {
            Log.w(TAG, "LE Advertise Failed: $errorCode")
        }
    }

    //GATT에 대한 콜백
    private val gattServerCallback = object : BluetoothGattServerCallback() {

        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "BluetoothDevice CONNECTED: $device")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "BluetoothDevice DISCONNECTED: $device")
                //Remove device from any active subscriptions
                registeredDevices.remove(device)
            }
        }

        @SuppressLint("MissingPermission")
        override fun onCharacteristicReadRequest(
            device: BluetoothDevice, requestId: Int, offset: Int,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (Uuid.HEART_RATE_MEASUREMENT == characteristic.uuid) {
                Log.i(TAG, "Read HeartRateMeasurement")
                bluetoothGattServer?.sendResponse(
                    device,
                    requestId,
                    BluetoothGatt.GATT_SUCCESS,
                    0,
                    byteArrayOf((60..100).random().toByte())
                )
            } else {
                Log.w(TAG, "Invalid Characteristic Read: " + characteristic.uuid)
                bluetoothGattServer?.sendResponse(
                    device,
                    requestId,
                    BluetoothGatt.GATT_FAILURE,
                    0,
                    null
                )
            }
        }

        @SuppressLint("MissingPermission")
        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice?,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic?,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray?
        ) {
            super.onCharacteristicWriteRequest(
                device,
                requestId,
                characteristic,
                preparedWrite,
                responseNeeded,
                offset,
                value
            )

            Log.d(TAG, Arrays.toString(value))

            val resultArray = value?.let { byteArrayToHexArray(it) }
            gear = resultArray?.get(0)?.toByte() ?: 1


            updateUi(gear.toFloat())

            if (responseNeeded) {
                bluetoothGattServer?.sendResponse(
                    device,
                    requestId,
                    BluetoothGatt.GATT_SUCCESS,
                    0,
                    null
                )
            }
        }

        //Descriptorr CCCD를 써야 노티를 보낼 수 있음
        @SuppressLint("MissingPermission")
        override fun onDescriptorReadRequest(
            device: BluetoothDevice, requestId: Int, offset: Int,
            descriptor: BluetoothGattDescriptor
        ) {
            if (Uuid.CLIENT_CONFIG == descriptor.uuid) {
                Log.d(TAG, "Config descriptor read")
                val returnValue = if (registeredDevices.contains(device)) {
                    BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                } else {
                    BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                }
                bluetoothGattServer?.sendResponse(
                    device,
                    requestId,
                    BluetoothGatt.GATT_SUCCESS,
                    0,
                    returnValue
                )
            } else {
                Log.w(TAG, "Unknown descriptor read request")
                bluetoothGattServer?.sendResponse(
                    device,
                    requestId,
                    BluetoothGatt.GATT_FAILURE,
                    0, null
                )
            }
        }

        @SuppressLint("MissingPermission")
        override fun onDescriptorWriteRequest(
            device: BluetoothDevice, requestId: Int,
            descriptor: BluetoothGattDescriptor,
            preparedWrite: Boolean, responseNeeded: Boolean,
            offset: Int, value: ByteArray
        ) {
            if (Uuid.CLIENT_CONFIG == descriptor.uuid) {
                if (Arrays.equals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, value)) {
                    Log.d(TAG, "Subscribe device to notifications: $device")
                    registeredDevices.add(device)
                } else if (Arrays.equals(
                        BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE,
                        value
                    )
                ) {
                    Log.d(TAG, "Unsubscribe device from notifications: $device")
                    registeredDevices.remove(device)
                    heartRateNotificationHandler.removeCallbacks(heartRateRunnable)
                }

                if (responseNeeded) {
                    bluetoothGattServer?.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0, null
                    )
                }
            } else {
                Log.w(TAG, "Unknown descriptor write request")
                if (responseNeeded) {
                    bluetoothGattServer?.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0, null
                    )
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun notifyHeartRate(heartRate: ByteArray) {
        if (registeredDevices.isEmpty()) return

        Log.i(TAG, "Sending heart rate update to ${registeredDevices.size} subscribers")
        for (device in registeredDevices) {
            val heartRateCharacteristic = bluetoothGattServer
                ?.getService(Uuid.HEART_RATE_SERVICE)
                ?.getCharacteristic(Uuid.HEART_RATE_MEASUREMENT)
            heartRateCharacteristic?.value = heartRate

            bluetoothGattServer?.notifyCharacteristicChanged(device, heartRateCharacteristic, false)
        }
    }

    fun cleanup() {
        context.unregisterReceiver(bluetoothReceiver)
        // GATT 서버 및 광고 종료 로직

        stopServer()
        stopAdvertising()
        heartRateNotificationHandler.removeCallbacks(heartRateRunnable)
    }

    private fun checkBluetoothSupport(bluetoothAdapter: BluetoothAdapter?): Boolean {
        // Bluetooth 지원 여부 확인

        if (bluetoothAdapter == null) {
            Log.w(TAG, "Bluetooth is not supported")
            return false
        }

        return true
    }

    //리시버를 통해 notification 함
    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)

            when (state) {
                BluetoothAdapter.STATE_ON -> {
                    startAdvertising()
                    startServer()
                    heartRateNotificationHandler.post(heartRateRunnable)  // Start heart rate notifications

                }

                BluetoothAdapter.STATE_OFF -> {
                    stopServer()
                    stopAdvertising()
                    heartRateNotificationHandler.removeCallbacks(heartRateRunnable)  // Stop heart rate notifications

                }
            }
        }
    }

    private val heartRateRunnable = object : Runnable {
        override fun run() {

            val buffer = ByteBuffer.allocate(9)
            buffer.put(floatToByteArray(distance))
            buffer.put(floatToByteArray(speed))
            buffer.put(gear)
            val resultArray = buffer.array()

            Log.d(TAG, "거리+속도+기어 : ${byteArrayToHexArray(resultArray).joinToString(" ")}")

            notifyHeartRate(resultArray)
            heartRateNotificationHandler.postDelayed(this, 1000)
        }
    }

    fun updateUi(gear: Float) {
        viewModel.updateUi(gear)
    }

    companion object {
        val TAG = "BluetoothServiceManager"
    }
}
