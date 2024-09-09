package com.eddy.nrf.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.eddy.nrf.R
import com.eddy.nrf.presentation.component.Pas
import com.eddy.nrf.presentation.component.Speed
import com.eddy.nrf.presentation.ui.theme.EndColor
import com.eddy.nrf.presentation.ui.theme.NRFTheme
import com.eddy.nrf.presentation.ui.theme.Primary
import com.eddy.nrf.presentation.ui.theme.StartColor


//@Preview
@Composable
fun BikeScreen() {
//    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary)
            .padding(30.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Animation
            Image(
                painter = painterResource(id = R.drawable.out_001),
                contentDescription = "Bike Animation",
                modifier = Modifier
                    .width(300.dp)
                    .height(400.dp),
                contentScale = ContentScale.Fit
            )

            //Speed
            Speed(200.dp, 22.5)


            // PAS
            Pas(1)

        }
    }
}






@Composable
fun GradientStrokeCircle(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(StartColor, EndColor), //여기가 바뀌면 안나옴
    strokeWidth: Dp = 4.dp
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


@Preview(
    showBackground = true, widthDp = 700,
    heightDp = 360
)
@Composable
fun GreetingPreview() {
    NRFTheme {
        BikeScreen()
    }
}