package co.javos.watchfly.ui.widgets

import android.location.Location
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.wear.compose.material3.Text
@Composable
fun DistanceWidget(droneLocation: Location? = null, homeLocation: Location? = null, altitude: Float? = null) {

    var distance: Float? = null
    if (droneLocation != null && homeLocation != null) {
        distance = droneLocation.distanceTo(homeLocation)
    }

    Column(
        modifier = Modifier.width(IntrinsicSize.Min),
    ) {
        Text("${ altitude ?: " - "}m", fontSize = TextUnit(1.2F, TextUnitType.Em))
        HorizontalDivider()
        Text("${ distance ?: " - "}m", fontSize = TextUnit(1.2F, TextUnitType.Em))
    }
}