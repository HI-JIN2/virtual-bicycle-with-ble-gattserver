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


    fun changeGear(gear: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    gear = gear
                )
            }
            //여긴 잘 바뀜
            Timber.d("기어값이 바뀌었습니다. : $gear   ${uiState.value.gear}")
        }
    }

    fun changeSpeed(proportionalFactor: Float) {
        viewModelScope.launch {
            val afterSpeed =
                Util.calculateSpeed(uiState.value.speed, uiState.value.gear, proportionalFactor)

            _uiState.update {
                it.copy(
                    speed = afterSpeed,
                )
            }
            Timber.d("속도값이 바뀌었습니다. : $afterSpeed   ${uiState.value.gear}")
        }
    }

    fun changeSpeed(gear: Int) {
        viewModelScope.launch {
            val afterSpeed =
                Util.calculateSpeed(uiState.value.speed, gear, uiState.value.proportionalFactor)

            _uiState.update {
                it.copy(
                    speed = afterSpeed,
                )
            }
            Timber.d("속도값이 바뀌었습니다. : $afterSpeed   ${uiState.value.gear}")
        }
    }

    fun changeTargetBattery(battery: Float) {
        viewModelScope.launch {
            val afterBattery = Util.calculateBattery(uiState.value.battery, battery)

            _uiState.update {
                it.copy(
                    battery = afterBattery,
                )
            }
        }
    }
}