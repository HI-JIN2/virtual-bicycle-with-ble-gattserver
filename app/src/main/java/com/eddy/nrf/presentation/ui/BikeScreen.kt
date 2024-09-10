package com.eddy.nrf.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eddy.nrf.R
import com.eddy.nrf.presentation.ui.component.AnimatedImage
import com.eddy.nrf.presentation.ui.component.Pas
import com.eddy.nrf.presentation.ui.component.Speed
import com.eddy.nrf.presentation.ui.theme.NRFTheme
import com.eddy.nrf.presentation.ui.theme.Primary


@Composable
fun BikeScreen(
    bikeViewModel: BikeViewModel = viewModel()) {

    val bikeUiState by bikeViewModel.uiState.collectAsState()

    var selected by remember { mutableIntStateOf(0) } // 선택된 항목을 저장

    Column(
        modifier = Modifier.fillMaxSize() // 화면 전체 크기를 채움
    ) {
        Box(
            modifier = Modifier
                .weight(1f)  // 남은 공간을 모두 차지
                .fillMaxWidth()  // 너비는 부모의 너비로 채움
                .background(Primary)
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                AnimatedImage()

                Speed(200.dp, bikeUiState.speed)

                Pas(modifier = Modifier.padding(end = 10.dp),
                    select = selected.toFloat(), //사용자로 하여금 바꾸고 싶은 값은 uistate로 하면 안됨
                    onSelect = {newIndex -> selected= newIndex} )
            }
        }
        Box(
            modifier = Modifier
                .height(50.dp)  // 고정된 높이 30dp
                .fillMaxWidth()  // 너비는 부모의 너비로 채움
                .background(Color.White)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),  // Row가 부모의 너비를 가득 채움
                horizontalArrangement = Arrangement.spacedBy(
                    150.dp,
                    Alignment.CenterHorizontally
                ),  // 요소들을 중앙에 정렬하고 16dp 간격 추가
                verticalAlignment = Alignment.CenterVertically
            )  // 세로 방향으로 중앙 정렬
            {
                Text(text = "08:30 PM", modifier = Modifier)
                Text(text = "ODO")
                Text(text = bikeUiState.distance.toString())

                Row(
                    verticalAlignment = Alignment.CenterVertically // 세로 방향으로 중앙 정렬

                ) {
                    Text(text = "100%")
                    Image(
                        painter = painterResource(id = R.drawable.img_battery),  // 리소스 이미지 사용
                        contentDescription = "Example Image",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
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