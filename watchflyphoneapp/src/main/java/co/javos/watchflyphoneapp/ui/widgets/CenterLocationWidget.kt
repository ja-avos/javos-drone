package co.javos.watchflyphoneapp.ui.widgets

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class CenterLocationWidget {

    @Composable
    fun CenterLocationButton() {
        IconButton(
            onClick = { },
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
                Icons.Default.MyLocation,
                contentDescription = "My Location",
            )
        }
    }
}
