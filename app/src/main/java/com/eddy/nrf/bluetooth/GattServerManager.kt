package com.eddy.nrf.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.util.Log
import com.eddy.nrf.presentation.ui.BikeViewModel
import com.eddy.nrf.utils.Util.byteArrayToHexArray
import com.eddy.nrf.utils.Uuid
import java.util.Arrays

class GattServerManager(
    private val context: Context,
    private val bluetoothManager: BluetoothManager,
    private val bikeViewModel: BikeViewModel
) {

    var bluetoothGattServer: BluetoothGattServer? = null
    val registeredDevices = mutableSetOf<BluetoothDevice>()

    @SuppressLint("MissingPermission")
    fun startServer() {
        bluetoothGattServer = bluetoothManager.openGattServer(context, gattServerCallback)
        bluetoothGattServer?.addService(BluetoothServiceBuilder.createHeartRateService())
            ?: Log.w(TAG, "Unable to create GATT server")
    }

    @SuppressLint("MissingPermission")
    fun stopServer() {
        bluetoothGattServer?.close()
    }

    @SuppressLint("MissingPermission")
    fun notifyCharacteristicChanged(characteristic: BluetoothGattCharacteristic) {
        registeredDevices.forEach { device ->
            bluetoothGattServer?.notifyCharacteristicChanged(device, characteristic, false)
        }
    }

    private val gattServerCallback = object : BluetoothGattServerCallback() {

        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "BluetoothDevice CONNECTED: $device")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "BluetoothDevice DISCONNECTED: $device")
                registeredDevices.remove(device)
            }
        }

        @SuppressLint("MissingPermission")
        override fun onCharacteristicReadRequest(
            device: BluetoothDevice, requestId: Int, offset: Int,
            characteristic: BluetoothGattCharacteristic
        ) {
            when (characteristic.uuid) {
                Uuid.HEART_RATE_MEASUREMENT -> {
                    Log.i(TAG, "Read HeartRateMeasurement")
                    bluetoothGattServer?.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        byteArrayOf((60..100).random().toByte())
                    )
                }

                else -> {
                    Log.w(TAG, "Invalid Characteristic Read: ${characteristic.uuid}")
                    bluetoothGattServer?.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0,
                        null
                    )
                }
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

            Log.d(TAG, "Received write request: ${Arrays.toString(value)}")

            val resultArray = value?.let { byteArrayToHexArray(it) }
            val newGear = resultArray?.get(0)?.toByte() ?: 1

            bikeViewModel.changeGear(newGear.toFloat())
            Log.d(TAG, "Gear value changed to: $newGear")

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

    companion object {
        private const val TAG = "GattServerManager"
    }
}