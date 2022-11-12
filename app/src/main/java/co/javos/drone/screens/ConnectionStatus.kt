package co.javos.drone.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup

@Composable
fun ConnectionStatusPopup() {
    var aircraftConnected by remember { mutableStateOf(false) }
            Surface(shape = RoundedCornerShape(6.dp), shadowElevation = 24.dp) {
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
                }

            }
        }