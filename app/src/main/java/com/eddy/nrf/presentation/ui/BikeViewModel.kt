package com.eddy.nrf.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eddy.nrf.utils.Util
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class BikeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BikeUiState())
    val uiState: StateFlow<BikeUiState> = _uiState.asStateFlow()

    init {
        startSpeedUpdate()
        startBatteryUpdate()
        startDistanceUpdate()
    }


    private fun startSpeedUpdate() {
        viewModelScope.launch {
            while (true) {
                updateSpeed()
                delay(1000) // 1초 대기
            }
        }
    }

    private fun updateSpeed() {
        val currentState = uiState.value
        val newSpeed = Util.calculateSpeed(
            currentState.speed,
            currentState.gear,
            currentState.proportionalFactor
        )
        _uiState.update {
            it.copy(speed = newSpeed)
        }
        Timber.d("속도가 업데이트되었습니다: $newSpeed")
    }

    private fun startBatteryUpdate() {
        viewModelScope.launch {
            while (true) {
                updateBattery()
                delay(1000) // 1초 대기
            }
        }
    }

    private fun updateBattery() {
        val currentState = uiState.value
        val newBattery = Util.calculateBattery(
            currentState.battery,
            currentState.targetBattery,
        )
        _uiState.update {
            it.copy(battery = newBattery)
        }
        Timber.d("배터리가 업데이트되었습니다: $newBattery")
    }

    private fun startDistanceUpdate() {
        viewModelScope.launch {
            while (true) {
                updateDistance()
                delay(1000) // 1초 대기
            }
        }
    }

    private fun updateDistance() {
        val currentState = uiState.value
        val odo = Util.calculateOdo(
            currentState.distance,
            currentState.speed,
        )
        _uiState.update {
            it.copy(distance = odo)
        }
        Timber.d("거리가 업데이트되었습니다: $odo")
    }

    fun changeProportionalFactor(proportionalFactor: Float) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    proportionalFactor = proportionalFactor
                )
            }
        }
    }

    fun changeGear(gear: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    gear = gear,
                )
            }
        }
    }

    fun changeTargetBattery(targetBattery: Float) {
        viewModelScope.launch {
            val afterBattery = Util.calculateBattery(uiState.value.battery, targetBattery)

            _uiState.update {
                it.copy(
                    battery = afterBattery,
                    targetBattery = targetBattery
                )
            }
        }
    }
}