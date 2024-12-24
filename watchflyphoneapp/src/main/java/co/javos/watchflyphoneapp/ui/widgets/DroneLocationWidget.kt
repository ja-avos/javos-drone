package co.javos.watchflyphoneapp.ui.widgets

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import co.javos.watchflyphoneapp.R

class DroneLocationWidget {
    @Composable
    fun DroneLocationButton(enabled: Boolean) {
        IconButton(
            enabled = enabled,
            onClick = {},
            colors = IconButtonColors(
                disabledContainerColor = Color.LightGray,
                disabledContentColor = Color.Gray,
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            modifier = Modifier
                .size(48.dp)
                .shadow(6.dp, CircleShape)

        ) {
            Icon(
                ImageVector.vectorResource(R.drawable.ic_quadcopter),
                contentDescription = "Stop",
                modifier = Modifier
                    .padding(10.dp)
                    .border(1.dp, if (enabled) Color.Black else Color.Gray, CircleShape)
                    .padding(6.dp)
            )
        }
    }
}
