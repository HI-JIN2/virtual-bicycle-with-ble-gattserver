package com.eddy.nrf.bluetooth

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.eddy.nrf.presentation.ui.BikeViewModel

class BluetoothService(
    context: Context,
    bikeViewModel: BikeViewModel
) : Service() {
    private val bluetoothManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter

    private var advertisingManager: AdvertisingManager
    private var gattServerManager: GattServerManager
    private var heartRateNotificationManager: HeartRateNotificationManager
    private var bluetoothStateReceiver: BluetoothStateReceiver

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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification() // Notification 생성
        startForeground(1, notification) // Foreground 서비스 시작

        return START_STICKY // 서비스가 중단되었을 경우 자동으로 다시 시작
    }

    private fun createNotification(): Notification {
        val channelId = "bluetooth_service_channel"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Bluetooth Service",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Bluetooth Service")
            .setContentText("Bluetooth service is running.")
//            .setSmallIcon(R.drawable.ic_bluetooth) // 적절한 아이콘 설정
            .build()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("MissingPermission")
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

}