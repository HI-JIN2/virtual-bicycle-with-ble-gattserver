package com.eddy.nrf.presentation


import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.BLUETOOTH_ADVERTISE
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.eddy.nrf.bluetooth.BluetoothServiceManager
import com.eddy.nrf.databinding.ActivityMainBinding
import kotlinx.coroutines.launch


private const val TAG = "NewActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bluetoothServiceManager: BluetoothServiceManager

    private val viewModel: MainViewModel by viewModels()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()

        bluetoothServiceManager = BluetoothServiceManager(viewModel, this)
        bluetoothServiceManager.initializeBluetooth()

        val data = viewModel.uiState.value
        binding.tvData.text =
            "speed: ${data.speed}\n distance: ${data.distance}\ngear: ${data.gear}"

        setData()

        // Devices with a display should not go to sleep
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun setData() {
        lifecycleScope.launch {
            viewModel.uiState.collect { data ->
                binding.tvData.text =
                    "speed: ${data.speed}\n distance: ${data.distance}\ngear: ${data.gear}"
            }
        }
    }


    override fun onStop() {
        super.onStop()

        bluetoothServiceManager.cleanup()
    }

    override fun onDestroy() {
        super.onDestroy()

        bluetoothServiceManager.cleanup()
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