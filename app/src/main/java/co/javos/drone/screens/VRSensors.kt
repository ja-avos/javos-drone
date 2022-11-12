import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Half
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import co.javos.drone.R
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Arrays
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun ParallaxScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var data by remember { mutableStateOf<SensorData?>(null) }

    DisposableEffect(Unit) {
        val dataManager = SensorDataManager(context)
        dataManager.init()

        val job = scope.launch {
            dataManager.data
                .receiveAsFlow()
                .onEach { data = it }
                .collect()
        }

        onDispose {
            dataManager.cancel()
            job.cancel()
        }
    }

    Box(modifier = modifier) {
        ParallaxView(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            depthMultiplier = 20,
            data = data
        )
    }
}

// For changing offset values, it is always preferred to use .offset { } instead of .offset()
// as offset {..} is implemented to avoid recomposition during the offset changes

@SuppressLint("UnrememberedMutableState")
@Composable
fun ParallaxView(
    modifier: Modifier = Modifier,
    depthMultiplier: Int = 20,
    data: SensorData?
) {
    val roll by derivedStateOf { (data?.roll ?: 0f) * depthMultiplier }
    val pitch by derivedStateOf { (data?.pitch ?: 0f) * depthMultiplier }

    Box(modifier = modifier) {
        // Glow Shadow
        // Has quicker offset change and in opposite direction to the Image Card
        Image(
            painter = painterResource(id = R.drawable.profile_picture),
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = -(roll * 1.5).dp.roundToPx(),
                        y = (pitch * 2).dp.roundToPx()
                    )
                }
                .width(256.dp)
                .height(356.dp)
                .align(Alignment.Center)
                .blur(radius = 24.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
        )

        // Edge (used to give depth to card when tilted)
        // Has slightly slower offset change than Image Card
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (roll * 0.9).dp.roundToPx(),
                        y = -(pitch * 0.9).dp.roundToPx()
                    )
                }
                .width(300.dp)
                .height(400.dp)
                .align(Alignment.Center)
                .background(
                    color = Color.White.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                ),
        )

        // Image Card
        // The image inside has a parallax shift in the opposite direction
        Image(
            painter = painterResource(id = R.drawable.profile_picture),
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = roll.dp.roundToPx(),
                        y = -pitch.dp.roundToPx()
                    )
                }
                .width(300.dp)
                .height(400.dp)
                .align(Alignment.Center)
                .clip(RoundedCornerShape(16.dp)),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            alignment = BiasAlignment(
                horizontalBias = (roll * 0.005).toFloat(),
                verticalBias = 0f,
            )
        )
    }
}

class SensorDataManager(context: Context) : SensorEventListener {

    private val sensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    fun init() {
        Log.d("SensorDataManager", "init")
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        val rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

//        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
//        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_UI)
//        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI)
    }

    private var gravity: FloatArray? = null
    private var geomagnetic: FloatArray? = null
    private var rotation: FloatArray? = null
    private var gyro: FloatArray? = null

    private val NS2S = 1.0f / 1000000000.0f
    private val deltaRotationVector = FloatArray(4) { 0f }
    private var timestamp: Float = 0f

    val data: Channel<SensorData> = Channel(Channel.UNLIMITED)

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GRAVITY)
            gravity = event.values

        if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD)
            geomagnetic = event.values

        if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR)
            rotation = event.values

        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE)
            gyro = event.values

        if (gravity != null && geomagnetic != null) {
            var r = FloatArray(9)
            var i = FloatArray(9)

            if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                var orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)

                data.trySend(
                    SensorData(
                        roll = 0F, //orientation[2],
                        pitch = 0F //orientation[1]
                    )
                )
            }
        }

        if (gyro != null) {
            Log.d("SENSOR GYRO", String.format("X: %.4f Y: %.4f Z: %.4f", gyro!![0], gyro!![1], gyro!![2]))
        }

//        if (rotation != null) {
//            var m = FloatArray(16)
//
//            SensorManager.getRotationMatrixFromVector(m, rotation)
//            Log.d("SensorDataManager", "Matrix: ${m.contentToString()}")
//        }
//
//        Log.d("SensorDataManager", "Arr: ${Arrays.toString(rotation)}")

        if (timestamp != 0f && event != null && rotation != null) {
            val dT = (event.timestamp - timestamp) * NS2S
            // Axis of the rotation sample, not normalized yet.
            var axisX: Float = event.values[0]
            var axisY: Float = event.values[1]
            var axisZ: Float = event.values[2]

            Log.d("SENSOR VECTOR", String.format("X: %.4f Y: %.4f Z: %.4f", axisX, axisY, axisZ))

            // Calculate the angular speed of the sample
            val omegaMagnitude: Float = sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ)

            // Normalize the rotation vector if it's big enough to get the axis
            // (that is, EPSILON should represent your maximum allowable margin of error)
            if (omegaMagnitude > Half.EPSILON) {
                axisX /= omegaMagnitude
                axisY /= omegaMagnitude
                axisZ /= omegaMagnitude
            }


            // Integrate around this axis with the angular speed by the timestep
            // in order to get a delta rotation from this sample over the timestep
            // We will convert this axis-angle representation of the delta rotation
            // into a quaternion before turning it into the rotation matrix.
//            val thetaOverTwo: Float = omegaMagnitude * dT / 2.0f
//            val sinThetaOverTwo: Float = sin(thetaOverTwo)
//            val cosThetaOverTwo: Float = cos(thetaOverTwo)
//
//            var newVector = FloatArray(4) { 0f }
//            newVector[0] = sinThetaOverTwo * axisX
//            newVector[1] = sinThetaOverTwo * axisY
//            newVector[2] = sinThetaOverTwo * axisZ
//            newVector[3] = cosThetaOverTwo
//
//            var angSpeed = FloatArray(3) { 0f }
//
//            angSpeed[0] = (newVector[0] - deltaRotationVector[0]) / dT
//            angSpeed[1] = (newVector[1] - deltaRotationVector[1]) / dT
//            angSpeed[2] = (newVector[2] - deltaRotationVector[2]) / dT

//            Log.d("SENSOR VECTOR", String.format("X: %.4f Y: %.4f Z: %.4f", angSpeed[0], angSpeed[1], angSpeed[2]))
        }
        timestamp = event?.timestamp?.toFloat() ?: 0f
        val deltaRotationMatrix = FloatArray(9) { 0f }
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
        // User code should concatenate the delta rotation we computed with the current rotation
        // in order to get the updated rotation.
        // rotationCurrent = rotationCurrent * deltaRotationMatrix;
    }

    fun cancel() {
        Log.d("SensorDataManager", "cancel")
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

data class SensorData(
    val roll: Float,
    val pitch: Float
)