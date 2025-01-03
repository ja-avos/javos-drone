package co.javos.watchflyphoneapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import co.javos.watchflyphoneapp.models.DroneState
import co.javos.watchflyphoneapp.ui.widgets.CameraCaptureWidget
import co.javos.watchflyphoneapp.ui.widgets.CenterLocationWidget
import co.javos.watchflyphoneapp.ui.widgets.DiagnosticsButton
import co.javos.watchflyphoneapp.ui.widgets.DroneLocationWidget
import co.javos.watchflyphoneapp.ui.widgets.DroneStopWidget
import co.javos.watchflyphoneapp.viewmodels.AlertsViewModel
import co.javos.watchflyphoneapp.viewmodels.CameraControlsViewModel
import co.javos.watchflyphoneapp.viewmodels.MapViewModel

class CameraControlView {
    @Preview(device = "spec:width=411dp,height=891dp", showBackground = true)
    @Composable
    fun ActionsWidget(
        showMapActions: Boolean = true,
        mapViewModel: MapViewModel? = null,
        cameraControlsViewModel: CameraControlsViewModel? = null,
        alertsViewModel: AlertsViewModel? = null,
        diagnosticsOnClick: () -> Unit = {}
    ) {
        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(modifier = Modifier.weight(1F)) {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DroneStopWidget().StopButton(cameraControlsViewModel)
                    DiagnosticsButton(alertsViewModel, onClick = diagnosticsOnClick)

                }
            }
            Box(modifier = Modifier.weight(1F), contentAlignment = Alignment.Center) {
                CameraCaptureWidget().CaptureButton(cameraControlsViewModel)
            }
            Box(modifier = Modifier.weight(1F)) {
                if (showMapActions) Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    CenterLocationWidget().CenterLocationButton(
                        viewModel = mapViewModel
                    )
                    DroneLocationWidget().DroneLocationButton(
                        viewModel = mapViewModel
                    )
                }
            }
        }
    }
}
