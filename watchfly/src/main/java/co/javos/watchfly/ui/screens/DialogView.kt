package co.javos.watchfly.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import androidx.wear.compose.material.dialog.Dialog
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.google.android.horologist.compose.material.AlertContent

@OptIn(ExperimentalHorologistApi::class)
@Preview(showBackground = true, device = Devices.WEAR_OS_SMALL_ROUND)
@Composable
fun DialogView(
    showDialog: Boolean = true,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {},
    title: String = "Dialog title",
    text: String = "Dialog text",
) {
    val state = rememberResponsiveColumnState()

    val properties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false
    )

    Dialog(
        showDialog = showDialog,
        onDismissRequest = onDismiss,
        scrollState = state.state,
        properties = properties
    ) {
        DialogContent(
            title = title,
            text = text,
            onCancel = onCancel,
            onDismiss = onDismiss,
            onOk = onConfirm
        )
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun DialogContent(
    title: String,
    text: String,
    onCancel: () -> Unit,
    onDismiss: () -> Unit,
    onOk: () -> Unit
) {
    AlertContent(
        icon = {},
        title = title,
        message = text,

        onCancel = {
            onCancel()
            onDismiss()
        },
        onOk = {
            onOk()
            onDismiss()
        }
    )
}