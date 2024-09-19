package com.eddy.nrf.bluetooth

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import com.eddy.nrf.utils.Uuid
import java.util.UUID

class BluetoothGattServiceBuilder(uuid: UUID, serviceType: Int) {
    //블루투스 생성 과정을 빌더로 만든 클래스

    private val service: BluetoothGattService = BluetoothGattService(uuid, serviceType)

    fun addCharacteristic(
        uuid: UUID,
        properties: Int,
        permissions: Int
    ): CharacteristicBuilder {
        val characteristic = BluetoothGattCharacteristic(uuid, properties, permissions)
        service.addCharacteristic(characteristic)
        return CharacteristicBuilder(characteristic)
    }

    fun build(): BluetoothGattService = service

    inner class CharacteristicBuilder(private val characteristic: BluetoothGattCharacteristic) {
        fun addDescriptor(
            uuid: UUID,
            permissions: Int
        ): CharacteristicBuilder {
            val descriptor = BluetoothGattDescriptor(uuid, permissions)
            characteristic.addDescriptor(descriptor)
            return this
        }

        fun and(): BluetoothGattServiceBuilder = this@BluetoothGattServiceBuilder
    }

    companion object {
        fun createHeartRateService(): BluetoothGattService {
            return BluetoothGattServiceBuilder(
                Uuid.HEART_RATE_SERVICE,
                BluetoothGattService.SERVICE_TYPE_PRIMARY
            )
                .addCharacteristic(
                    Uuid.HEART_RATE_MEASUREMENT,
                    BluetoothGattCharacteristic.PROPERTY_READ or
                            BluetoothGattCharacteristic.PROPERTY_NOTIFY or
                            BluetoothGattCharacteristic.PROPERTY_WRITE,
                    BluetoothGattCharacteristic.PERMISSION_READ or
                            BluetoothGattCharacteristic.PERMISSION_WRITE
                )
                .addDescriptor(
                    Uuid.CLIENT_CONFIG,
                    BluetoothGattDescriptor.PERMISSION_READ or
                            BluetoothGattDescriptor.PERMISSION_WRITE
                )
                .and()
                .build()
        }
    }
}