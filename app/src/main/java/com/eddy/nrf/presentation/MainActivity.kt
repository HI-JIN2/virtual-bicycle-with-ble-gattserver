package com.eddy.nrf.presentation

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH_ADVERTISE
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.eddy.nrf.bluetooth.BluetoothService
import com.eddy.nrf.presentation.ui.BikeScreen
import com.eddy.nrf.presentation.ui.BikeViewModel
import com.eddy.nrf.presentation.ui.theme.NRFTheme

class MainActivity : ComponentActivity() {

    private val bikeViewModel: BikeViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    private lateinit var bluetoothService: BluetoothService

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        checkPermission()

        bluetoothService = BluetoothService( this, bikeViewModel)
        bluetoothService.initializeBluetooth()

        setContent {
            NRFTheme {
                // A surface container using the 'background' color from the theme
                BikeScreen(bikeViewModel)
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStop() {
        super.onStop()

        //Todo 안해도 될까???
//        bluetoothServiceManager.cleanup()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroy() {
        super.onDestroy()

//        bluetoothServiceManager.cleanup()
    }

    private fun checkPermission() {
        // 런타임 권한 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED
            ) {
                requestBlePermissions()
                return
            }
        } else {
            if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestBlePermissions()
                return
            }
        }
    }

    private fun requestBlePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions(
                arrayOf(
                    BLUETOOTH_SCAN,
                    BLUETOOTH_CONNECT,
                    BLUETOOTH_ADVERTISE,
                    ACCESS_FINE_LOCATION
                ), 1
            )
        } else {
            requestPermissions(
                arrayOf(ACCESS_FINE_LOCATION),
                2
            )
        }
    }
}