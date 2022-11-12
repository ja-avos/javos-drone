package co.javos.drone.screens

import android.util.Log
import android.widget.FrameLayout
import androidx.annotation.FloatRange
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import co.javos.drone.utils.VideoFeedView
import dji.common.gimbal.CapabilityKey
import dji.common.gimbal.GimbalMode
import dji.common.gimbal.Rotation
import dji.common.gimbal.RotationMode
import dji.common.util.DJIParamMinMaxCapability
import dji.sdk.camera.VideoFeeder
import dji.sdk.products.Aircraft
import dji.sdk.sdkmanager.DJISDKManager

@Composable
fun GimbalControlScreen(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Gimbal Control", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(32.dp))
        GimbalControls()
    }
}

@Composable
fun GimbalControls() {

    val aircraft = DJISDKManager.getInstance().product as Aircraft?

    val gController = aircraft?.gimbal

    gController?.setMode(GimbalMode.FREE) { error ->
        Log.e("GIMABL CONTROL", error?.toString() ?: "No error")
    }

    val yawCapability =
        (gController?.capabilities?.get(CapabilityKey.ADJUST_YAW) as DJIParamMinMaxCapability)
    val yawRange = yawCapability.min.toFloat()..yawCapability.max.toFloat()

    val pitchCapability =
        (gController.capabilities?.get(CapabilityKey.ADJUST_PITCH) as DJIParamMinMaxCapability)
    val pitchRange = pitchCapability.min.toFloat()..pitchCapability.max.toFloat()

    val rollCapability =
        (gController.capabilities?.get(CapabilityKey.ADJUST_ROLL) as DJIParamMinMaxCapability)
    val rollRange = rollCapability.min.toFloat()..rollCapability.max.toFloat()

    fun changeYaw(value: Float) {
        var rotation = Rotation.Builder().mode(RotationMode.ABSOLUTE_ANGLE).yaw(value)
            .build()
        gController.rotate(
            rotation
        ) { error ->
            if (error != null) {
                Log.e("GIMBAL CONTROL", "$error")
            } else {
                Log.e("GIMBAL CONTROL", "SUCCESSFUL")
            }
        }
    }

    fun changePitch(value: Float) {
        var rotation = Rotation.Builder().mode(RotationMode.ABSOLUTE_ANGLE).pitch(value)
            .build()
        gController.rotate(
            rotation
        ) { error ->
            if (error != null) {
                Log.e("GIMBAL CONTROL", "$error")
            } else {
                Log.e("GIMBAL CONTROL", "SUCCESSFUL")
            }
        }
    }

    fun changeRoll(value: Float) {
        var rotation = Rotation.Builder().mode(RotationMode.ABSOLUTE_ANGLE).roll(value)
            .build()
        gController.rotate(
            rotation
        ) { error ->
            if (error != null) {
                Log.e("GIMBAL CONTROL", "$error")
            } else {
                Log.e("GIMBAL CONTROL", "SUCCESSFUL")
            }
        }
    }

    val videoFeed = VideoFeedView(LocalContext.current)

    videoFeed.registerLiveVideo(VideoFeeder.getInstance().primaryVideoFeed, true)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .width(380.dp)
                .height(260.dp)
                .padding(vertical = 24.dp)
        ) {
            AndroidView<VideoFeedView>(factory = {
                videoFeed
            })
        }
        slideControl("Yaw", onChange = { value -> changeYaw(value) }, range = yawRange)
        slideControl("Pitch", onChange = { value -> changePitch(value) }, range = pitchRange)
        slideControl("Roll", onChange = { value -> changeRoll(value) }, range = rollRange)
    }
}

@Composable
fun slideControl(name: String, onChange: (Float) -> Unit, range: ClosedFloatingPointRange<Float>) {

    var value by remember {
        mutableStateOf(0f)
    }

    Column() {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(text = name, style = MaterialTheme.typography.labelLarge)
            Text(text = "Reset", color = MaterialTheme.colorScheme.secondary, modifier = Modifier.clickable {
                value = 0F
                onChange(value) })
        }
        Spacer(modifier = Modifier.height(4.dp))
        Slider(value = value, onValueChange = { tmp ->
            value = tmp
            onChange(value)
        }, valueRange = range,
            onValueChangeFinished = { onChange(value) }
        )
    }

}