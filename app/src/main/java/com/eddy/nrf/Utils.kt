package com.eddy.nrf


import android.app.Activity
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.content.pm.PackageManager.PERMISSION_GRANTED
import java.util.UUID

object Utils {

    /*service*/
    val HEART_RATE_SERVICE: UUID = UUID.fromString("0000180D-0000-1000-8000-00805F9B34FB")

    /*Characteristic*/
    var HEART_RATE_MEASUREMENT: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")

    /* Mandatory Client Characteristic Config Descriptor */
    val CLIENT_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")


    fun createHeartRateService(): BluetoothGattService {
        val service = BluetoothGattService(
            HEART_RATE_SERVICE,
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )

        val heartRateMeasurement = BluetoothGattCharacteristic(
            HEART_RATE_MEASUREMENT,
            //Read-only characteristic, supports notifications
            BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ
        )
        val configDescriptor = BluetoothGattDescriptor(
            CLIENT_CONFIG,
            //Read/write descriptor
            BluetoothGattDescriptor.PERMISSION_READ or BluetoothGattDescriptor.PERMISSION_WRITE
        )
        heartRateMeasurement.addDescriptor(configDescriptor)

        service.addCharacteristic(heartRateMeasurement)

        return service
    }

    fun Activity.checkAllPermission(vararg permission: String): Boolean {
        return permission.all { checkSelfPermission(it) == PERMISSION_GRANTED }
    }

}