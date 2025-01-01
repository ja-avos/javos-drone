package co.javos.watchfly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Text
import co.javos.watchfly.models.DroneState
import co.javos.watchfly.ui.widgets.BlinkingWidget
import co.javos.watchfly.viewmodels.MainViewModel

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun LandedView(viewModel: MainViewModel? = null) {

    val droneState = viewModel?.droneStatus?.collectAsState()?.value?.state ?: DroneState.MOTORS_OFF

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1F)
                .background(
                    color = if (droneState == DroneState.MOTORS_OFF)
                        Color.Black
                    else
                        Color.White
                )
                .clickable {
                    viewModel?.toggleMotors()
                }, contentAlignment = Alignment.Center
        ) {
            Text(
                "${if (droneState == DroneState.MOTORS_OFF) "Start" else "Stop"}\nMotors",
                textAlign = TextAlign.Center, fontSize = TextUnit(3F, TextUnitType.Em),
                color = if (droneState == DroneState.MOTORS_OFF)
                    Color.White
                else
                    Color.Black
            )
        }
        HorizontalDivider(color = Color.White, thickness = 1.dp)
        BlinkingWidget(enabled = droneState == DroneState.TAKING_OFF,
            modifier = Modifier
                .fillMaxSize()
                .weight(1F)
                .clickable {
                    viewModel?.takeOff()
                }
        ) {
            Text(
                "Take\nOff",
                textAlign = TextAlign.Center,
                fontSize = TextUnit(3F, TextUnitType.Em)
            )
        }
    }
}
