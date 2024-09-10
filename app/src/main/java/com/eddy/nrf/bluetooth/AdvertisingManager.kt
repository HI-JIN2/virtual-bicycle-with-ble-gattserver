package com.eddy.nrf.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.os.ParcelUuid
import android.util.Log
import com.eddy.nrf.utils.Uuid

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
        } ?: Log.w(TAG, "Failed to create advertiser")
    }

    @SuppressLint("MissingPermission")
    fun stopAdvertising() {
        bluetoothLeAdvertiser?.stopAdvertising(advertiseCallback)
    }

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            Log.i(TAG, "LE Advertise Started.")
        }

        override fun onStartFailure(errorCode: Int) {
            Log.w(TAG, "LE Advertise Failed: $errorCode")
        }
    }

    companion object {
        private const val TAG = "AdvertisingManager"
    }
}