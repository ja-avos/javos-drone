package co.javos.drone.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierInfo
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.unit.dp
import dji.common.error.DJIError
import dji.common.flightcontroller.ControlMode
import dji.common.flightcontroller.FlightControllerState
import dji.common.flightcontroller.virtualstick.FlightControlData
import dji.common.flightcontroller.virtualstick.YawControlMode
import dji.common.util.CommonCallbacks.CompletionCallback
import dji.common.util.CommonCallbacks.CompletionCallbackWith
import dji.sdk.flightcontroller.FlightController
import dji.sdk.mission.MissionControl.Listener
import dji.sdk.mission.timeline.TimelineElement
import dji.sdk.mission.timeline.TimelineEvent
import dji.sdk.products.Aircraft
import dji.sdk.sdkmanager.DJISDKManager
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun TakeOffAndLandScreen() {

//    var aircraft = DJISDKManager.getInstance().product as Aircraft

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(36.dp),
        verticalArrangement = Arrangement.Center
    ) {
        showStatus(Modifier.align(alignment = Alignment.CenterHorizontally))
        Spacer(modifier = Modifier.height(24.dp))
        takeOffLandButton()
    }
}

@Composable
fun showStatus(modifier: Modifier = Modifier) {

    var flightStatus by remember { mutableStateOf("Loading status...") }
    val scope = rememberCoroutineScope()

    var aircraft = DJISDKManager.getInstance().product as Aircraft?

    aircraft?.flightController?.setStateCallback(
        object : FlightControllerState.Callback {
            override fun onUpdate(state: FlightControllerState) {
                scope.launch {
                    flightStatus = state?.areMotorsOn()?.toString() ?: "No status"
                }
            }

        }
    )



    Text(text = flightStatus, modifier = modifier)
}

@Composable
fun takeOffLandButton() {

    var aircraft = DJISDKManager.getInstance().product as Aircraft?

    var fController = aircraft?.flightController

    var motorsOn by remember { mutableStateOf(false) }
    var isFlying by remember { mutableStateOf(false) }
    var virtualSticksEnabled by remember { mutableStateOf(false) }
    var yawMode by remember { mutableStateOf( fController?.yawControlMode == YawControlMode.ANGLE) }


    fun toggleMotors() {
        if (!motorsOn) {
            fController?.turnOnMotors(CompletionCallback<DJIError> { error ->
                Log.d(
                    "DRONE MOTORS",
                    error?.toString() ?: "No error. Motors On"
                )
                if (error == null) {
                    motorsOn = true
                }
            })
        } else {
            fController?.turnOffMotors(CompletionCallback<DJIError> { error ->
                Log.d(
                    "DRONE MOTORS",
                    error?.toString() ?: "No error. Motors Off"
                )
                if (error == null) {
                    isFlying = false
                    motorsOn = false
                }
            })
        }
    }

    fun takeOff() {

        fController?.startTakeoff(CompletionCallback<DJIError> { error ->
            Log.d(
                "DRONE FLIGHT",
                error?.toString() ?: "No error. Taking Off!"
            )
            if (error == null) {
                motorsOn = true
                isFlying = true
            }
        })
    }

    fun land() {
        fController?.state?.isFlying
        fController?.startLanding(CompletionCallback<DJIError> { error ->
            Log.d(
                "DRONE FLIGHT",
                error?.toString() ?: "No error. Landing!"
            )
            if (error == null) {
                isFlying = false
                motorsOn = false
                virtualSticksEnabled = false
            }
        })
    }

    fun enableVirtualSticks(enable: Boolean) {
        fController?.setVirtualStickModeEnabled(enable) {
            error ->
            Log.d(
                "DRONE VS ENABLE",
                error?.toString() ?: ("No error. Changed to $enable")
            )
            if (error == null) {
                virtualSticksEnabled = enable
                fController.setVirtualStickAdvancedModeEnabled(enable)
            }
        }
    }

    fun changeYawMode(mode: Boolean) {
        if (fController == null) {
            return
        }
        fController.yawControlMode = if (mode) YawControlMode.ANGLE else YawControlMode.ANGULAR_VELOCITY
        yawMode = mode
    }

    fun rotateLeft() {
        var flightDataL = FlightControlData(0F,0F,45F,1F)
        fController?.sendVirtualStickFlightControlData(flightDataL) {
            error ->
            Log.d("DRONE ROTATE L", error?.toString() ?: ("No error. Changed to $flightDataL"))
        }
    }

    fun rotateRight() {
        var flightDataR = FlightControlData(0F,0F,-45F,1F)
        fController?.sendVirtualStickFlightControlData(flightDataR) {
                error ->
            Log.d("DRONE ROTATE R", error?.toString() ?: ("No error. Changed to $flightDataR"))
        }
    }


    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 48.dp)
        ) {
            Text(text = "Motors", style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.fillMaxWidth())
            Switch(checked = motorsOn, onCheckedChange = { toggleMotors() })
        }
        Button(onClick = { if (isFlying) land() else takeOff() }) {
            Text(text = if (isFlying) "Land" else "Take Off")
        }
        if (isFlying) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 48.dp)
            ) {
                Text(text = "Virtual Sticks", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.fillMaxWidth())
                Switch(checked = virtualSticksEnabled, onCheckedChange = { enable -> enableVirtualSticks(enable) })
            }
        }
        if (virtualSticksEnabled) {
            Row(
                modifier = Modifier.padding(horizontal = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Yaw Mode", style = MaterialTheme.typography.labelMedium)
                Switch(checked = yawMode, onCheckedChange = { mode -> changeYawMode(mode) })
            }
            Row(
                modifier = Modifier.padding(horizontal = 48.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { rotateLeft() }) {
                    Text(text = "Left")
                }
                Button(onClick = { rotateRight() }) {
                    Text(text = "Right")
                }
            }
        }
    }


}
