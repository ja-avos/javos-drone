/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package co.javos.watchfly.presentation

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Battery0Bar
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Wifi1Bar
import androidx.compose.material.icons.filled.Wifi2Bar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.AnchorType
import androidx.wear.compose.foundation.CurvedDirection
import androidx.wear.compose.foundation.CurvedLayout
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.foundation.curvedRow
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.curvedText
import co.javos.watchfly.R
import co.javos.watchfly.presentation.theme.JAVOSDroneTheme
import kotlinx.coroutines.delay
import java.util.Timer
import kotlin.concurrent.timerTask

enum class Actions {
    LAND,
    RTH,
    STOP,
    CHANGE_CONTROLS,
    CHANGE_ALTITUDE
}

enum class ControlMode {
    RPY,
    PY
}

enum class DroneState {
    MOTORS_ON,
    MOTORS_OFF,
    RTH,
    LANDING,
    FLYING,
    ALTITUDE_CHANGE
}

enum class ConnectionState {
    DISCONNECTED,
    PHONE_CONNECTED,
    REMOTE_CONNECTED,
    DRONE_CONNECTED
}

class MainActivity : ComponentActivity() {

    private var timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            WearApp(this)
        }
    }

    private fun showToast(toastMsg: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.post { Toast.makeText(applicationContext, toastMsg, Toast.LENGTH_LONG).show() }
    }

    fun performAction(action: Actions, currentStatus: Any? = null): Any? {
        when (action) {
            Actions.LAND -> {
                showToast("Landing...")
            }

            Actions.RTH -> {
                showToast("RTH...")
            }

            Actions.STOP -> {
                timer.cancel()
//                showToast("Stopping...")
            }

            Actions.CHANGE_CONTROLS -> {
                return if (currentStatus == ControlMode.RPY) {
                    ControlMode.PY
                } else {
                    ControlMode.RPY
                }
            }

            Actions.CHANGE_ALTITUDE -> {
                showToast("Changing Altitude...")
            }

            else -> {
                showToast("Unknown Action")
            }
        }
        return currentStatus
    }

    fun setTimer(time: Long, action: () -> Unit) {
        timer.cancel()
        timer = Timer()
        timer.schedule(timerTask {
            action()
        }, time)
    }

    fun cancelTimer() {
        timer.cancel()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WearApp(mainActivity: MainActivity?) {
    val fullscreen = remember { mutableStateOf(false) }
    val sensorsData = remember { mutableStateOf(List(3) { 0.0 }) }

    val controlMode = remember { mutableStateOf(ControlMode.RPY) }

    val droneState = remember { mutableStateOf(DroneState.MOTORS_OFF) }

    val connectionState = remember { mutableStateOf(ConnectionState.DISCONNECTED) }

    var cursorOffset = remember { mutableStateOf(Offset.Zero) }

    val sensorManager = mainActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager?

    // Add flag to keep screen awake
    mainActivity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

    LaunchedEffect(Unit) {
        if (connectionState.value == ConnectionState.DISCONNECTED) {
            for (i in ConnectionState.values()) {
                connectionState.value = i
                delay(2700)
            }
        }
    }


    val sensorType = Sensor.TYPE_GAME_ROTATION_VECTOR
    val sensors = sensorManager?.getSensorList(sensorType)

    val listener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // I have no desire to deal with the accuracy events
        }

        override fun onSensorChanged(event: SensorEvent) {
            //just set the values to a textview so they can be displayed.
            if (event.sensor.type == sensorType) {
                sensorsData.value = event.values.toList().map { num -> num.toDouble() }
                if (controlMode.value == ControlMode.RPY && fullscreen.value) {
                    cursorOffset.value = Offset(
                        event.values[1] * 200,
                        event.values[0] * 200
                    )
                }
            }
        }
    }

    if (fullscreen.value && controlMode.value == ControlMode.RPY)
        sensorManager?.registerListener(listener, sensors?.get(0), SensorManager.SENSOR_DELAY_GAME)
    else
        sensorManager?.unregisterListener(listener)

    var altitudeCursorModifier = Modifier.offset(28.dp, 0.dp)

    if (droneState.value == DroneState.ALTITUDE_CHANGE) {
        altitudeCursorModifier = Modifier.offset(cursorOffset.value.x.dp, cursorOffset.value.y.dp)
    }

    JAVOSDroneTheme {
        if (connectionState.value != ConnectionState.DRONE_CONNECTED)
            ConnectionScreen(mainActivity, connectionState)
        else
            if (droneState.value == DroneState.MOTORS_OFF || droneState.value == DroneState.MOTORS_ON)
                LandedScreen(mainActivity, droneState)
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
                            when (droneState.value) {
                                DroneState.FLYING, DroneState.RTH, DroneState.LANDING -> Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    MainGrid(mainActivity = mainActivity, controlMode, droneState)
                                }

                                DroneState.ALTITUDE_CHANGE -> ChangeAltitudeScreen(cursorOffset.value)
                                else -> Box {}
                            }
                            Button(
                                onClick = {},
                                enabled = droneState.value == DroneState.FLYING || droneState.value == DroneState.ALTITUDE_CHANGE,
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
                                            droneState.value = DroneState.FLYING
                                        }) { change, dragAmount ->
                                            change.consume()
                                            cursorOffset.value += Offset(
                                                dragAmount.x / 2,
                                                dragAmount.y / 2
                                            )
                                        }
                                    }
                                    .pointerInteropFilter {
                                        if (droneState.value == DroneState.FLYING || droneState.value == DroneState.ALTITUDE_CHANGE) {
                                            when (it.action) {
                                                MotionEvent.ACTION_DOWN -> {
                                                    droneState.value = DroneState.ALTITUDE_CHANGE
                                                    true
                                                }

                                                MotionEvent.ACTION_UP -> {
                                                    droneState.value = DroneState.FLYING
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
                    } else if (controlMode.value == ControlMode.RPY) {
                        SensorsDataScreen(
                            sensorsData.value[0],
                            sensorsData.value[1],
                            sensorsData.value[2]
                        )
                    } else {
                        GridControlScreen(fullscreen)
                    }
                    if (droneState.value != DroneState.ALTITUDE_CHANGE)
                        Button(
                            enabled = droneState.value == DroneState.FLYING,
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                containerColor =
                                if (controlMode.value == ControlMode.RPY)
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
                                        if (controlMode.value == ControlMode.PY) {
                                            change.consume()
                                            cursorOffset.value += Offset(
                                                dragAmount.x / 2,
                                                dragAmount.y / 2
                                            )
                                        }
                                    }
                                }
                                .pointerInteropFilter {
                                    if (droneState.value == DroneState.FLYING) {
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

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun ConnectionScreen(
    mainActivity: MainActivity? = null,
    connectionState: MutableState<ConnectionState> = mutableStateOf(ConnectionState.DISCONNECTED)
) {

    val deviceIcon: ImageVector = when (connectionState.value) {
        ConnectionState.DISCONNECTED -> {
            Icons.Default.Smartphone
        }

        ConnectionState.PHONE_CONNECTED -> {
            ImageVector.vectorResource(R.drawable.stadia_controller_24dp)
        }

        ConnectionState.REMOTE_CONNECTED -> {
            ImageVector.vectorResource(R.drawable.ic_quadcopter)
        }

        else -> {
            Icons.Default.Smartphone
        }
    }

    val connectionIconList = listOf(
        Icons.Default.Wifi1Bar,
        Icons.Default.Wifi2Bar,
        Icons.Default.Wifi
    )

    var connectionIcon = remember { mutableStateOf(Icons.Default.Wifi) }

    // Change every second to the next icon in the list
    LaunchedEffect(Unit) {
        while (true) {
            connectionIcon.value =
                connectionIconList[(connectionIconList.indexOf(connectionIcon.value) + 1) % connectionIconList.size]
            if (connectionState.value == ConnectionState.DRONE_CONNECTED) {
                break
            }
            delay(300)
        }
    }

    Box(
        modifier = Modifier
            .background(color = Color.Black)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    )
    {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                deviceIcon,
                "Phone / Remote / Drone icon",
                modifier = Modifier.size(46.dp)
            )
            Icon(
                connectionIcon.value,
                "Connection icon",
                modifier = Modifier.size(46.dp)
            )
            Icon(
                Icons.Default.Watch,
                "Watch icon",
                modifier = Modifier.size(46.dp)
            )
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun LandedScreen(
    mainActivity: MainActivity? = null,
    droneState: MutableState<DroneState> = mutableStateOf(DroneState.MOTORS_OFF)
) {

    val takingOff = remember { mutableStateOf(false) }

    fun changeState() {
        droneState.value = if (droneState.value == DroneState.MOTORS_OFF) {
            DroneState.MOTORS_ON
        } else {
            DroneState.MOTORS_OFF
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1F)
                .background(
                    color = if (droneState.value == DroneState.MOTORS_OFF)
                        Color.Black
                    else
                        Color.White
                )
                .clickable {
                    changeState()
                }, contentAlignment = Alignment.Center
        ) {
            Text(
                "${if (droneState.value == DroneState.MOTORS_OFF) "Start" else "Stop"}\nMotors",
                textAlign = TextAlign.Center, fontSize = TextUnit(3F, TextUnitType.Em),
                color = if (droneState.value == DroneState.MOTORS_OFF)
                    Color.White
                else
                    Color.Black
            )
        }
        HorizontalDivider(color = Color.White, thickness = 1.dp)
        BlinkingBox(enabled = takingOff.value,
            modifier = Modifier
                .fillMaxSize()
                .weight(1F)
                .clickable {
                    takingOff.value = !takingOff.value
                    if (takingOff.value) {
                        mainActivity?.setTimer(1000) {
                            // Log that the drone is taking off
                            Log.d("DRONE", "Drone is taking off")
                            droneState.value = DroneState.FLYING
                        }
                    } else {
                        mainActivity?.cancelTimer()
                    }
                }
        ) {
            Text(
                "Take\nOff",
                textAlign = TextAlign.Center,
                fontSize = TextUnit(3F, TextUnitType.Em)
            )
        }
    }
}

enum class BlinkingMode {
    COLOR,
    OPACITY
}

@Composable
fun BlinkingBox(
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

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun MainGrid(
    mainActivity: MainActivity? = null,
    controlMode: MutableState<ControlMode> = mutableStateOf(ControlMode.RPY),
    droneState: MutableState<DroneState> = mutableStateOf(DroneState.FLYING)
) {
    var controlText = if (controlMode.value == ControlMode.RPY) "PY" else "RPY"
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .rotate(45F)
        ) {
            Column {
                Row(modifier = Modifier.weight(1F)) {
                    BlinkingBox(false, modifier = Modifier
                        .fillMaxSize()
                        .weight(1F)
                        .clickable(enabled = droneState.value == DroneState.FLYING) {
                            controlMode.value = mainActivity?.performAction(
                                Actions.CHANGE_CONTROLS,
                                controlMode.value
                            ) as ControlMode
                        }) {
                        Text(
                            "$controlText\nControls",
                            modifier = Modifier.rotate(-45F),
                            style = LocalTextStyle.current.merge(
                                fontSize = TextUnit(2.3F, TextUnitType.Em),
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                    VerticalDivider(thickness = 1.dp)
                    BlinkingBox(
                        enabled = false,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1F)
                            .clickable {
                                mainActivity?.performAction(Actions.STOP)
                                droneState.value = DroneState.FLYING
                            },
                    ) {
                        Text(
                            text = "STOP",
                            modifier = Modifier.rotate(-45F),
                            style = LocalTextStyle.current.merge(
                                fontSize = TextUnit(2.3F, TextUnitType.Em),
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
                HorizontalDivider(thickness = 1.dp)
                Row(modifier = Modifier.weight(1F)) {
                    BlinkingBox(
                        enabled = droneState.value == DroneState.RTH,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1F)
                            .clickable(enabled = droneState.value == DroneState.FLYING) {
                                mainActivity?.performAction(Actions.RTH)
                                droneState.value = DroneState.RTH
                            },
                    ) {
                        Text(
                            text = "RTH",
                            modifier = Modifier.rotate(-45F),
                            style = LocalTextStyle.current.merge(
                                fontSize = TextUnit(2.3F, TextUnitType.Em),
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                    VerticalDivider(thickness = 1.dp)
                    BlinkingBox(
                        enabled = droneState.value == DroneState.LANDING,
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1F)
                            .clickable(enabled = droneState.value == DroneState.FLYING) {
                                mainActivity?.performAction(Actions.LAND)
                                droneState.value = DroneState.LANDING
                                mainActivity?.setTimer(5000) {
                                    droneState.value = DroneState.MOTORS_OFF
                                }
                            },
                    ) {

                        Text(
                            text = "Land",
                            modifier = Modifier.rotate(-45F),
                            style = LocalTextStyle.current.merge(
                                fontSize = TextUnit(2.3F, TextUnitType.Em),
                                textAlign = TextAlign.Center
                            )
                        )

                    }
                }
            }
        }
        DroneStatus()
    }
}

@Composable
fun DroneStatus() {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .size(100.dp)
            .border(1.dp, color = Color.White, shape = CircleShape)
            .background(color = Color.Black)
            .padding(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Battery0Bar, "Good Signal", modifier = Modifier.size(12.dp))
                Text("53%", fontSize = TextUnit(1.5F, TextUnitType.Em))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.width(IntrinsicSize.Min),
                ) {
                    Text("120m", fontSize = TextUnit(1.2F, TextUnitType.Em))
                    HorizontalDivider()
                    Text("600m", fontSize = TextUnit(1.2F, TextUnitType.Em))
                }
                Icon(
                    Icons.Default.SignalCellularAlt,
                    "Good Signal",
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                "Touch to control", //fontSize = TextUnit(1F, TextUnitType.Em), modifier = Modifier.width(30.dp))
                modifier = Modifier.width(30.dp),
                style = LocalTextStyle.current.merge(
                    TextStyle(
                        fontSize = TextUnit(1.2F, TextUnitType.Em),
                        lineHeight = 1.em,
                        textAlign = TextAlign.Center
                    )
                )
            )
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun SensorsDataScreen(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0) {
    val textMeasurer = rememberTextMeasurer()
    val cardinalityStyle = TextStyle(
        color = Color.White,
        background = Color.Black,
        fontWeight = FontWeight.Bold,
        fontSize = TextUnit(
            3F,
            TextUnitType.Em
        )
    )
    val textN = textMeasurer.measure(
        style = cardinalityStyle.merge(color = Color.Red),
        text = "N"
    )
    val textE = textMeasurer.measure(
        style = cardinalityStyle,
        text = "E"
    )
    val textS = textMeasurer.measure(
        style = cardinalityStyle,
        text = "S"
    )
    val textW = textMeasurer.measure(
        style = cardinalityStyle,
        text = "W"
    )

    val rotation = ((z.toFloat() + 2) * 180) % 360

    Box {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotation)
        ) {
            drawCircle(
                color = Color.Black,
                radius = (size.width / 2)
            )
            var degrees = 360
            var divisions = 40
            for (i in 0..degrees step degrees /
                divisions) {
                rotate((i).toFloat(), pivot = Offset(size.width / 2, size.height / 2)) {
                    drawLine(
                        color = Color.White,
                        start = Offset(size.width / 2, size.height),
                        end = Offset(size.width / 2, 0F),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
            drawCircle(
                color = Color.Black,
                radius = (size.width / 2) - 15,
            )
            divisions = 4
            for (i in 0..degrees step degrees /
                divisions) {
                rotate((i + 45).toFloat(), pivot = Offset(size.width / 2, size.height / 2)) {
                    drawLine(
                        color = Color.White,
                        start = Offset(size.width / 2, size.height),
                        end = Offset(size.width / 2, 0F),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            }
            drawCircle(
                color = Color.Black,
                radius = (size.width / 2) - 40,
            )
            drawText(
                textN,
                topLeft = Offset((size.width - textN.size.width) / 2, 0F),
            )
            drawText(
                textW,
                topLeft = Offset(3.dp.toPx(), (size.height - textW.size.height) / 2),
            )
            drawText(
                textS,
                topLeft = Offset(
                    (size.width - textS.size.width) / 2,
                    size.height - textS.size.height
                ),
            )
            drawText(
                textE,
                topLeft = Offset(
                    size.width - textE.size.width - 3.dp.toPx(),
                    (size.height - textE.size.height) / 2
                ),
            )
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = Color.Green,
                start = Offset(40F, size.height / 2),
                end = Offset(size.width - 40F, size.height / 2),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(7F, 7F), 0F)
            )
            drawCircle(
                color = Color.White, radius = 22.dp.toPx(),
                style = Stroke(width = 2.5F)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "X: ${String.format("%.2f", x)}", fontSize = TextUnit(
                    1.5F,
                    TextUnitType.Em
                )
            )
            Text(
                text = "Y: ${String.format("%.2f", y)}", fontSize = TextUnit(
                    1.5F,
                    TextUnitType.Em
                )
            )
            Text(
                text = "Z: ${String.format("%.2f", z)}", fontSize = TextUnit(
                    1.5F,
                    TextUnitType.Em
                )
            )
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun GridControlScreen(fullscreen: MutableState<Boolean> = mutableStateOf(false)) {
    val fontSize = 13.sp
    val textStyle = TextStyle(
        fontSize = fontSize,
        color = Color.White,
        background = Color.Black,
        lineHeight = 17.sp
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .drawBehind {
                val strokeWidth = 0.5.dp.toPx()
                val lines = 10;
                val height = size.height
                val width = size.width
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(7F, 7F), 0F)

                for (i in 0..lines) {
                    val y = height * i / lines
                    drawLine(
                        color = Color.White,
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = strokeWidth,
                        pathEffect = pathEffect
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(y, 0f),
                        end = Offset(y, height),
                        strokeWidth = strokeWidth,
                        pathEffect = pathEffect
                    )
                }

                drawLine(
                    color = Color.White,
                    start = Offset(0f, height / 2),
                    end = Offset(width, height / 2),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = Color.White,
                    start = Offset(width / 2, 0f),
                    end = Offset(width / 2, height),
                    strokeWidth = 1.dp.toPx()
                )
            },
        contentAlignment = Alignment.Center
    ) {
        CurvedText(anchor = 270F, color = Color.White, text = "Pitch   Down")
        CurvedText(
            anchor = 94F,
            color = Color.White,
            text = "Pitch   Up",
            angularDirection = CurvedDirection.Angular.Reversed
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Yaw\nLeft", style = textStyle)
            Text("Yaw\nRight", style = textStyle, textAlign = TextAlign.End)
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = Color.White,
                start = Offset(0f, size.height / 2),
                end = Offset(size.width, size.height / 2),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = Color.White,
                start = Offset(size.width / 2, 0f),
                end = Offset(size.width / 2, size.height),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

@Composable
fun CurvedText(
    anchor: Float,
    color: Color,
    text: String,
    angularDirection: CurvedDirection.Angular? = null
) {
    CurvedLayout(
        anchor = anchor,
        anchorType = AnchorType.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        curvedRow() {
            curvedText(
                text = text,
                angularDirection = angularDirection,
                style = CurvedTextStyle(
                    fontSize = 13.sp,
                    color = color,
                    background = Color.Black
                )
            )
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun ChangeAltitudeScreen(cursorOffset: Offset = Offset.Zero) {
    var altitude = remember { mutableFloatStateOf(100F) }
    var ascending = remember { mutableIntStateOf(0) }

    ascending.value = -1 * cursorOffset.y.toInt()
    altitude.value = 100F + ascending.value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1F)
            ) {
                BlinkingBox(enabled = ascending.value != 0, mode = BlinkingMode.OPACITY) {
                    Icon(
                        if (ascending.value > 0) Icons.Default.KeyboardDoubleArrowUp else if (ascending.value < 0) Icons.Default.KeyboardDoubleArrowDown else Icons.Default.Menu,
                        "Going up/down",
                        modifier = Modifier.size(80.dp)
                    )

                }
                Text("${ascending.value} m")
                Text("${altitude.value} m")

            }
            VerticalDivider()
            Column(
                Modifier
                    .weight(1F)
                    .fillMaxHeight()
                    .drawBehind {
                        drawLine(
                            color = Color.Magenta,
                            start = Offset(0F, size.height / 2),
                            end = Offset(size.width, size.height / 2),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(7F, 7F), 0F)
                        )
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .weight(1F)
                        .background(color = if (ascending.value > 0) Color.Magenta.copy(alpha = 0.1F) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add, "Increase", modifier = Modifier
                            .size(40.dp)
                            .offset((-7.5).dp, 7.5.dp)
                    )
                }
                Box(
                    Modifier
                        .fillMaxSize()
                        .weight(1F)
                        .background(color = if (ascending.value < 0) Color.Magenta.copy(alpha = 0.1F) else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Remove, "Decrease", modifier = Modifier
                            .size(40.dp)
                            .offset((-7.5).dp, (-7.5).dp)
                    )
                }

            }

        }
    }
}

@Preview(device = Devices.WEAR_OS_LARGE_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp("Preview Android", null)
}
