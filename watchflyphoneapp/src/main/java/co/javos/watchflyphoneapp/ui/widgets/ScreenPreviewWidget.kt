package co.javos.watchflyphoneapp.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp

class ScreenPreviewWidget {
    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun ScreenPreview(onTap: () -> Unit, content: @Composable () -> Unit) {
        Box(
            modifier = Modifier
                .clickable { onTap() }
                .pointerInteropFilter {
                    when (it.action) {
                        android.view.MotionEvent.ACTION_UP -> {
                            onTap()
                            true
                        }
                        android.view.MotionEvent.ACTION_DOWN -> true
                        else -> false
                    }                }
                .clip(RoundedCornerShape(10.dp))
                .size(200.dp, 120.dp)
                .background(Color.Black)
        ) {
            content()
        }

    }
}
