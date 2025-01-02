package co.javos.watchfly.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalCellular0Bar
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.SignalCellularAlt1Bar
import androidx.compose.material.icons.filled.SignalCellularAlt2Bar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.Text

@Composable
fun SignalStrengthWidget(signalValue: Int? = null) {

    val signalIcon = when(signalValue) {
        in 0..25 -> Icons.Default.SignalCellular0Bar
        in 26..50 -> Icons.Default.SignalCellularAlt1Bar
        in 51..75 -> Icons.Default.SignalCellularAlt2Bar
        else -> Icons.Default.SignalCellularAlt
    }

    Column(
        modifier = Modifier.width(IntrinsicSize.Min),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            signalIcon,
            "Drone Signal",
            modifier = Modifier.size(17.dp)
        )
        Text(
            "${signalValue ?: "-"}%",
            fontSize = TextUnit(1.2F, TextUnitType.Em)
        )
    }
}