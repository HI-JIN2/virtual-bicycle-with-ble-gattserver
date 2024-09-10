package com.eddy.nrf.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import com.eddy.nrf.presentation.ui.BikeViewModel

class BluetoothService(
    private val context: Context,
    private val bikeViewModel: BikeViewModel,
) {
    private val bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter

    private val advertisingManager: AdvertisingManager
    private val gattServerManager: GattServerManager
    private val heartRateNotificationManager: HeartRateNotificationManager
    private val bluetoothStateReceiver: BluetoothStateReceiver

    private var isReceiverRegistered = false


    init {
        advertisingManager = AdvertisingManager(bluetoothAdapter)
        gattServerManager = GattServerManager(context, bluetoothManager, bikeViewModel)
        heartRateNotificationManager =
            HeartRateNotificationManager(gattServerManager, bikeViewModel.uiState)
        bluetoothStateReceiver = BluetoothStateReceiver(
            context,
            advertisingManager,
            gattServerManager,
            heartRateNotificationManager
        )
    }

    fun initializeBluetooth() {
        if (!checkBluetoothSupport(bluetoothAdapter)) {
            return
        }

        bluetoothStateReceiver.register()

        if (!bluetoothAdapter.isEnabled) {
            bluetoothAdapter.enable()
        } else {
            startBluetoothServices()
        }
    }

    private fun startBluetoothServices() {
        advertisingManager.startAdvertising()
        gattServerManager.startServer()
        heartRateNotificationManager.startNotifications()
    }

    fun cleanup() {
        bluetoothStateReceiver.unregister()
        gattServerManager.stopServer()
        advertisingManager.stopAdvertising()
        heartRateNotificationManager.stopNotifications()
    }

    private fun checkBluetoothSupport(bluetoothAdapter: BluetoothAdapter?): Boolean {
        return bluetoothAdapter != null
    }
}