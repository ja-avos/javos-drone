package co.javos.watchfly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import co.javos.watchfly.ui.widgets.AltitudeWidget
import co.javos.watchfly.viewmodels.AltitudeControlViewModel

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun AltitudeControlView(viewModel: AltitudeControlViewModel? = null, cursorOffset: Offset = Offset.Zero) {

    val droneStatus = viewModel?.droneStatus?.collectAsState()?.value
    val altitude = droneStatus?.altitude ?: 0F
    val ascending = droneStatus?.verticalSpeed ?: 0F

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            AltitudeWidget(
                altitude, ascending,
                modifier = Modifier
                    .weight(1F)
            )
            VerticalDivider()
            Column(
                Modifier
                    .weight(1F)
                    .fillMaxHeight()
                    .drawBehind {
                        drawLine(
                            color = Color.Magenta,
                            start = Offset(0F, size.height / 2),
                            end = Offset(size.width, size.height / 2),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(7F, 7F), 0F)
                        )
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .weight(1F)
                        .background(color = if (ascending > 0) Color.Magenta.copy(alpha = 0.1F) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add, "Increase", modifier = Modifier
                            .size(40.dp)
                            .offset((-7.5).dp, 7.5.dp)
                    )
                }
                Box(
                    Modifier
                        .fillMaxSize()
                        .weight(1F)
                        .background(color = if (ascending < 0) Color.Magenta.copy(alpha = 0.1F) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Remove, "Decrease", modifier = Modifier
                            .size(40.dp)
                            .offset((-7.5).dp, (-7.5).dp)
                    )
                }

            }

        }
    }
}