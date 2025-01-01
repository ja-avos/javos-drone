package co.javos.watchfly.ui.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Wifi1Bar
import androidx.compose.material.icons.filled.Wifi2Bar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import kotlinx.coroutines.delay

@Composable
fun ConnectingWidget() {
    val connectionIconList = listOf(
        Icons.Default.Wifi1Bar,
        Icons.Default.Wifi2Bar,
        Icons.Default.Wifi
    )

    var connectionIcon = remember { mutableStateOf(Icons.Default.Wifi) }

    // Change every second to the next icon in the list
    LaunchedEffect(Unit) {
        while (true) {
            connectionIcon.value =
                connectionIconList[(connectionIconList.indexOf(connectionIcon.value) + 1) % connectionIconList.size]
            delay(300)
        }
    }

    Icon(
        connectionIcon.value,
        "Connection icon",
        modifier = Modifier.size(46.dp)
    )


}