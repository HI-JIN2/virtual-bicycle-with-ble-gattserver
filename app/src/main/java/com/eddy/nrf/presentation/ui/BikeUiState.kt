package com.eddy.nrf.presentation.ui


data class BikeUiState(
    var distance: Float =190.0f,
    var speed: Float = 0.0f,
    var gear: Int = 1,
    var battery: Float = 100f,
    var proportionalFactor: Float = 1.0f
)