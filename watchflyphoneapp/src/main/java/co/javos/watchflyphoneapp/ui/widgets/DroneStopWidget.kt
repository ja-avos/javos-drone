package co.javos.watchflyphoneapp.ui.widgets

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.javos.watchflyphoneapp.viewmodels.CameraControlsViewModel

class DroneStopWidget {
    @Composable
    fun StopButton(viewModel: CameraControlsViewModel?) {
        val enabled = viewModel?.isDroneConnected?.collectAsState()?.value ?: false
        val onClick = {
            viewModel?.stopDrone()
        }
        IconButton(
            enabled = enabled,
            onClick = {
                viewModel?.stopDrone()
            },
            colors = IconButtonColors(
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.Gray,
                containerColor = Color.White,
                contentColor = Color.Red
            ),
            modifier = Modifier
                .size(60.dp)
                .shadow(6.dp, CircleShape)
        ) {
            Icon(
                Icons.Default.Stop,
                contentDescription = "Stop",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
                    .border(2.dp, if (enabled) Color.Red else Color.Gray, CircleShape)
                    .padding(1.dp)
            )
        }
    }
}
