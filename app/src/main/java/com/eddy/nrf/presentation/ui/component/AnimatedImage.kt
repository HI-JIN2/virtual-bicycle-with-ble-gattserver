package com.eddy.nrf.presentation.ui.component

import android.util.Log
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
    val frameCount = 115 //총 프레임은 115이지만 59면 한 사이클을 돈다
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

    // speed에 따른 frameDuration 계산
    val frameDuration = (baseDuration * 3 / (speed / 3).coerceIn(1f, 9f)).toLong()

    // 현재 프레임 상태
    val currentFrame = remember { mutableStateOf(0) }

    // 애니메이션 업데이트를 위한 LaunchedEffect
    LaunchedEffect(speed) { // speed를 키로 사용
        while (true) {
            // 프레임 업데이트
            currentFrame.value = (currentFrame.value + 1) % frameCount
            // 속도에 따른 프레임 지속시간 조정
            delay(frameDuration)

            Log.d("laun", "${currentFrame.value} $speed $frameDuration")

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