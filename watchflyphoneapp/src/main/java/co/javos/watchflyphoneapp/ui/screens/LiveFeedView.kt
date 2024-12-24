package co.javos.watchflyphoneapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import co.javos.watchflyphoneapp.models.DroneState
import co.javos.watchflyphoneapp.ui.widgets.NoConnectionStatusWidget

class LiveFeedView {

    @Composable
    fun LiveFeed(droneStatus: DroneState, idDrawable: Int) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            if (droneStatus == DroneState.NO_REMOTE || droneStatus == DroneState.NO_DRONE)
                NoConnectionStatusWidget().NoConnectionStatus(droneStatus)
            else
                Image(
                    painter = painterResource(idDrawable),
                    contentDescription = "Drone photo",
                    contentScale = ContentScale.Crop
                )
        }
    }
}
