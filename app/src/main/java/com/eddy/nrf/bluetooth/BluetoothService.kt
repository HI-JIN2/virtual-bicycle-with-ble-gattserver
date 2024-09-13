package com.eddy.nrf.bluetooth

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.eddy.nrf.presentation.ui.BikeViewModel

class BluetoothService(
    context: Context,
    bikeViewModel: BikeViewModel,
) : Service() {
    private val bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter

    private val advertisingManager: AdvertisingManager
    private val gattServerManager: GattServerManager
    private val heartRateNotificationManager: HeartRateNotificationManager
    private val bluetoothStateReceiver: BluetoothStateReceiver


    init { //블루투스 서비스가 거대해지는 것을 막기 위해서 단일 책임원칙에 따른 관심사 분리를 했음
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
        if (!checkBluetoothSupport(bluetoothAdapter)) {//블루투스 미지원 기기 거름
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

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}