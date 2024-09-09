package com.eddy.nrf.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LeftPointingTriangle(
    modifier: Modifier = Modifier,
    color: Color = Color.Gray,
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(width * 0.8f, height * 0.1f)  // Top right
            lineTo(width * 0.8f, height * 0.9f)  // Bottom right
            lineTo(width * 0.2f, height * 0.5f)  // Middle left
            close()  // Back to start
        }

        // Fill the triangle
        drawPath(
            path = path,
            color = color
        )
    }
}

// Usage example
@Preview(showBackground = true)
@Composable
fun TriangleExample() {
    LeftPointingTriangle(
        modifier = Modifier.size(100.dp),
    )
}