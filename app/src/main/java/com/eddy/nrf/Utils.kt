package com.eddy.nrf

/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.app.Activity
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.content.pm.PackageManager.PERMISSION_GRANTED
import java.util.UUID

/**
 * Implementation of the Bluetooth GATT Time Profile.
 * https://www.bluetooth.com/specifications/adopted-specifications
 */
object Utils {

    /* Current Time Service UUID */
    val TIME_SERVICE: UUID = UUID.fromString("00001805-0000-1000-8000-00805f9b34fb")

    /* Mandatory Current Time Information Characteristic */
    val CURRENT_TIME: UUID = UUID.fromString("00002a2b-0000-1000-8000-00805f9b34fb")

    /* Optional Local Time Information Characteristic */
    val LOCAL_TIME_INFO: UUID = UUID.fromString("00002a0f-0000-1000-8000-00805f9b34fb")

    /* Mandatory Client Characteristic Config Descriptor */
    val CLIENT_CONFIG: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    /*service*/
    val HEART_RATE_SERVICE: UUID = UUID.fromString("0000180D-0000-1000-8000-00805F9B34FB")

    /*Characteristic*/
    var HEART_RATE_MEASUREMENT: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")

    /* Bluetooth DST Offset Codes */
    private const val DST_STANDARD: Byte = 0x0
    private const val DST_HALF: Byte = 0x2
    private const val DST_SINGLE: Byte = 0x4
    private const val DST_DOUBLE: Byte = 0x8
    private const val DST_UNKNOWN = 0xFF.toByte()

    /**
     * Return a configured [BluetoothGattService] instance for the
     * Current Time Service.
     */
    fun createHeartRateService(): BluetoothGattService {
        val service = BluetoothGattService(
            HEART_RATE_SERVICE,
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )

        // Current Time characteristic
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

    fun generateRandomString(length: Int): Byte {
        val allowedChars = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("").toByte()
    }
}