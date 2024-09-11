package com.eddy.nrf.bluetooth

import android.os.Handler
import android.os.Looper
import com.eddy.nrf.presentation.ui.BikeUiState
import com.eddy.nrf.utils.Util.floatTo4ByteArray
import com.eddy.nrf.utils.Uuid
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import java.nio.ByteBuffer

class HeartRateNotificationManager(
    private val gattServerManager: GattServerManager,
    private val uiStateFlow: StateFlow<BikeUiState>
) {

    private val notificationHandler = Handler(Looper.getMainLooper())

    fun startNotifications() {
        notificationHandler.post(notificationRunnable)
    }

    fun stopNotifications() {
        notificationHandler.removeCallbacks(notificationRunnable)
    }

    private val notificationRunnable = object : Runnable {
        override fun run() {
            val distance = uiStateFlow.value.distance
            val speed = uiStateFlow.value.speed
            val gear = uiStateFlow.value.gear
            val battery = uiStateFlow.value.battery

            val buffer = ByteBuffer.allocate(13)//4+4+1+4
            buffer.put(floatTo4ByteArray(distance))
            buffer.put(floatTo4ByteArray(speed))
            buffer.put(gear.toByte())
            buffer.put(floatTo4ByteArray(battery))
            val resultArray = buffer.array()

            notifyHeartRate(resultArray)
            notificationHandler.postDelayed(this, 1000)
        }
    }

    private fun notifyHeartRate(heartRate: ByteArray) {
        if (gattServerManager.registeredDevices.isEmpty()) return

        Timber.i(
            "Sending heart rate update to ${gattServerManager.registeredDevices.size} subscribers"
        )
        val heartRateCharacteristic = gattServerManager.bluetoothGattServer
            ?.getService(Uuid.HEART_RATE_SERVICE)
            ?.getCharacteristic(Uuid.HEART_RATE_MEASUREMENT)
        heartRateCharacteristic?.value = heartRate

        gattServerManager.notifyCharacteristicChanged(heartRateCharacteristic!!)
    }
}