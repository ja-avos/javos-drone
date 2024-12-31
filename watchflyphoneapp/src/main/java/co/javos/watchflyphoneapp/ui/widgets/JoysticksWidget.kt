package co.javos.watchflyphoneapp.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.javos.watchflyphoneapp.models.RemoteType
import co.javos.watchflyphoneapp.viewmodels.JoysticksViewModel

class JoysticksWidget {

    @Composable
    fun VirtualJoysticks(viewModel: JoysticksViewModel?, onClick: () -> Unit) {

        val virtualSticks = viewModel?.sticksState?.collectAsState()?.value

        val color = when (virtualSticks?.type) {
            RemoteType.REMOTE_CONTROLLER -> Color.Blue
            RemoteType.WATCH -> Color.Green
            else -> Color.Gray
        }

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
                VirtualStick(70.dp, virtualSticks?.left?.x ?: 0, virtualSticks?.left?.y ?: 0, color)
                VirtualStick(70.dp, virtualSticks?.right?.x ?: 0, virtualSticks?.right?.y ?: 0, color)
            }
        }
    }

    @Composable
    fun VirtualStick(size: Dp, x: Int, y: Int, color: Color) {

        val drawableSize = (size.value - 20)/2
        val realX = drawableSize * (x/100.0)
        val realY = drawableSize * (y/100.0) * -1

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
                    .absoluteOffset(
                        realX.dp, realY.dp
                    )
                    .background(color, CircleShape)
            )
        }
    }
}
