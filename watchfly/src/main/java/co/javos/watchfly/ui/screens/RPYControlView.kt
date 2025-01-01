package co.javos.watchfly.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Text
import co.javos.watchfly.ui.widgets.CompassFrame

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun RPYControlView(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0) {

    Box {
        CompassFrame(z)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = Color.Green,
                start = Offset(40F, size.height / 2),
                end = Offset(size.width - 40F, size.height / 2),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(7F, 7F), 0F)
            )
            drawCircle(
                color = Color.White, radius = 22.dp.toPx(),
                style = Stroke(width = 2.5F)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "X: ${String.format("%.2f", x)}", fontSize = TextUnit(
                    1.5F,
                    TextUnitType.Em
                )
            )
            Text(
                text = "Y: ${String.format("%.2f", y)}", fontSize = TextUnit(
                    1.5F,
                    TextUnitType.Em
                )
            )
            Text(
                text = "Z: ${String.format("%.2f", z)}", fontSize = TextUnit(
                    1.5F,
                    TextUnitType.Em
                )
            )
        }
    }

}