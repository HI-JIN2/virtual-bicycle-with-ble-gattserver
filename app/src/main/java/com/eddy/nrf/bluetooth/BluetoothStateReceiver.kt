package com.eddy.nrf.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log

class BluetoothStateReceiver(
    private val context: Context,
    private val advertisingManager: AdvertisingManager,
    private val gattServerManager: GattServerManager,
    private val heartRateNotificationManager: HeartRateNotificationManager
) : BroadcastReceiver() {

    private var isReceiverRegistered = false

    fun register() {
        // 리시버 등록 코드 추가
        if (!isReceiverRegistered) {
            val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
            context.registerReceiver(this, filter)
            isReceiverRegistered = true
        }
    }

    fun unregister() {
        // 리시버 등록 상태를 확인한 후에만 해제
        if (isReceiverRegistered) {
            context.unregisterReceiver(this)
            isReceiverRegistered = false
        } else {
            Log.w("TAG", "Receiver was not registered")
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)

        when (state) {
            BluetoothAdapter.STATE_ON -> {
                advertisingManager.startAdvertising()
                gattServerManager.startServer()
                heartRateNotificationManager.startNotifications()
            }

            BluetoothAdapter.STATE_OFF -> {
                gattServerManager.stopServer()
                advertisingManager.stopAdvertising()
                heartRateNotificationManager.stopNotifications()
            }
        }
    }
}