package co.javos.watchfly.ui.widgets

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.AnchorType
import androidx.wear.compose.foundation.CurvedDirection
import androidx.wear.compose.foundation.CurvedLayout
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.foundation.curvedRow
import androidx.wear.compose.material3.curvedText

@Composable
fun CurvedText(
    anchor: Float,
    color: Color,
    text: String,
    angularDirection: CurvedDirection.Angular? = null
) {
    CurvedLayout(
        anchor = anchor,
        anchorType = AnchorType.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        curvedRow() {
            curvedText(
                text = text,
                angularDirection = angularDirection,
                style = CurvedTextStyle(
                    fontSize = 13.sp,
                    color = color,
                    background = Color.Black
                )
            )
        }
    }
}