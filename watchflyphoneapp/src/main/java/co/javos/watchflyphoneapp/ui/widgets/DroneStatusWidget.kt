package co.javos.watchflyphoneapp.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battery5Bar
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.javos.watchflyphoneapp.R
import co.javos.watchflyphoneapp.models.DroneState
import co.javos.watchflyphoneapp.viewmodels.DroneStatusViewModel

class DroneStatusWidget {

    @Composable
    fun DroneStatus(viewModel: DroneStatusViewModel?, onClick: () -> Unit = {}) {

        val status = viewModel?.droneStatus?.value?.state ?: DroneState.NO_REMOTE



        val statusDetailStyle = TextStyle(
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
        val statusTitleStyle = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        val statusMainStyle = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        val iconModifier = Modifier.size(20.dp)

        Box(
            modifier = Modifier
                .shadow(8.dp, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .clickable(enabled = status == DroneState.FLYING, onClick = onClick)
                .size(200.dp, 110.dp)
                .background(Color.White)
                .drawWithContent {
                    drawContent()
                    if (status != DroneState.FLYING)
                        drawRect(Color.LightGray.copy(alpha = 0.8F))
                }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1F)
                        .padding(8.dp)
                ) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_quadcopter),
                        contentDescription = "Drone Icon",
                        modifier = Modifier.fillMaxSize(),
                        tint = Color.DarkGray
                    )
                }
                VerticalDivider(color = Color.Gray)

                Column(modifier = Modifier.weight(2F)) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .weight(1F)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.SignalCellularAlt,
                            contentDescription = "Drone Signal",
                            modifier = iconModifier
                        )
                        Icon(
                            Icons.Default.SatelliteAlt,
                            contentDescription = "GPS Signal",
                            modifier = iconModifier
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Battery5Bar,
                                contentDescription = "Battery Level",
                                modifier = iconModifier
                            )
                            Text("100%", style = statusDetailStyle)
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.7F),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1F),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Altitude", style = statusTitleStyle)
                            Text("120 m", style = statusDetailStyle)
                            Text("+ 2.0 m/s", style = statusDetailStyle)
                        }
                        VerticalDivider(color = Color.Gray, modifier = Modifier.weight(0.1F, false))
                        Column(
                            modifier = Modifier.weight(1F),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Distance", style = statusTitleStyle)
                            Text("520 m", style = statusDetailStyle)
                            Text("11.0 m/s", style = statusDetailStyle)
                        }
                    }
                    HorizontalDivider(color = Color.Gray)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(status.name, style = statusMainStyle)
                    }
                }
            }
        }
    }
}
