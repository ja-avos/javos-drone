package co.javos.watchflyphoneapp.ui.widgets

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.javos.watchflyphoneapp.models.AlertType
import co.javos.watchflyphoneapp.viewmodels.AlertsViewModel

@Preview(device = "spec:width=411dp,height=891dp", showBackground = true)
@Composable
fun DiagnosticsButton(
    viewModel: AlertsViewModel? = null,
    onClick: () -> Unit = {}
) {//viewModel: CameraControlsViewModel? = null) {
//    val enabled = viewModel?.isDroneConnected?.collectAsState()?.value ?: true
    val alertsLength = viewModel?.alerts?.collectAsState()?.value?.size ?: 0
    val hasErrors =
        viewModel?.alerts?.collectAsState()?.value?.any { it.type == AlertType.ERROR } ?: false

    val infiniteTransition = rememberInfiniteTransition("State blink")
    var blinking = Color.White
    var alpha = 1F

    if (hasErrors) {
        blinking = infiniteTransition.animateColor(
            Color.White, Color.White,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 800
                    Color.Red at 400
                }
            ), label = "ColorBlink"
        ).value
    }

    BadgedBox(
        badge = {
            if (alertsLength > 0)
                Badge() {
                    Text(alertsLength.toString())
                }
        }
    ) {
        IconButton(
//        enabled = enabled,
            onClick = {
//            viewModel?.openAlerts()
                onClick()
            },
            modifier = Modifier
                .size(48.dp)
                .shadow(6.dp, CircleShape),
            colors = IconButtonColors(
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.Gray,
                containerColor = if (hasErrors) blinking else Color.White,
                contentColor = Color.Black
            ),
        ) {
            Icon(
                Icons.Default.ErrorOutline,
                contentDescription = "Alerts"
            )
        }
    }

}
