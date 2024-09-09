package com.eddy.nrf.presentation

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eddy.nrf.R
import com.eddy.nrf.presentation.ui.theme.EndColor
import com.eddy.nrf.presentation.ui.theme.Primary
import com.eddy.nrf.presentation.ui.theme.StartColor


//@Preview
@Composable
fun BikeScreen() {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Primary)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
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
            Speed()
            GradientStrokeCircle()

            // PAS
            Pas()
        }
    }
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when the composable is disposed
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}


@Preview(showBackground = true)
@Composable
fun Pas(select: Int = 2) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("PAS")
        repeat(3) { index ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(width = 30.dp, height = 45.dp)
                        .padding(8.dp)
                        .background(Color.Gray)
                )
                if (index == select) {
                    LeftPointingTriangle(
                        modifier = Modifier.size(25.dp),
                    )
                } else {
                    Spacer(modifier = Modifier.size(25.dp))
                }
            }
        }
        Text(select.toString())
    }
}


@Preview(showBackground = true, widthDp = 200, heightDp = 200)
@Composable
fun Speed(speed: Float = 22.5f) {
    // Speed
    Box(
        modifier = Modifier
            .size(200.dp)
            .clip(CircleShape)
            .background(brush = Brush.verticalGradient(listOf(StartColor, EndColor)))
            .border(width = 1.dp, color = Color.Black, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Speed")
            Text(speed.toString(), style = MaterialTheme.typography.headlineMedium)
            Text("km/h")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GradientStrokeCircle(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(Color.Red, Color.Blue),
                start = Offset(0f, 0f),
                end = Offset(canvasWidth, canvasHeight)
            ),
            center = Offset(canvasWidth / 2, canvasHeight / 2),
            radius = (canvasWidth - 4) / 2,
            style = Stroke(width = 4F)
        )
    }
}

//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    NRFTheme {
//    }
//}