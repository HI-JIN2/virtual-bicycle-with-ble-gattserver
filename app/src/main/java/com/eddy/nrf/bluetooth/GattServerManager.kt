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
import com.eddy.nrf.presentation.ui.BikeViewModel
import com.eddy.nrf.utils.Util.byteArrayToHexArray
import com.eddy.nrf.utils.Uuid
import timber.log.Timber
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
            ?: Timber.w("Unable to create GATT server")
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
                Timber.i("BluetoothDevice CONNECTED: $device")
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Timber.i("BluetoothDevice DISCONNECTED: $device")
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
                    Timber.i("Read HeartRateMeasurement")
                    bluetoothGattServer?.sendResponse(
                        device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        byteArrayOf((60..100).random().toByte())
                    )
                }

                else -> {
                    Timber.i("Invalid Characteristic Read: ${characteristic.uuid}")
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

            Timber.i("Received write request: ${Arrays.toString(value)}")

            val resultArray = value?.let { byteArrayToHexArray(it) }
            val newGear = resultArray?.get(0)?.toByte() ?: 1

            bikeViewModel.changeGear(newGear.toInt())
//            bikeViewModel.changeSpeed(gear = newGear.toInt()) //Todo 여기서 호출하는 것이 맞나?
            Timber.i("Gear value changed to: $newGear")

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
                Timber.d("Config descriptor read")
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
                Timber.w("Unknown descriptor read request")
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
                    Timber.d("Subscribe device to notifications: $device")
                    registeredDevices.add(device)
                } else if (Arrays.equals(
                        BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE,
                        value
                    )
                ) {
                    Timber.d("Unsubscribe device from notifications: $device")
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
                Timber.w("Unknown descriptor write request")
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
}