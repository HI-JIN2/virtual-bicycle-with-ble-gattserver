package com.eddy.nrf.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.eddy.nrf.presentation.ui.theme.Gray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val colors = SliderDefaults.colors(
        thumbColor = Color.Blue,
        activeTrackColor = Color.Blue.copy(alpha = 0.5f),
        inactiveTrackColor = Color.LightGray
    )

    Slider(
        colors = colors,
        interactionSource = interactionSource,
        onValueChangeFinished = onValueChangeFinished,
        steps = steps,
        valueRange = valueRange,
        enabled = enabled,
        value = value,
        onValueChange = onValueChange,
        thumb = {
            // 네모난 Thumb
            Box(
                modifier = Modifier
                    .size(30.dp) // 네모 크기
                    .background(Color.Red, RectangleShape) // 색상과 모양
            )
        },
        modifier = Modifier
            .graphicsLayer {
                rotationZ = 270f
                transformOrigin = TransformOrigin(0f, 0f)
            }
            .layout { measurable, constraints ->
                val placeable = measurable.measure(
                    Constraints(
                        minWidth = constraints.minHeight,
                        maxWidth = constraints.maxHeight,
                        minHeight = constraints.minWidth,
                        maxHeight = constraints.maxWidth
                    )
                )
                layout(placeable.height, placeable.width) {
                    placeable.place(-placeable.width, 0)
                }
            }
            .width(160.dp) // 슬라이더의 너비 (세로 방향으로 표시될 때의 높이)
            .fillMaxHeight(),
        track = { sliderState ->
            CustomTrack(
                colors = colors.copy(
                    thumbColor = Color.Red, // 네모난 Thumb의 색상
                    activeTrackColor = Color.Blue,
                    inactiveTrackColor = Gray
                ),
                enabled = enabled,
                sliderState = sliderState
            )

        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTrack(
    colors: SliderColors,
    enabled: Boolean,
    sliderState: SliderState
) {
    // 트랙을 커스터마이징하여 넓은 트랙 생성
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp) // 원하는 트랙 두께
            .background(
                Color.Gray
            )
    )
}

@Preview(showBackground = true)
@Composable
fun VerticalSliderPreview() {
    var value by remember { mutableStateOf(0f) }
    VerticalSlider(
        value = value,
        onValueChange = {
            value = it
        },
        modifier = Modifier

    )
}