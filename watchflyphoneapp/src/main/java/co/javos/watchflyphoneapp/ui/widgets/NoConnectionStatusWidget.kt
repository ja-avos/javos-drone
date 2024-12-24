package co.javos.watchflyphoneapp.ui.widgets

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.javos.watchflyphoneapp.R
import co.javos.watchflyphoneapp.models.DroneState

class NoConnectionStatusWidget {

    @Composable
    fun NoConnectionStatus(status: DroneState) {

        val iconModifier = Modifier.size(40.dp)
        var textStyle = remember {
            mutableStateOf(
                TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        fun addRedLine(add: Boolean = false, modifier: Modifier): Modifier {
            if (add)
                return Modifier.drawWithContent {
                    drawContent()
                    val start = (size.width - size.height) / 2
                    drawLine(
                        Color.Red,
                        Offset(start, 0F),
                        Offset(size.width - start, size.height),
                        strokeWidth = 10F
                    )
                }.then(modifier)
            return modifier
        }

        Box(
            modifier = Modifier
                .padding(16.dp)
                .size(300.dp, 110.dp)
                .onSizeChanged {
                    Log.d("BOX SIZE", it.toString())
                    textStyle.value = textStyle.value.copy(
                        fontSize = (it.width / 50).sp,
                        textIndent = TextIndent(
                            firstLine = (it.width / 50).sp
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1F)
                ) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_controller),
                        tint = Color.White,
                        modifier = addRedLine(
                            status == DroneState.NO_REMOTE,
                            iconModifier.weight(1F)
                        ),
                        contentDescription = "Controller Icon"
                    )
                    VerticalDivider(color = Color.Gray, modifier = Modifier.weight(0.1F, false))
                    Row(
                        modifier = Modifier.weight(3F),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (status != DroneState.NO_REMOTE)
                            Icon(
                                Icons.Default.Circle,
                                tint = Color.Green,
                                contentDescription = "Remote Connected Status",
                                modifier = Modifier.weight(0.3f, false)
                            )
                        Text(
                            text = if (status == DroneState.NO_REMOTE) "Not Connected" else "Connected",
                            style = textStyle.value,
                            modifier = Modifier.weight(3F)
                        )

                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1F)
                ) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.ic_quadcopter),
                        tint = Color.White,
                        modifier = addRedLine(
                            true, iconModifier
                                .weight(1F)
                                .padding(4.dp)
                        ),
                        contentDescription = "Drone Icon"
                    )
                    VerticalDivider(color = Color.Gray, modifier = Modifier.weight(0.1F, false))
                    Text(
                        text = "Not Connected",
                        style = textStyle.value,
                        modifier = Modifier.weight(3F)
                    )
                }
            }
        }
    }
}
