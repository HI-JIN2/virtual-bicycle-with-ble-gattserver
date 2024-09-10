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

@Preview(showBackground = true)
@Composable
fun Pas(
    modifier: Modifier = Modifier,
    select: Float = 2f,
    onSelect: (Int) -> Unit = {}// 선택 콜백
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "PAS",
            modifier = Modifier
                .align(Alignment.Start)
                .padding(5.dp)  // 왼쪽 정렬
        )
        repeat(3) { index ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (index == select.toInt()) {
                    Box(
                        modifier = Modifier
                            .size(width = 40.dp, height = 60.dp)
                            .padding(8.dp)
                            .background(Color.Gray)
                            .clickable{
                                Log.d("TAG", "Pas: $index")
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
                            .background(Color.White)
                            .clickable { onSelect(index)
                                Log.d("TAG", "Pas:selected $index")
                            } // 클릭 이벤트로 선택 변경
                    )
                    Spacer(modifier = Modifier.size(30.dp))

                }

            }
        }
        Text(
            select.toInt().toString(),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(10.dp), // 왼쪽 정렬
        )
    }
}