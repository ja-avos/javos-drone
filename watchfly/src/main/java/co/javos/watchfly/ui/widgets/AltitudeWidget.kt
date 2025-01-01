package co.javos.watchfly.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text

@Composable
fun AltitudeWidget(altitude: Float, altitudeDelta: Float, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        BlinkingWidget(enabled = altitudeDelta != 0F, mode = BlinkingMode.OPACITY) {
            Icon(
                if (altitudeDelta > 0) Icons.Default.KeyboardDoubleArrowUp else if (altitudeDelta < 0) Icons.Default.KeyboardDoubleArrowDown else Icons.Default.Menu,
                "Going up/down",
                modifier = Modifier.size(80.dp)
            )

        }
        Text("$altitudeDelta m")
        Text("$altitude m")

    }
}