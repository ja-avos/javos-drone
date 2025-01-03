package co.javos.watchflyphoneapp.ui.screens

import android.media.AudioManager
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.VibratorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.javos.watchflyphoneapp.R
import co.javos.watchflyphoneapp.models.Alert
import co.javos.watchflyphoneapp.models.AlertType
import co.javos.watchflyphoneapp.models.DroneState
import co.javos.watchflyphoneapp.ui.widgets.AlertsPopup
import co.javos.watchflyphoneapp.ui.widgets.DroneStatusWidget
import co.javos.watchflyphoneapp.ui.widgets.JoysticksWidget
import co.javos.watchflyphoneapp.ui.widgets.ScreenPreviewWidget
import co.javos.watchflyphoneapp.ui.widgets.WatchChatWidget
import co.javos.watchflyphoneapp.viewmodels.AlertsViewModel
import co.javos.watchflyphoneapp.viewmodels.CameraControlsViewModel
import co.javos.watchflyphoneapp.viewmodels.DroneStatusViewModel
import co.javos.watchflyphoneapp.viewmodels.JoysticksViewModel
import co.javos.watchflyphoneapp.viewmodels.LiveFeedViewModel
import co.javos.watchflyphoneapp.viewmodels.MapViewModel
import co.javos.watchflyphoneapp.viewmodels.WatchButtonViewModel

class MainView {
    @Preview(device = "spec:width=1280dp,height=800dp,dpi=240", uiMode = 0)
    @Composable
    fun MainScreen(
        navController: NavController? = null,
        audioManager: AudioManager? = null,
        vibratorManager: VibratorManager? = null,
        droneStatusViewModel: DroneStatusViewModel? = null,
        watchButtonViewModel: WatchButtonViewModel? = null,
        liveFeedViewModel: LiveFeedViewModel? = null,
        mapViewModel: MapViewModel? = null,
        cameraControlsViewModel: CameraControlsViewModel? = null,
        joysticksViewModel: JoysticksViewModel? = null,
        alertsViewModel: AlertsViewModel? = null
    ) {
        val dronePhotos = listOf(
            R.drawable.drone_bacata,
            R.drawable.drone_solar,
            R.drawable.drone_laguna,
            R.drawable.drone_playa
        )
        val showMap = remember { mutableStateOf(true) }
        val droneStatus = remember { mutableStateOf(DroneState.NO_REMOTE) }
        val idDronePhoto = remember { mutableIntStateOf(dronePhotos.first()) }
        val showAlert = remember { mutableStateOf(false) }

        Box {
            if (showAlert.value) {
                audioManager?.playSoundEffect(
                    AudioManager.FX_KEYPRESS_INVALID,
                    1F
                )
                val vibrateEffect =
                    VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)
                vibratorManager?.vibrate(
                    CombinedVibration.createParallel(vibrateEffect)
                )
                AlertsPopup(
                    alertsViewModel,
                    onDismiss = { showAlert.value = false }
                )
            }

            if (showMap.value)
                MapView().Map(mapViewModel)
            else LiveFeedView().LiveFeed(
                liveFeedViewModel,
                LocalContext.current
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp, 0.dp, 15.dp, 0.dp)
                    .safeDrawingPadding(),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1F),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    DroneStatusWidget().DroneStatus(
                        droneStatusViewModel,
                        onClick = {
                            idDronePhoto.intValue =
                                dronePhotos[(dronePhotos.indexOf(idDronePhoto.intValue) + 1) % dronePhotos.size]
                        })
                    WatchChatWidget().WatchButton(navController, watchButtonViewModel)
                    ScreenPreviewWidget().ScreenPreview(onTap = {
                        showMap.value = !showMap.value
                    }) {
                        if (!showMap.value)
                            MapView().Map(mapViewModel)
                        else LiveFeedView().LiveFeed(
                            liveFeedViewModel,
                            LocalContext.current
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1F),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    JoysticksWidget().VirtualJoysticks(
                        joysticksViewModel,
                        onClick = {
                        droneStatus.value =
                            DroneState.entries.toTypedArray()[(droneStatus.value.ordinal + 1) % DroneState.entries.size]
                    })
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1F),
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.End
                ) {
                    CameraControlView().ActionsWidget(
                        showMapActions = showMap.value,
                        mapViewModel = mapViewModel,
                        cameraControlsViewModel = cameraControlsViewModel,
                        alertsViewModel = alertsViewModel,
                        diagnosticsOnClick = {
                            showAlert.value = true
                        }
                    )
                }
            }
        }
    }

}
