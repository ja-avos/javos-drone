package co.javos.watchfly.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.CurvedDirection
import androidx.wear.compose.material3.Text
import co.javos.watchfly.ui.widgets.CurvedText
import co.javos.watchfly.ui.widgets.GridFrame

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun PYControlView(fullscreen: MutableState<Boolean> = mutableStateOf(false)) {
    val fontSize = 13.sp
    val textStyle = TextStyle(
        fontSize = fontSize,
        color = Color.White,
        background = Color.Black,
        lineHeight = 17.sp
    )
    GridFrame {
        CurvedText(anchor = 270F, color = Color.White, text = "Pitch   Down")
        CurvedText(
            anchor = 94F,
            color = Color.White,
            text = "Pitch   Up",
            angularDirection = CurvedDirection.Angular.Reversed
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Yaw\nLeft", style = textStyle)
            Text("Yaw\nRight", style = textStyle, textAlign = TextAlign.End)
        }
    }

}