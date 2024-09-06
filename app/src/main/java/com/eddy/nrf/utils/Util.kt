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

    fun floatToByteArray(value: Float): ByteArray {
        val bytes = ByteArray(4)
        ByteBuffer.wrap(bytes).putFloat(value)
        return bytes.reversedArray()
    }
}