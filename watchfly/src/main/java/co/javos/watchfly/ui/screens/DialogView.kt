package co.javos.watchfly.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
fun DialogView(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    text: String
) {
    Dialog(onDismissRequest = { /*TODO*/ }) {
        // TODO
    }
}