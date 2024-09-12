package com.eddy.nrf.presentation.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AnimatedImage(
    modifier: Modifier = Modifier,
    speed: Float = 1f // 1f는 기본 속도, 2f는 2배 빠르게, 0.5f는 절반 속도
) {
    val frameCount = 115
    val baseDuration = 1000 / 24 // 기본 프레임 지속 시간 (초당 24프레임)
    val context = LocalContext.current

    // 이미지 리스트
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
                durationMillis = (frameCount * baseDuration / speed).toInt(),
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = ""
    )

    Image(
        painter = images[currentFrame],
        contentDescription = null,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun AnimatedImagePreview() {
    AnimatedImage(speed = 1f)
}