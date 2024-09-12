package com.eddy.nrf.presentation.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eddy.nrf.presentation.ui.theme.EndColor
import com.eddy.nrf.presentation.ui.theme.StartColor
import com.eddy.nrf.presentation.ui.theme.Typography

@Preview(showBackground = true, widthDp = 200, heightDp = 200)
@Composable
fun Speed(modifier: Modifier = Modifier, speed: Float = 22.5f) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        GradientStrokeCircle(modifier = modifier)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Speed",
                style = Typography.bodyMedium
            )
            Text(
                speed.toString(),
                style = Typography.bodyLarge
            )
            Text("km/h", style = Typography.bodyMedium)
        }
    }
}

@Composable
fun GradientStrokeCircle(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(StartColor, EndColor), //여기가 바뀌면 안나옴
    strokeWidth: Dp = 10.dp
) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawCircle(
            brush = Brush.linearGradient(
                colors = colors,
                start = Offset(0f, 0f),
                end = Offset(canvasWidth, canvasHeight)
            ),
            center = Offset(canvasWidth / 2, canvasHeight / 2),
            radius = (canvasWidth - strokeWidth.toPx()) / 2,
            style = Stroke(width = strokeWidth.toPx())
        )
    }
}