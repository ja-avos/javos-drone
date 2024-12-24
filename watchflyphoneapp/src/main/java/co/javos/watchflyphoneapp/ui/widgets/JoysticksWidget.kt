package co.javos.watchflyphoneapp.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class JoysticksWidget {

    @Composable
    fun VirtualJoysticks(onClick: () -> Unit) {
        Box(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .shadow(8.dp, RoundedCornerShape(80.dp))
                .clickable(onClick = onClick)
                .clip(RoundedCornerShape(80.dp))
                .size(180.dp, 90.dp)
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                VirtualStick(70.dp)
                VirtualStick(70.dp)
            }
        }
    }

    @Composable
    fun VirtualStick(size: Dp) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .border(1.dp, Color.Gray, CircleShape)
                .size(size),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color.Blue, CircleShape)
            )
        }
    }
}
