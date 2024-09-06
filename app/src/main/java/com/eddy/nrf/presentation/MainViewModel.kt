package com.eddy.nrf.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _uiState: MutableStateFlow<MainState> = MutableStateFlow(MainState())
    val uiState: StateFlow<MainState> = _uiState.asStateFlow()

    fun updateUi(gear: Float) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    gear = gear
                )
            }
        }
    }

    data class MainState(
        var distance: Float = 2.0f,
        var speed: Float = 15.0f,
        var gear: Float = 1.0f,
    )
}