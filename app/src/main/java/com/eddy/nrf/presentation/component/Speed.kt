package com.eddy.nrf.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eddy.nrf.presentation.GradientStrokeCircle

@Preview(showBackground = true, widthDp = 200, heightDp = 200)
@Composable
fun Speed(size: Dp = 200.dp, speed: Double = 22.5) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        GradientStrokeCircle(modifier = Modifier.size(size))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Speed")
            Text(speed.toString(), style = MaterialTheme.typography.headlineMedium)
            Text("km/h")
        }
    }
}