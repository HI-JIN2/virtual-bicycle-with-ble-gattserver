package com.eddy.nrf.presentation.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true)
@Composable
fun AnimatedImage() {

    val frameCount = 115
    val frameDuration = 1000 / 24 // 초당 24프레임
    val context = LocalContext.current

    // List of images
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

    val infiniteTransition = rememberInfiniteTransition(label = "")
    val currentFrame by infiniteTransition.animateValue(
        initialValue = 0,
        targetValue = frameCount - 1,
        typeConverter = Int.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = frameCount * frameDuration,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Image(
        painter = images[currentFrame],
        contentDescription = null,
        modifier = Modifier.size(300.dp)
    )
}