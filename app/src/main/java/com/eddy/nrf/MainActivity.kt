package com.eddy.nrf

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.AdvertisingSet
import android.bluetooth.le.AdvertisingSetCallback
import android.bluetooth.le.AdvertisingSetParameters
import android.bluetooth.le.BluetoothLeAdvertiser
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanRecord
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.eddy.nrf.databinding.ActivityMainBinding
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import java.util.UUID


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothLeAdvertiser: BluetoothLeAdvertiser? = null

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 위치 권한 요청 (안드로이드 6.0 이상에서 필요)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH_ADVERTISE),
            1
        )

        requestPermissions(arrayOf(Manifest.permission.BLUETOOTH_CONNECT), 1)


        // BluetoothManager에서 BluetoothAdapter 가져오기
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Toast.makeText(this, "권한이 없어 광고를 시작할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("name", bluetoothAdapter.name.toString())
        bluetoothAdapter.setName("JIN")
        Log.d("name", bluetoothAdapter.name.toString())


        binding.btnStart.setOnClickListener{
            if (bluetoothAdapter.isEnabled) {
                startAdvertising()
            } else {
                // 블루투스가 꺼져있으면 켜는 과정 필요
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Toast.makeText(this, "블루투스가 꺼져있어 광고를 시작할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                bluetoothAdapter.isEnabled  // 사용자에게 Bluetooth 활성화 요청 다이얼로그가 표시됨.
            }
        }

    }

    private fun startAdvertising() {
        bluetoothLeAdvertiser = bluetoothAdapter.bluetoothLeAdvertiser
        bluetoothLeAdvertiser?.let { advertiser ->

            // 광고 설정
            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .setConnectable(true)  // 연결 가능한 상태로 광고
                .build()

            // 광고 데이터 설정 (기기 식별을 위한 UUID 등)
            val data = AdvertiseData.Builder()
                .setIncludeDeviceName(true)  // 기기 이름 포함
                .addServiceData(ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"),"yujin".toByteArray(
                    Charset.forName("UTF-8")))
                .addServiceUuid(ParcelUuid.fromString("0000180D-0000-1000-8000-00805F9B34FB"))  // 예시 UUID
                .build()

            data.includeDeviceName.toString()

            Log.d("BluetoothAdvertise", data.toString())
            Log.d("BluetoothAdvertise",             data.includeDeviceName.toString())

            // 광고 시작
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_ADVERTISE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                Toast.makeText(this, "권한이 없어 광고를 시작할 수 없습니다.", Toast.LENGTH_SHORT).show()
                return
            }
            advertiser.startAdvertising(settings, data, advertiseCallback)
        }
    }

    // 광고 콜백
    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
            Log.d("BluetoothAdvertise", "Advertising started successfully")
        }

        override fun onStartFailure(errorCode: Int) {
            Log.e("BluetoothAdvertise", "Advertising failed with error code: $errorCode")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 광고 중단
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_ADVERTISE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "권한이 없어 광고를 중단할 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        bluetoothLeAdvertiser?.stopAdvertising(advertiseCallback)
    }
}
