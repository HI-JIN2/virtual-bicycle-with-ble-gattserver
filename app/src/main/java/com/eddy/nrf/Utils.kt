package com.eddy.nrf

import android.app.Activity
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.ParcelUuid
import java.util.UUID


object Utils {
    var HEART_RATE_MEASUREMENT: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
    var CLIENT_CHARACTERISTIC_CONFIG: String = "00002902-0000-1000-8000-00805f9b34fb"
    val HEART_RATE_UUID: UUID = UUID.fromString("0000180D-0000-1000-8000-00805F9B34FB")
    val HEART_RATE_P_UUID: ParcelUuid? =
        ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB")


    fun Activity.checkAllPermission(vararg permission: String): Boolean {
        return permission.all { checkSelfPermission(it) == PERMISSION_GRANTED }
    }
}