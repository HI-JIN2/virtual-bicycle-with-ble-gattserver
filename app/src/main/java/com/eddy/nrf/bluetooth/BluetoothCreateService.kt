package com.eddy.nrf.bluetooth

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import com.eddy.nrf.utils.Uuid.CLIENT_CONFIG
import com.eddy.nrf.utils.Uuid.HEART_RATE_MEASUREMENT
import com.eddy.nrf.utils.Uuid.HEART_RATE_SERVICE

object BluetoothCreateService {
    fun createHeartRateService(): BluetoothGattService {
        val service = BluetoothGattService(
            HEART_RATE_SERVICE,
            BluetoothGattService.SERVICE_TYPE_PRIMARY
        )

        val heartRateMeasurement = BluetoothGattCharacteristic(
            HEART_RATE_MEASUREMENT,
            //Read-only characteristic, supports notifications
            BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_NOTIFY or BluetoothGattCharacteristic.PROPERTY_WRITE,
            BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE
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
}