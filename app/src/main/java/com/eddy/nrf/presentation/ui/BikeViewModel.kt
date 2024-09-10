package com.eddy.nrf.presentation.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eddy.nrf.utils.Util.floatToByteArray
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

class BikeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BikeUiState())
    val uiState: StateFlow<BikeUiState> = _uiState.asStateFlow()

//    init {
//        onBluetoothDataReceived(1F)
//    }

    fun onBluetoothDataReceived(gear: Float) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    gear = gear
                )
            }
        }
    }

    fun getUiState(): ByteArray{
        val buffer = ByteBuffer.allocate(9)
        val data = uiState.value
        buffer.put(floatToByteArray(data.distance,4))
        buffer.put(floatToByteArray(data.speed,4))
        buffer.put(floatToByteArray(data.gear,1))
        val resultArray = buffer.array()

        return resultArray
    }
}