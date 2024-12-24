package co.javos.watchflyphoneapp.ui.widgets

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

class CommandMessageButtonWidget {
    @Composable
    fun CommandMessageButton(text: String, onClick: () -> Unit = {}) {
        Button(onClick = onClick) {
            Text(text = text)
        }
    }
}
