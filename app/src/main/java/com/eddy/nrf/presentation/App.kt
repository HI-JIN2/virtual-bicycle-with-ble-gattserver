package com.eddy.nrf.presentation

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.eddy.nrf.presentation.ui.BikeScreen

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    Surface(modifier) {
        BikeScreen()
    }
}
