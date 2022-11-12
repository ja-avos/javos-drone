package co.javos.drone.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun JetpackSandboxScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Jetpack Sandbox")
            PopupTest()
        }
    }
}

@Composable
fun PopupTest() {
    var popupControl by remember { mutableStateOf(false) }
    var aircraftConnected by remember { mutableStateOf(false) }
    TextButton(onClick = { popupControl = !popupControl }) {
        Text("Open normal popup")
    }

    if (popupControl) {
        Popup(alignment = Alignment.Center) {
            // Composable content to be shown in the Popup
            Surface(shape = RoundedCornerShape(6.dp), modifier = Modifier
                .padding(top = 24.dp, end = 16.dp), shadowElevation = 24.dp) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(all = 24.dp)) {
                    Text(text = "Aircraft Connection", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    if (aircraftConnected) {
                        TextButton(onClick = { aircraftConnected = false }) {
                            Text("Disconnect")
                        }
                    } else {
                        TextButton(onClick = { aircraftConnected = true }) {
                            Text("Connect")
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    IconButton(onClick = { popupControl = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

            }
        }
    }
}