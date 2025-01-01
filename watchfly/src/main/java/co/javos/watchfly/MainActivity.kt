/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package co.javos.watchfly

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material3.ButtonDefaults
import co.javos.watchfly.models.ControlMode
import co.javos.watchfly.models.DroneState
import co.javos.watchfly.presentation.theme.JAVOSDroneTheme
import co.javos.watchfly.repository.PhoneMessageConnection
import co.javos.watchfly.ui.screens.AltitudeControlView
import co.javos.watchfly.ui.screens.ConnectingView
import co.javos.watchfly.ui.screens.LandedView
import co.javos.watchfly.ui.screens.MainView
import co.javos.watchfly.ui.screens.PYControlView
import co.javos.watchfly.ui.screens.RPYControlView
import co.javos.watchfly.viewmodels.AltitudeControlViewModel
import co.javos.watchfly.viewmodels.DroneStatusViewModel
import co.javos.watchfly.viewmodels.MainViewModel
import co.javos.watchfly.viewmodels.PYControlViewModel
import co.javos.watchfly.viewmodels.RPYControlViewModel
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable

class MainActivity : ComponentActivity() {

    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }

    /// Repositories
    private val phoneMessageConnection by lazy {
        PhoneMessageConnection(
            dataClient, messageClient, capabilityClient
        )
    }

    // ViewModels
    private val altitudeControlViewModel: AltitudeControlViewModel by viewModels {
        AltitudeControlViewModel.AltitudeControlViewModelFactory(phoneMessageConnection)
    }
    private val droneStatusViewModel: DroneStatusViewModel by viewModels {
        DroneStatusViewModel.DroneStatusViewModelFactory(phoneMessageConnection)
    }
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory(phoneMessageConnection)
    }
    private val pyControlViewModel: PYControlViewModel by viewModels {
        PYControlViewModel.PYControlViewModelFactory(phoneMessageConnection)
    }
    private val rpyControlViewModel: RPYControlViewModel by viewModels {
        RPYControlViewModel.RPYControlViewModelFactory(phoneMessageConnection)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WatchFlyApp(
                this,
                altitudeControlViewModel,
                droneStatusViewModel,
                mainViewModel,
                pyControlViewModel,
                rpyControlViewModel
            )
        }
    }

    private fun showToast(toastMsg: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post { Toast.makeText(applicationContext, toastMsg, Toast.LENGTH_LONG).show() }
    }

    override fun onResume() {
        super.onResume()
        dataClient.addListener(phoneMessageConnection)
        messageClient.addListener(phoneMessageConnection)
        capabilityClient.addListener(
            phoneMessageConnection,
            Uri.parse("wear://"),
            CapabilityClient.FILTER_REACHABLE
        )
        val capabilityQuery =
            capabilityClient.getCapability("mobile", CapabilityClient.FILTER_REACHABLE)
        capabilityQuery.addOnSuccessListener {
            Log.d("MainActivity", "onResume: $it")
            phoneMessageConnection.onCapabilityChanged(it)
        }
    }

    override fun onPause() {
        super.onPause()
        dataClient.removeListener(phoneMessageConnection)
        messageClient.removeListener(phoneMessageConnection)
        capabilityClient.removeListener(phoneMessageConnection)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WatchFlyApp(
    mainActivity: MainActivity?,
    altitudeVM: AltitudeControlViewModel = viewModel(),
    droneStatusVM: DroneStatusViewModel = viewModel(),
    mainVM: MainViewModel = viewModel(),
    pyControlVM: PYControlViewModel = viewModel(),
    rpyControlVM: RPYControlViewModel = viewModel()
) {
    val fullscreen = remember { mutableStateOf(false) }
    val sensorsData = remember { mutableStateOf(List(3) { 0.0 }) }

    val controlMode = mainVM.controlMode.collectAsState().value
    val droneStatus = droneStatusVM.droneStatus.collectAsState().value

    val cursorOffset = remember { mutableStateOf(Offset.Zero) }

    val sensorManager = mainActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager?

    // Add flag to keep screen awake
    mainActivity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    val sensorType = Sensor.TYPE_GAME_ROTATION_VECTOR
    val sensors = sensorManager?.getSensorList(sensorType)

    val listener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // Accuracy events do not matter
        }

        override fun onSensorChanged(event: SensorEvent) {
            //just set the values to a textview so they can be displayed.
            if (event.sensor.type == sensorType) {
                sensorsData.value = event.values.toList().map { num -> num.toDouble() }
                if (controlMode == ControlMode.RPY && fullscreen.value) {
                    cursorOffset.value = Offset(
                        event.values[1] * 200,
                        event.values[0] * 200
                    )
                }
            }
        }
    }

    if (fullscreen.value && controlMode == ControlMode.RPY)
        sensorManager?.registerListener(
            listener,
            sensors?.get(0),
            SensorManager.SENSOR_DELAY_GAME
        )
    else
        sensorManager?.unregisterListener(listener)

    var altitudeCursorModifier = Modifier.offset(28.dp, 0.dp)

    if (controlMode == ControlMode.ALTITUDE) {
        altitudeCursorModifier =
            Modifier.offset(cursorOffset.value.x.dp, cursorOffset.value.y.dp)
    }

    JAVOSDroneTheme {
        if (!droneStatus.isDroneConnected() || !droneStatusVM.isPhoneConnected.collectAsState().value)
            ConnectingView(droneStatusVM)
        else
            if (droneStatus.state in listOf(
                    DroneState.MOTORS_OFF,
                    DroneState.MOTORS_ON,
                    DroneState.TAKING_OFF
                )
            )
                LandedView(mainVM)
            else
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    if (!fullscreen.value) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            when (controlMode) {
                                ControlMode.ALTITUDE -> AltitudeControlView(cursorOffset.value)
                                else -> Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    MainView(mainVM)
                                }
                            }
                            //  Altitude control button
                            Button(
                                onClick = {},
                                enabled = droneStatus.state == DroneState.FLYING || controlMode == ControlMode.ALTITUDE,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Magenta
                                ),
                                shape = CircleShape,
                                modifier = altitudeCursorModifier
                                    .size(40.dp)
                                    .align(
                                        Alignment.CenterEnd
                                    )
                                    .pointerInput(Unit) {
                                        detectDragGestures(onDragEnd = {
                                            cursorOffset.value = Offset.Zero
                                            mainVM.changeControlMode(ControlMode.PY)
                                        }) { change, dragAmount ->
                                            change.consume()
                                            cursorOffset.value += Offset(
                                                dragAmount.x / 2,
                                                dragAmount.y / 2
                                            )
                                        }
                                    }
                                    .pointerInteropFilter {
                                        if (droneStatus.state == DroneState.FLYING || controlMode == ControlMode.ALTITUDE) {
                                            when (it.action) {
                                                MotionEvent.ACTION_DOWN -> {
                                                    mainVM.changeControlMode(ControlMode.ALTITUDE)
                                                    true
                                                }

                                                MotionEvent.ACTION_UP -> {
                                                    mainVM.changeControlMode(ControlMode.PY)
                                                    true
                                                }

                                                else -> false
                                            }
                                        } else {
                                            false
                                        }
                                    }
                            ) {

                            }
                        }
                    } else if (controlMode == ControlMode.RPY) {
                        RPYControlView(
                            sensorsData.value[0],
                            sensorsData.value[1],
                            sensorsData.value[2]
                        )
                    } else {
                        PYControlView(fullscreen)
                    }
                    if (controlMode != ControlMode.ALTITUDE)
                    // Central control button (PY/RPY)
                        Button(
                            enabled = droneStatus.state == DroneState.FLYING,
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                containerColor =
                                if (controlMode == ControlMode.RPY)
                                    Color.Green
                                else
                                    Color.Blue
                            ),
                            shape = CircleShape,
                            modifier = Modifier
                                .size(40.dp)
                                .offset(cursorOffset.value.x.dp, cursorOffset.value.y.dp)
                                .pointerInput(Unit) {
                                    detectDragGestures(onDragEnd = {
                                        fullscreen.value = false
                                        cursorOffset.value = Offset.Zero
                                    }) { change, dragAmount ->
                                        if (controlMode == ControlMode.PY) {
                                            change.consume()
                                            cursorOffset.value += Offset(
                                                dragAmount.x / 2,
                                                dragAmount.y / 2
                                            )
                                        }
                                    }
                                }
                                .pointerInteropFilter {
                                    if (droneStatus.state == DroneState.FLYING) {
                                        when (it.action) {
                                            MotionEvent.ACTION_DOWN -> {
                                                fullscreen.value = true
                                                true
                                            }

                                            MotionEvent.ACTION_UP -> {
                                                fullscreen.value = false
                                                cursorOffset.value = Offset.Zero
                                                true
                                            }

                                            else -> false
                                        }
                                    } else {
                                        false
                                    }
                                }
                        ) {
                        }
                }
    }
}
