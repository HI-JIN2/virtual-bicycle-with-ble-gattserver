package com.eddy.nrf

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.eddy.nrf.presentation.BikeScreen

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    Surface(modifier) {
        BikeScreen()
    }
}
