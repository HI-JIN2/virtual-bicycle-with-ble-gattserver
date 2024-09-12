package com.eddy.nrf.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eddy.nrf.R
import com.eddy.nrf.presentation.ui.component.AnimatedImage
import com.eddy.nrf.presentation.ui.component.Pas
import com.eddy.nrf.presentation.ui.component.Speed
import com.eddy.nrf.presentation.ui.component.VerticalSlider
import com.eddy.nrf.presentation.ui.theme.NRFTheme
import com.eddy.nrf.presentation.ui.theme.Primary
import com.eddy.nrf.presentation.ui.theme.Typography
import kotlinx.coroutines.delay
import java.time.LocalTime


@Composable
fun BikeScreen(
    bikeViewModel: BikeViewModel = BikeViewModel()
) { //uistate를 주입 받는 방법은? 메소드도 파라미터로 넘기고

    val bikeUiState by bikeViewModel.uiState.collectAsStateWithLifecycle() //이걸로해야 값을 계속 관찰할 수 있음~

    var selected by remember { mutableIntStateOf(0) } // 선택된 항목을 저장
    var proportionalFactorSliderValue by remember { mutableStateOf(1f) }
    var targetBatterySliderValue by remember { mutableStateOf(100f) }

    Column(
        modifier = Modifier.fillMaxSize() // 화면 전체 크기를 채움
    ) {
        Box(
            modifier = Modifier
                .weight(1f)  // 남은 공간을 모두 차지
                .fillMaxWidth()  // 너비는 부모의 너비로 채움
                .background(Primary)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedImage(
                    modifier = Modifier
                        .weight(1f),
                    speed = 1f //Todo 기본 속도로 되어있음
                )
                Column {
                    //비례값 조정
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .size(100.dp, 300.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        VerticalSlider(
                            modifier = Modifier.size(20.dp, 5.dp),
                            value = proportionalFactorSliderValue,
                            onValueChange = {
                                proportionalFactorSliderValue = it
                                bikeViewModel.changeProportionalFactor(it)
                            },
                            valueRange = 0f..1.5f,
//                            steps = 2, //간격 없음!
                        )
                        Text(
                            text = "비례값\n${String.format("%.1f", proportionalFactorSliderValue)}",
                            style = Typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    //배터리 조정
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .size(100.dp, 300.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        VerticalSlider(
                            modifier = Modifier.size(20.dp, 10.dp),
                            value = targetBatterySliderValue,
                            onValueChange = {
                                targetBatterySliderValue = it
                                bikeViewModel.changeTargetBattery(it)
                            },
                            valueRange = 0f..100.0F,
                        )
                        Text(
                            text = "배터리 목표값\n${String.format("%.1f", targetBatterySliderValue)}",
                            style = Typography.bodySmall, textAlign = TextAlign.Center
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .background(Color.Black)
                        .fillMaxHeight()
                        .fillMaxWidth(0.3f)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically // 세로 방향으로 중앙 정렬
                    ) {
                        Text(
                            text = String.format("%.1f", bikeUiState.battery) + "%",
                            color = Color.White,
                            style = Typography.bodyMedium,
                        )
                        Image(
                            painter = painterResource(id = R.drawable.img_battery),  // 리소스 이미지 사용
                            contentDescription = "Example Image",
                            colorFilter = ColorFilter.tint(
                                Color.White,
                                BlendMode.SrcIn
                            ) // 색상 및 혼합 모드 설정
                        )
                    }

                    Speed(
                        modifier = Modifier.size(200.dp),
                        speed = bikeUiState.speed
                    )

                    Text(
                        text = "ODO " + bikeUiState.distance.toString() + "km", color = Color.White,
                        style = Typography.bodyMedium
                    )

                    Pas( //todo 글씨 색 수정
                        modifier = Modifier.padding(end = 10.dp),
                        select = bikeUiState.gear,
//                    selected.toFloat(), //사용자로 하여금 바꾸고 싶은 값은 uistate로 하면 안됨
                        onSelect = { newIndex ->
//                        selected = newIndex
                            bikeViewModel.changeGear(newIndex)
                        },
                        viewModel = bikeViewModel
                    )

                }


            }
        }
    }
}

@Composable
fun realTimeClock(): String {
    var currentTime by remember { mutableStateOf(LocalTime.now()) } // 현재 시간 상태

    // LaunchedEffect를 사용하여 일정 시간마다 상태 업데이트
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalTime.now() // 현재 시간 갱신
            delay(1000L) // 1초마다 시간 업데이트
        }
    }

    val timeText = "${currentTime.hour}:${currentTime.minute}:${currentTime.second}"

    return timeText
}


@Preview(
    showBackground = true,
    name = "Galaxy Tab A9+",
    device = "spec:width=1800dp,height=1100dp,dpi=260"
//    widthDp = 1920, heightDp = 1200
)
@Composable
fun BikePreview() {
    NRFTheme {
        BikeScreen()
    }
}