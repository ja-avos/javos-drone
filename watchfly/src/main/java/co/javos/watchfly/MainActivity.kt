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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.State
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material3.ButtonDefaults
import co.javos.watchfly.models.ControlMode
import co.javos.watchfly.models.DroneState
import co.javos.watchfly.presentation.theme.JAVOSDroneTheme
import co.javos.watchfly.repository.PhoneMessageConnection
import co.javos.watchfly.ui.screens.AltitudeControlView
import co.javos.watchfly.ui.screens.ConnectingView
import co.javos.watchfly.ui.screens.DialogView
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
            dataClient, messageClient, capabilityClient, this
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

    fun showToast(toastMsg: String) {
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
    val sensorsData = remember { mutableStateOf(FloatArray(3)) }

    val controlModeState = mainVM.controlMode.collectAsState()
    val controlMode = controlModeState.value
    val droneStatus = droneStatusVM.droneStatus.collectAsState().value

    val cursorOffset = remember { mutableStateOf(Offset.Zero) }
    var sensorManager = mainActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager?

    // Add flag to keep screen awake
    mainActivity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    val sensorType = Sensor.TYPE_GAME_ROTATION_VECTOR
    val sensors = sensorManager?.getSensorList(sensorType)

    val listener = remember { SensorDataListener(
        controlModeState,
        fullscreen,
        sensorsData,
        cursorOffset,
        rpyControlVM,
        sensorType
    ) }

    if (fullscreen.value && controlModeState.value == ControlMode.RPY) {
        sensorManager = mainActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager?
        sensorManager?.registerListener(
            listener,
            sensors?.get(0),
            SensorManager.SENSOR_DELAY_UI
        )
    } else {
        Log.d("MainActivity", "onSensorChanged: unregistering... $listener")
        sensorManager?.unregisterListener(listener, sensors?.get(0))
        listener.reset()
    }


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
            else if (droneStatus.state in listOf(
                    DroneState.CONFIRM_LANDING
                )
            )
                DialogView(
                    title = "Landing drone",
                    text = "Confirm drone landing?",
                    onCancel = { mainVM.confirmLanding(false) },
                    onConfirm = { mainVM.confirmLanding(true) }
                )
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
                            when (controlModeState.value) {
                                ControlMode.ALTITUDE -> AltitudeControlView(altitudeVM, cursorOffset.value)
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
                                enabled = droneStatus.state == DroneState.FLYING || controlModeState.value == ControlMode.ALTITUDE,
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
                                            altitudeVM.sendAltitudeVelocity(0F)
                                        }) { change, dragAmount ->
                                            change.consume()
                                            cursorOffset.value += Offset(
                                                dragAmount.x / 2,
                                                dragAmount.y / 2
                                            )
                                            altitudeVM.sendAltitudeVelocity(
                                                cursorOffset.value.y.coerceIn(
                                                    -100F,
                                                    100F
                                                ) / 100 * -1
                                            )
                                        }
                                    }
                                    .pointerInteropFilter {
                                        if (droneStatus.state == DroneState.FLYING || controlModeState.value == ControlMode.ALTITUDE) {
                                            when (it.action) {
                                                MotionEvent.ACTION_DOWN -> {
                                                    mainVM.changeControlMode(ControlMode.ALTITUDE)
                                                    true
                                                }

                                                MotionEvent.ACTION_UP -> {
                                                    mainVM.changeControlMode(ControlMode.PY)
                                                    altitudeVM.sendAltitudeVelocity(0F)
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
                    } else if (controlModeState.value == ControlMode.RPY) {
                        RPYControlView(
                            sensorsData.value[0],
                            sensorsData.value[1],
                            sensorsData.value[2]
                        )
                    } else {
                        PYControlView(fullscreen)
                    }
                    if (controlModeState.value != ControlMode.ALTITUDE)
                    // Central control button (PY/RPY)
                        Button(
                            enabled = droneStatus.state == DroneState.FLYING,
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                containerColor =
                                if (controlModeState.value == ControlMode.RPY)
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
                                        pyControlVM.sendPY(
                                            0F,
                                            0F
                                        )
                                        rpyControlVM.sendRPY(
                                            0F,
                                            0F,
                                            0F
                                        )
                                    }) { change, dragAmount ->
                                        Log.d(
                                            "MainActivity",
                                            "onDragCenterButton. ControlMode = $controlMode"
                                        )
                                        if (controlModeState.value == ControlMode.PY) {
                                            change.consume()
                                            cursorOffset.value += Offset(
                                                dragAmount.x / 2,
                                                dragAmount.y / 2
                                            )
                                            pyControlVM.sendPY(
                                                cursorOffset.value.x.coerceIn(
                                                    -100F,
                                                    100F
                                                ) / 100,
                                                cursorOffset.value.y.coerceIn(
                                                    -100F,
                                                    100F
                                                ) / 100 * -1
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
                                                pyControlVM.sendPY(
                                                    0F,
                                                    0F
                                                )
                                                rpyControlVM.sendRPY(
                                                    0F,
                                                    0F,
                                                    0F
                                                )
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

class SensorDataListener(
    private val controlMode: State<ControlMode>,
    private val fullscreen: MutableState<Boolean>,
    private val sensorsData: MutableState<FloatArray>,
    private val cursorOffset: MutableState<Offset>,
    private val rpyControlVM: RPYControlViewModel,
    private val sensorType: Int
) : SensorEventListener {

    private var initialOffset: FloatArray? = null

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Accuracy events do not matter
    }

    override fun onSensorChanged(event: SensorEvent) {
        //just set the values to a textview so they can be displayed.
        if (event.sensor.type == sensorType) {

            if (initialOffset == null) {
                Log.d("MainActivity", "onSensorChanged: firstEventAfterDiscontinuity")
                initialOffset = floatArrayOf(0F, 0F, event.values[2])
            }

            sensorsData.value = FloatArray(3) {
                event.values[it] - (initialOffset?.get(it) ?: 0F)
            }

            sensorsData.value = getRollPitchYaw(sensorsData.value)

            if (controlMode.value == ControlMode.RPY && fullscreen.value) {
                cursorOffset.value = Offset(
                    sensorsData.value[0] * 100,
                    sensorsData.value[1] * 100
                )
                // Sends the RPY values to the drone
                rpyControlVM.sendRPY(
                    cursorOffset.value.x.coerceIn(
                        -100F,
                        100F
                    ) / 100,
                    cursorOffset.value.y.coerceIn(
                        -100F,
                        100F
                    ) / 100 * -1,
                    sensorsData.value[2] * -1
                )
            }
        }
    }

    fun getRollPitchYaw(vector: FloatArray): FloatArray {
        val rotationMatrix = FloatArray(9)
        val remappedMatrix = FloatArray(9)

        SensorManager.getRotationMatrixFromVector(rotationMatrix, vector)
        SensorManager.remapCoordinateSystem(
            rotationMatrix,
            SensorManager.AXIS_Y,
            SensorManager.AXIS_MINUS_X,
            remappedMatrix
        )


        val orientation = FloatArray(3)
        SensorManager.getOrientation(rotationMatrix, orientation)

        val roll = normalize(Math.toDegrees(orientation[2].toDouble()).toFloat(), 90F)
        val pitch = normalize(Math.toDegrees(orientation[1].toDouble()).toFloat(), -90F)
        val yaw = normalize(Math.toDegrees(orientation[0].toDouble()).toFloat(), -180F)

        return floatArrayOf(roll, pitch, yaw)
    }

    fun normalize(angle: Float, maxAngle: Float): Float {
        return angle / maxAngle
    }

    fun reset() {
        initialOffset = null
    }
}