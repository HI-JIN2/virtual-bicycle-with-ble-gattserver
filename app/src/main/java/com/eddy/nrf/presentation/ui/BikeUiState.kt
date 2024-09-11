package com.eddy.nrf.presentation.ui


data class BikeUiState(
    var distance: Float =190.0f,
    var speed: Float = 15.0f,
    var gear: Int = 0,
    var battery: Int = 100,
)