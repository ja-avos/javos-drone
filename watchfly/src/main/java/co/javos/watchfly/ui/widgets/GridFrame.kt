package co.javos.watchfly.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.dp

@Composable
fun GridFrame(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .drawBehind {
                val strokeWidth = 0.5.dp.toPx()
                val lines = 10;
                val height = size.height
                val width = size.width
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(7F, 7F), 0F)

                for (i in 0..lines) {
                    val y = height * i / lines
                    drawLine(
                        color = Color.White,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = strokeWidth,
                        pathEffect = pathEffect
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(y, 0f),
                        end = Offset(y, height),
                        strokeWidth = strokeWidth,
                        pathEffect = pathEffect
                    )
                }

                drawLine(
                    color = Color.White,
                    start = Offset(0f, height / 2),
                    end = Offset(width, height / 2),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = Color.White,
                    start = Offset(width / 2, 0f),
                    end = Offset(width / 2, height),
                    strokeWidth = 1.dp.toPx()
                )
            },
        contentAlignment = Alignment.Center
    ) {
        content()
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = Color.White,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = Color.White,
                start = Offset(size.width / 2, 0f),
                end = Offset(size.width / 2, size.height),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}