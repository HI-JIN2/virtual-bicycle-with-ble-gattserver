package com.eddy.nrf.presentation.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BikeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BikeUiState())
    val uiState: StateFlow<BikeUiState> = _uiState.asStateFlow()

    fun changeGear(gear: Float) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    gear = gear
                )

            }
            //여긴 잘 바뀜
            Log.d("TAG", "기어값이 바뀌었습니다. : $gear   ${uiState.value.gear}")
        }
    }
}