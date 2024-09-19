package com.eddy.nrf.presentation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay

@Composable
fun AnimatedImage(
    modifier: Modifier = Modifier,
    speed: Float = 1f // Speed value (controls the animation speed)
) {
    val frameCount = 115
    val baseDuration = 1000 / 24 // Base duration for 24 fps
    val context = LocalContext.current

    // Image list
    val images = (1..frameCount).map { frameNumber ->
        val formattedFrameNumber = String.format("%03d", frameNumber)
        painterResource(
            id = context.resources.getIdentifier(
                "out_$formattedFrameNumber",
                "drawable",
                context.packageName
            )
        )
    }

    val frameDuration = (baseDuration / (speed / 10).coerceIn(0.1f, 3f)).toInt()

    // speed가 바뀔 때마다 새로운 애니메이션을 생성
    val currentFrame = remember { mutableStateOf(0) }

    LaunchedEffect(speed) {
        while (true) {
            for (i in 0 until frameCount) { //
                currentFrame.value = i
                delay(frameDuration.toLong())
            }
        }
    }

    Image(
        painter = images[currentFrame.value],
        contentDescription = null,
        modifier = modifier
    )

}


@Preview(showBackground = true)
@Composable
fun AnimatedImagePreview() {
    AnimatedImage(speed = 1f)
}