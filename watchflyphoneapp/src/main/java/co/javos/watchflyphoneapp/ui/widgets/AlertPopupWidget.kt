package co.javos.watchflyphoneapp.ui.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import co.javos.watchflyphoneapp.models.Alert
import co.javos.watchflyphoneapp.models.AlertType

class AlertPopupWidget {
    @Preview
    @Composable
    fun AlertPopup(
        alert: Alert = Alert(
            AlertType.WARNING,
            "Warning",
            "lorem ipsum dolor sit amet".repeat(15)
        ),
        onDismiss: () -> Unit = {}
    ) {
        val backgroundColor = Color.White
        val textColor = when (alert.type) {
            AlertType.WARNING -> Color.Black
            AlertType.ERROR -> Color.Black
        }
        val iconColor = when (alert.type) {
            AlertType.WARNING -> Color(0xFFF7BA37)
            AlertType.ERROR -> Color.Red
        }
        val icon = when (alert.type) {
            AlertType.WARNING -> Icons.Default.Warning
            AlertType.ERROR -> Icons.Default.Error
        }

        val dialogProperties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = alert.type != AlertType.ERROR
        )

        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = { },
            title = { Text(alert.title, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(
                        rememberScrollState()
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(alert.message)
                }
            },
            icon = {
                Icon(
                    icon,
                    contentDescription = "Alert Icon",
                    modifier = Modifier.size(60.dp),
                )
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss, colors =
                    ButtonDefaults.textButtonColors().copy(
                        contentColor = textColor
                    )
                ) {
                    Text("Dismiss", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = backgroundColor,
            titleContentColor = textColor,
            textContentColor = textColor,
            iconContentColor = iconColor,
            properties = dialogProperties
        )
    }
}
