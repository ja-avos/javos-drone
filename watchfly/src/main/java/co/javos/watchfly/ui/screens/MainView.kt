package co.javos.watchfly.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Text
import co.javos.watchfly.models.ControlMode
import co.javos.watchfly.models.DroneState
import co.javos.watchfly.models.DroneStatus
import co.javos.watchfly.ui.widgets.BlinkingWidget
import co.javos.watchfly.ui.widgets.DroneStatusWidget
import co.javos.watchfly.viewmodels.MainViewModel

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun MainView(
    viewModel: MainViewModel? = null
) {
    val controlMode = viewModel?.controlMode?.collectAsState()?.value ?: ControlMode.RPY
    val droneStatus = viewModel?.droneStatus?.collectAsState()?.value ?: DroneStatus()
    val controlText = if (controlMode == ControlMode.RPY) "PY" else "RPY"
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .rotate(45F)
        ) {
            Column {
                Row(modifier = Modifier.weight(1F)) {
                    BlinkingWidget(false,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1F)
                            .clickable(enabled = droneStatus.state == DroneState.FLYING) {
                                viewModel?.togglePYRPYControlMode()
                            }) {
                        Text(
                            "$controlText\nControls",
                            modifier = Modifier.rotate(-45F),
                            style = LocalTextStyle.current.merge(
                                fontSize = TextUnit(2.3F, TextUnitType.Em),
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                    VerticalDivider(thickness = 1.dp)
                    BlinkingWidget(
                        enabled = false,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1F)
                            .clickable {
                                viewModel?.stopDrone()
                            },
                    ) {
                        Text(
                            text = "STOP",
                            modifier = Modifier.rotate(-45F),
                            style = LocalTextStyle.current.merge(
                                fontSize = TextUnit(2.3F, TextUnitType.Em),
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
                HorizontalDivider(thickness = 1.dp)
                Row(modifier = Modifier.weight(1F)) {
                    BlinkingWidget(
                        enabled = droneStatus.state == DroneState.GOING_HOME,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1F)
                            .clickable(enabled = droneStatus.state == DroneState.FLYING) {
                                viewModel?.droneRTH()
                            },
                    ) {
                        Text(
                            text = "RTH",
                            modifier = Modifier.rotate(-45F),
                            style = LocalTextStyle.current.merge(
                                fontSize = TextUnit(2.3F, TextUnitType.Em),
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                    VerticalDivider(thickness = 1.dp)
                    BlinkingWidget(
                        enabled = droneStatus.state == DroneState.LANDING,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1F)
                            .clickable(enabled = droneStatus.state == DroneState.FLYING) {
                                viewModel?.land()
                            },
                    ) {

                        Text(
                            text = "Land",
                            modifier = Modifier.rotate(-45F),
                            style = LocalTextStyle.current.merge(
                                fontSize = TextUnit(2.3F, TextUnitType.Em),
                                textAlign = TextAlign.Center
                            )
                        )

                    }
                }
            }
        }
        DroneStatusWidget(viewModel?.droneStatus?.collectAsState()?.value)
    }
}