package co.javos.watchfly.ui.widgets

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color

enum class BlinkingMode {
    COLOR,
    OPACITY
}

@Composable
fun BlinkingWidget(
    enabled: Boolean,
    modifier: Modifier = Modifier,
    mode: BlinkingMode = BlinkingMode.COLOR,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition("State blink")
    var blinking = Color.Black
    var alpha = 1F

    if (enabled && mode == BlinkingMode.COLOR) {
        blinking = infiniteTransition.animateColor(
            Color.Black, Color.Black,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 800
                    Color.White at 400
                }
            ), label = "ColorBlink"
        ).value
    } else if (enabled && mode == BlinkingMode.OPACITY) {
        alpha = infiniteTransition.animateFloat(
            0F, 1F,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 800
                }), label = "OpacityBlink"
        ).value
    }

    Box(
        modifier = Modifier
            .then(modifier)
            .background(blinking)
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}