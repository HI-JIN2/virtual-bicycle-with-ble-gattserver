package com.eddy.nrf.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.os.ParcelUuid
import com.eddy.nrf.utils.Uuid
import timber.log.Timber

class AdvertisingManager(private val bluetoothAdapter: BluetoothAdapter) {

    private var bluetoothLeAdvertiser: BluetoothLeAdvertiser? = null

    @SuppressLint("MissingPermission")
    fun startAdvertising() {
        bluetoothLeAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser

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
        } ?: Timber.w("Failed to create advertiser")
    }

    @SuppressLint("MissingPermission")
    fun stopAdvertising() {
        bluetoothLeAdvertiser?.stopAdvertising(advertiseCallback)
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            Timber.i("LE Advertise Started.")
        }

        override fun onStartFailure(errorCode: Int) {
            Timber.w("LE Advertise Failed: $errorCode")
        }
    }
}