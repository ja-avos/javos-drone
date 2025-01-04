package co.javos.watchfly.ui.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

@Composable
fun CompassFrame(angle: Float = 0F) {
    val textMeasurer = rememberTextMeasurer()
    val cardinalityStyle = TextStyle(
        color = Color.White,
        background = Color.Black,
        fontWeight = FontWeight.Bold,
        fontSize = TextUnit(
            3F,
            TextUnitType.Em
        )
    )
    val textN = textMeasurer.measure(
        style = cardinalityStyle.merge(color = Color.Red),
        text = "N"
    )
    val textE = textMeasurer.measure(
        style = cardinalityStyle,
        text = "E"
    )
    val textS = textMeasurer.measure(
        style = cardinalityStyle,
        text = "S"
    )
    val textW = textMeasurer.measure(
        style = cardinalityStyle,
        text = "W"
    )

    val rotation = ((angle.toFloat() + 2) * 180) % 360

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .rotate(rotation)
    ) {
        drawCircle(
            color = Color.Black,
            radius = (size.width / 2)
        )
        val degrees = 360
        var divisions = 40
        for (i in 0..degrees step degrees /
                divisions) {
            rotate((i).toFloat(), pivot = Offset(size.width / 2, size.height / 2)) {
                drawLine(
                    color = Color.White,
                    start = Offset(size.width / 2, size.height),
                    end = Offset(size.width / 2, 0F),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
        drawCircle(
            color = Color.Black,
            radius = (size.width / 2) - 15,
        )
        divisions = 4
        for (i in 0..degrees step degrees /
                divisions) {
            rotate((i + 45).toFloat(), pivot = Offset(size.width / 2, size.height / 2)) {
                drawLine(
                    color = Color.White,
                    start = Offset(size.width / 2, size.height),
                    end = Offset(size.width / 2, 0F),
                    strokeWidth = 1.dp.toPx()
                )
            }
        }
        drawCircle(
            color = Color.Black,
            radius = (size.width / 2) - 40,
        )
        drawText(
            textN,
            topLeft = Offset((size.width - textN.size.width) / 2, 0F),
        )
        drawText(
            textW,
            topLeft = Offset(3.dp.toPx(), (size.height - textW.size.height) / 2),
        )
        drawText(
            textS,
            topLeft = Offset(
                (size.width - textS.size.width) / 2,
                size.height - textS.size.height
            ),
        )
        drawText(
            textE,
            topLeft = Offset(
                size.width - textE.size.width - 3.dp.toPx(),
                (size.height - textE.size.height) / 2
            ),
        )
    }
}