package com.eddy.nrf.presentation.component

import androidx.compose.foundation.background
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
import com.eddy.nrf.presentation.LeftPointingTriangle

@Preview(showBackground = true)
@Composable
fun Pas(select: Int = 2) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("PAS")
        repeat(3) { index ->
            Row(verticalAlignment = Alignment.CenterVertically) {

                if (index == select) {
                    Box(
                        modifier = Modifier
                            .size(width = 30.dp, height = 45.dp)
                            .padding(8.dp)
                            .background(Color.Gray)
                    )
                    LeftPointingTriangle(
                        modifier = Modifier.size(25.dp),
                    )

                } else {
                    Box(
                        modifier = Modifier
                            .size(width = 30.dp, height = 45.dp)
                            .padding(8.dp)
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.size(25.dp))

                }
            }
        }
        Text(select.toString())
    }
}