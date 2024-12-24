package co.javos.watchflyphoneapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import co.javos.watchflyphoneapp.models.DroneState
import co.javos.watchflyphoneapp.ui.widgets.CameraCaptureWidget
import co.javos.watchflyphoneapp.ui.widgets.CenterLocationWidget
import co.javos.watchflyphoneapp.ui.widgets.DroneLocationWidget
import co.javos.watchflyphoneapp.ui.widgets.DroneStopWidget

class CameraControlView {
    @Composable
    fun ActionsWidget(
        status: MutableState<DroneState>,
        showMapActions: Boolean = false,
        onTakePhoto: () -> Unit
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(modifier = Modifier.weight(1F)) {
                DroneStopWidget().StopButton(enabled = status.value == DroneState.FLYING, onClick = {
                    status.value = DroneState.NO_DRONE
                })
            }
            Box(modifier = Modifier.weight(1F), contentAlignment = Alignment.Center) {
                CameraCaptureWidget().CaptureButton(
                    enabled =
                    status.value == DroneState.FLYING, onClick = onTakePhoto
                )
            }
            Box(modifier = Modifier.weight(1F)) {
                if (showMapActions)
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CenterLocationWidget().CenterLocationButton()
                        DroneLocationWidget().DroneLocationButton(
                            enabled =
                            status.value == DroneState.FLYING
                        )
                    }
            }
        }
    }
}
