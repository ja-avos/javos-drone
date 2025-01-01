package co.javos.watchfly.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Watch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import co.javos.watchfly.MainActivity
import co.javos.watchfly.R
import co.javos.watchfly.models.DroneState
import co.javos.watchfly.ui.widgets.ConnectingWidget
import co.javos.watchfly.viewmodels.DroneStatusViewModel

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun ConnectingView(viewModel: DroneStatusViewModel? = null) {

    val droneStatus = viewModel?.droneStatus?.collectAsState()?.value?.state ?: DroneState.NO_REMOTE
    val isPhoneConnected = viewModel?.isPhoneConnected?.collectAsState()?.value ?: false
    val deviceIcon: ImageVector =
        if (!isPhoneConnected)
            Icons.Default.Smartphone
        else
            when (droneStatus) {
                DroneState.NO_REMOTE -> {
                    ImageVector.vectorResource(R.drawable.stadia_controller_24dp)
                }

                DroneState.NO_DRONE -> {
                    ImageVector.vectorResource(R.drawable.ic_quadcopter)
                }

                else -> {
                    Icons.Default.QuestionMark
                }
            }

    Box(
        modifier = Modifier
            .background(color = Color.Black)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    )
    {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                deviceIcon,
                "Phone / Remote / Drone icon",
                modifier = Modifier.size(46.dp)
            )
            ConnectingWidget()
            Icon(
                Icons.Default.Watch,
                "Watch icon",
                modifier = Modifier.size(46.dp)
            )
        }
    }
}