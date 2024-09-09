package com.eddy.nrf.presentation

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
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eddy.nrf.R
import com.eddy.nrf.presentation.component.Pas
import com.eddy.nrf.presentation.component.Speed
import com.eddy.nrf.presentation.ui.theme.NRFTheme
import com.eddy.nrf.presentation.ui.theme.Primary


//@Preview
@Composable
fun BikeScreen() {
    Column(
        modifier = Modifier.fillMaxSize() // 화면 전체 크기를 채움
    ) {
        Box(
            modifier = Modifier
                .weight(1f)  // 남은 공간을 모두 차지
                .fillMaxWidth()  // 너비는 부모의 너비로 채움
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

                Speed(200.dp, 22.5)

                Pas(1)
            }
        }
        Box(
            modifier = Modifier
                .height(30.dp)  // 고정된 높이 30dp
                .fillMaxWidth()  // 너비는 부모의 너비로 채움
                .background(Color.White)
                .padding(10.dp)
        ) {
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