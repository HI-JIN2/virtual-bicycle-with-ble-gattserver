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
import com.eddy.nrf.bluetooth.BluetoothService
import com.eddy.nrf.presentation.ui.BikeScreen
import com.eddy.nrf.presentation.ui.BikeViewModel
import com.eddy.nrf.presentation.ui.theme.NRFTheme

class MainActivity : ComponentActivity() {

    private val bikeViewModel: BikeViewModel by viewModels()

    private lateinit var bluetoothService: BluetoothService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        checkPermission()

        bluetoothService = BluetoothService(this, bikeViewModel)
        bluetoothService.initializeBluetooth()

        setContent {
            NRFTheme {
                // A surface container using the 'background' color from the theme
                BikeScreen(bikeViewModel)
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    }


    override fun onStop() {
        super.onStop()

        //onStop 상태에서 블루투스를 종료하면 안됨
//        bluetoothService.cleanup()
    }

    override fun onDestroy() { //Todo onDestroy 전에 무조건 onStop을 거침 그러면 여기서는 클린업을 안해줘도 되는것 아닌가?
        //재미있는 사실은 우선순위가 더 높은 앱이 메모리가 필요하다면 앱은 언제든지 종료될 수 있다.
        // 그렇게 때문에 onStop, onDestroy 메서드는 반드시 실행된다는 보장이 없다.
        super.onDestroy()

        bluetoothService.cleanup()
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