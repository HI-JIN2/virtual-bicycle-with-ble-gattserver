package com.eddy.nrf.presentation.ui.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eddy.nrf.presentation.ui.BikeViewModel
import com.eddy.nrf.presentation.ui.theme.Gray
import com.eddy.nrf.presentation.ui.theme.Green
import com.eddy.nrf.presentation.ui.theme.Orange
import com.eddy.nrf.presentation.ui.theme.Typography
import com.eddy.nrf.presentation.ui.theme.Yellow
import timber.log.Timber

@Preview(showBackground = true, backgroundColor = 0xFFF0EAE2)
@Composable
fun Pas(
    modifier: Modifier = Modifier,
    select: Int = 3,
    onSelect: (Int) -> Unit = {},// 선택 콜백,
    viewModel: BikeViewModel = BikeViewModel()
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "PAS  $select",
            modifier = Modifier
                .padding(5.dp), color = Color.White  // 왼쪽 정렬
            , style = Typography.bodyMedium
        )
        repeat(4) { index ->
            //인덱스는 위에서 아래로, 하지만 파스는 아래부터
            //기어가 3일시에 인덱스는 맨위칸임 그래서 3을 뺌
            val pas = 3 - index
            val color = getColorByPas(pas)

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (pas == select) {
                    Box(
                        modifier = Modifier
                            .size(width = 40.dp, height = 60.dp)
                            .padding(8.dp)
                            .background(color)
                            .clickable {
                                Timber.d("Pas: $index")
//                                viewModel.uiState.value.gear = index.toFloat()
                            }
                    )
                    LeftPointingTriangle(
                        modifier = Modifier.size(30.dp),
                    )

                } else {
                    Box(
                        modifier = Modifier
                            .size(width = 40.dp, height = 60.dp)
                            .padding(8.dp)
                            .background(Color.Gray)
                            .clickable {
//                                onSelect(index)
                                Log.d("TAG", "Pas:selected $index")
                            } // 클릭 이벤트로 선택 변경
                    )
                    Spacer(modifier = Modifier.size(30.dp))
                }
            }
        }
    }
}

fun getColorByPas(pas: Int): Color {
    val color = when (pas) {
        0 -> Green
        1 -> Yellow
        2 -> Orange
        3 -> Color.Red
        else -> Gray // 기본값 설정 (필요에 맞게 설정)
    }
    return color
}