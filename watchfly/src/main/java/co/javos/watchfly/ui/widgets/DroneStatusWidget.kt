package co.javos.watchfly.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battery0Bar
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text
import co.javos.watchfly.models.DroneStatus

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true, apiLevel = 26)
@Composable
fun DroneStatusWidget(droneStatus: DroneStatus? = null) {

    Box(
        modifier = Modifier
            .clip(CircleShape)
            .size(100.dp)
            .border(1.dp, color = Color.White, shape = CircleShape)
            .background(color = Color.Black)
            .padding(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Battery0Bar, "Good Signal", modifier = Modifier.size(12.dp))
                Text("${droneStatus?.battery ?: "- "}%", fontSize = TextUnit(1.5F, TextUnitType.Em))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DistanceWidget(
                    droneStatus?.location,
                    droneStatus?.homeLocation,
                    droneStatus?.altitude
                )
                SignalStrengthWidget(droneStatus?.signalStrength)
            }
            Text(
                "Touch to control", //fontSize = TextUnit(1F, TextUnitType.Em), modifier = Modifier.width(30.dp))
                modifier = Modifier.width(30.dp),
                style = LocalTextStyle.current.merge(
                    TextStyle(
                        fontSize = TextUnit(1.2F, TextUnitType.Em),
                        lineHeight = 1.em,
                        textAlign = TextAlign.Center
                    )
                )
            )
        }
    }
}