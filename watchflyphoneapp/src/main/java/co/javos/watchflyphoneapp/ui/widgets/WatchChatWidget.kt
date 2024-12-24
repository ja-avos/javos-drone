package co.javos.watchflyphoneapp.ui.widgets

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.WatchOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import co.javos.watchflyphoneapp.AppScreens
import co.javos.watchflyphoneapp.viewmodels.WatchButtonViewModel

class WatchChatWidget {
    @Composable
    fun WatchButton(navController: NavController?, viewModel: WatchButtonViewModel? = null) {
        Log.d("WatchChatWidget", "WatchButton: $viewModel")
        Log.d("WatchChatWidget", "WatchButton: ${viewModel?.enabled}")
        val watchOpened = remember { mutableStateOf(false) }
        val watchConnected = viewModel?.enabled ?: remember { mutableStateOf(false) }.value
        if (watchOpened.value) {
            Button(
                onClick = {
                    watchOpened.value = !watchOpened.value
                    navController?.navigate(AppScreens.CHAT.name)
                },
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.Gray,
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text(text = "Open messages")
            }

        } else {
            IconButton(
                enabled = watchConnected,
                onClick = {
                    watchOpened.value = !watchOpened.value
                },
                colors = IconButtonColors(
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.Gray,
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                modifier = Modifier
                    .shadow(6.dp, CircleShape)
                    .size(48.dp)

            ) {
                Icon(
                    if (watchConnected)
                        Icons.Default.Watch else Icons.Default.WatchOff,
                    contentDescription = "Watch"
                )
            }
        }
    }
}
