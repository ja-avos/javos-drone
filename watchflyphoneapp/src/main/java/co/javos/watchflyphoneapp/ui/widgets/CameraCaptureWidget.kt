package co.javos.watchflyphoneapp.ui.widgets

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class CameraCaptureWidget {
    @Composable
    fun CaptureButton(enabled: Boolean, onClick: () -> Unit) {
        val buttonColor = if (enabled) Color.White else Color.Gray
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .clickable(enabled = enabled, onClick = onClick)
                .border(3.dp, buttonColor, CircleShape)
                .size(80.dp)
                .shadow(6.dp, CircleShape)
                .padding(6.dp)
                .drawBehind {
                    drawCircle(
                        buttonColor,
                        radius = size.width / 2,
                        center = Offset(size.width / 2, size.height / 2)
                    )
                })
    }
}
