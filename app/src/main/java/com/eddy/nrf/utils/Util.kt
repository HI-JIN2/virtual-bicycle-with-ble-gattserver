package com.eddy.nrf.utils

import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import java.nio.ByteBuffer

object Util {

    fun Activity.checkAllPermission(vararg permission: String): Boolean {
        return permission.all { checkSelfPermission(it) == PERMISSION_GRANTED }
    }

    fun byteArrayToHexArray(byteArray: ByteArray): Array<String> {
        return byteArray.map { String.format("%02X", it) }.toTypedArray()
    }

    fun floatToHex4Byte(value: Float, capacity: Int): ByteArray {
        val buffer = ByteBuffer.allocate(capacity)
        buffer.putFloat(value)
        return buffer.array()
    }

    fun floatTo4ByteArray(value: Float): ByteArray {
        val bytes = ByteArray(4)
        ByteBuffer.wrap(bytes).putFloat(value)
        return bytes.reversedArray()
    }

    fun IntTo4ByteArray(value: Int): ByteArray {
        val bytes = ByteArray(4)
        ByteBuffer.wrap(bytes).putInt(value)
        return bytes.reversedArray()
    }

    fun calculateSpeed(currentSpeed: Float, targetGear: Int, proportionalFactor: Float): Float {
        ///현재 속도 s, 목표속도 t, 비례값
        val targetSpeed = targetGear * 10.0 // 1단 -> 10km/h, 2단 -> 20km/h, 3단 -> 30km/h
        return (currentSpeed + ((targetSpeed * proportionalFactor) - currentSpeed) * 0.1).toFloat()
    }

    fun calculateBattery(currentBattery: Float, targetBattery: Float): Float {
        return (currentBattery + (targetBattery - currentBattery) * 0.2).toFloat()
    }

    fun calculateOdo(currentDistance: Float, currentSpeed: Float): Float {
        val newDistance = currentSpeed / 3600
        return currentDistance + newDistance
    }
}