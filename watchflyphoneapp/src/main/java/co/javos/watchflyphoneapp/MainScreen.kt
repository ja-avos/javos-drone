package co.javos.watchflyphoneapp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Battery5Bar
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.SatelliteAlt
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.WatchOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import co.javos.watchflyphoneapp.widgets.ChangeScreenWidget
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

enum class DroneStatus {
    NO_REMOTE,
    NO_DRONE,

    //    MOTORS_OFF,
//    MOTORS_ON,
    FLYING,
//    LANDED,
//    ERROR
}


@Preview(device = Devices.TABLET, uiMode = 0)
@Composable
fun MainScreen(navController: NavController? = null) {
    val dronePhotos = listOf(
        R.drawable.drone_bacata,
        R.drawable.drone_solar,
        R.drawable.drone_laguna,
        R.drawable.drone_playa
    )
    val showMap = remember { mutableStateOf(true) }
    val droneStatus = remember { mutableStateOf(DroneStatus.NO_REMOTE) }
    val idDronePhoto = remember { mutableIntStateOf(dronePhotos.first()) }
    val showAlert = remember { mutableStateOf(false) }

    Box {
        if (showAlert.value)
            AlertPopup(
                AlertType.WARNING,
                "Warning",
                "lorem ipsum dolor sit amet".repeat(15),
                onDismiss = { showAlert.value = false }
            )

        if (showMap.value) MapScreen() else CameraScreen(
            droneStatus.value,
            idDronePhoto.intValue
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp, 0.dp, 15.dp, 0.dp)
                .safeDrawingPadding(),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                DroneStatusWidget(
                    status = droneStatus.value,
                    onClick = {
                        idDronePhoto.intValue =
                            dronePhotos[(dronePhotos.indexOf(idDronePhoto.intValue) + 1) % dronePhotos.size]
                    })
                WatchButton(navController)
                ChangeScreenWidget(onTap = { showMap.value = !showMap.value }) {
                    if (!showMap.value) MapScreen() else CameraScreen(
                        droneStatus.value,
                        idDronePhoto.intValue
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                VirtualSticksWidget(onClick = {
                    droneStatus.value =
                        DroneStatus.values()[(droneStatus.value.ordinal + 1) % DroneStatus.values().size]
                })
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                ActionsWidget(
                    status = droneStatus,
                    showMapActions = showMap.value,
                    onTakePhoto = {
                        showAlert.value = true
                    })
            }
        }
    }
}

@Composable
fun MapScreen() {
    val home = LatLng(4.92, -74.02)
    val dronePosition = LatLng(4.92, -74.03)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(home, 14f)
    }
    val mapUISettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
    val mapProperties = MapProperties(isMyLocationEnabled = true)
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        uiSettings = mapUISettings,
        properties = mapProperties,
        cameraPositionState = cameraPositionState
    ) {
        Marker(
            state = MarkerState(position = home),
            icon = bitmapDescriptorFromVector(LocalContext.current, R.drawable.home_icon, 150),
            title = "Home",
            snippet = "Marker in Home location"
        )
        Marker(
            state = MarkerState(position = dronePosition),
            icon = bitmapDescriptorFromVector(
                LocalContext.current,
                R.drawable.drone_control_logo,
                150
            ),
            title = "Drone",
            snippet = "Marker in drone location"
        )
    }
}

@Composable
fun CameraScreen(droneStatus: DroneStatus, idDrawable: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (droneStatus == DroneStatus.NO_REMOTE || droneStatus == DroneStatus.NO_DRONE)
            DroneConnectingScreen(droneStatus)
        else
            Image(
                painter = painterResource(idDrawable),
                contentDescription = "Drone photo",
                contentScale = ContentScale.Crop
            )
    }
}

@Composable
fun DroneConnectingScreen(status: DroneStatus) {

    val iconModifier = Modifier.size(40.dp)
    var textStyle = remember {
        mutableStateOf(
            TextStyle(
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }

    fun addRedLine(add: Boolean = false, modifier: Modifier): Modifier {
        if (add)
            return Modifier.drawWithContent {
                drawContent()
                val start = (size.width - size.height) / 2
                drawLine(
                    Color.Red,
                    Offset(start, 0F),
                    Offset(size.width - start, size.height),
                    strokeWidth = 10F
                )
            }.then(modifier)
        return modifier
    }

    Box(
        modifier = Modifier
            .padding(16.dp)
            .size(300.dp, 110.dp)
            .onSizeChanged {
                Log.d("BOX SIZE", it.toString())
                textStyle.value = textStyle.value.copy(
                    fontSize = (it.width / 50).sp,
                    textIndent = TextIndent(
                        firstLine = (it.width / 50).sp
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1F)
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.ic_controller),
                    tint = Color.White,
                    modifier = addRedLine(status == DroneStatus.NO_REMOTE, iconModifier.weight(1F)),
                    contentDescription = "Controller Icon"
                )
                VerticalDivider(color = Color.Gray, modifier = Modifier.weight(0.1F, false))
                Row(modifier = Modifier.weight(3F), verticalAlignment = Alignment.CenterVertically) {
                    if (status != DroneStatus.NO_REMOTE)
                        Icon(
                            Icons.Default.Circle,
                            tint = Color.Green,
                            contentDescription = "Remote Connected Status",
                            modifier = Modifier.weight(0.3f, false)
                        )
                    Text(
                        text = if (status == DroneStatus.NO_REMOTE) "Not Connected" else "Connected",
                        style = textStyle.value,
                        modifier = Modifier.weight(3F)
                    )

                }
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1F)
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.ic_quadcopter),
                    tint = Color.White,
                    modifier = addRedLine(true, iconModifier
                        .weight(1F)
                        .padding(4.dp)),
                    contentDescription = "Drone Icon"
                )
                VerticalDivider(color = Color.Gray, modifier = Modifier.weight(0.1F, false))
                Text(
                    text = "Not Connected",
                    style = textStyle.value,
                    modifier = Modifier.weight(3F)
                )
            }
        }
    }
}

@Composable
fun DroneStatusWidget(status: DroneStatus = DroneStatus.NO_DRONE, onClick: () -> Unit) {

    val statusDetailStyle = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold
    )
    val statusTitleStyle = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )

    val statusMainStyle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )

    val iconModifier = Modifier.size(20.dp)

    Box(
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .clickable(enabled = status == DroneStatus.FLYING, onClick = onClick)
            .size(200.dp, 110.dp)
            .background(Color.White)
            .drawWithContent {
                drawContent()
                if (status != DroneStatus.FLYING)
                    drawRect(Color.LightGray.copy(alpha = 0.8F))
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1F)
                    .padding(8.dp)
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.ic_quadcopter),
                    contentDescription = "Drone Icon",
                    modifier = Modifier.fillMaxSize(),
                    tint = Color.DarkGray
                )
            }
            VerticalDivider(color = Color.Gray)

            Column(modifier = Modifier.weight(2F)) {
                Row(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .weight(1F)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.SignalCellularAlt,
                        contentDescription = "Drone Signal",
                        modifier = iconModifier
                    )
                    Icon(
                        Icons.Default.SatelliteAlt,
                        contentDescription = "GPS Signal",
                        modifier = iconModifier
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Battery5Bar,
                            contentDescription = "Battery Level",
                            modifier = iconModifier
                        )
                        Text("100%", style = statusDetailStyle)
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.7F),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1F),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Altitude", style = statusTitleStyle)
                        Text("120 m", style = statusDetailStyle)
                        Text("+ 2.0 m/s", style = statusDetailStyle)
                    }
                    VerticalDivider(color = Color.Gray, modifier = Modifier.weight(0.1F, false))
                    Column(
                        modifier = Modifier.weight(1F),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Distance", style = statusTitleStyle)
                        Text("520 m", style = statusDetailStyle)
                        Text("11.0 m/s", style = statusDetailStyle)
                    }
                }
                HorizontalDivider(color = Color.Gray)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(status.name, style = statusMainStyle)
                }
            }
        }
    }
}

@Composable
fun WatchButton(navController: NavController?) {
    val watchOpened = remember { mutableStateOf(false) }
    val watchConnected = remember { mutableStateOf(true) }
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
            enabled = watchConnected.value,
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
                if (watchConnected.value)
                    Icons.Default.Watch else Icons.Default.WatchOff, contentDescription = "Watch"
            )
        }
    }
}

@Composable
fun VirtualSticksWidget(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .shadow(8.dp, RoundedCornerShape(80.dp))
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(80.dp))
            .size(180.dp, 90.dp)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            VirtualStick(70.dp)
            VirtualStick(70.dp)
        }
    }
}

@Composable
fun VirtualStick(size: Dp) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .border(1.dp, Color.Gray, CircleShape)
            .size(size),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(Color.Blue, CircleShape)
        )
    }
}

@Composable
fun ActionsWidget(
    status: MutableState<DroneStatus>,
    showMapActions: Boolean = false,
    onTakePhoto: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Box(modifier = Modifier.weight(1F)) {
            StopButton(enabled = status.value == DroneStatus.FLYING, onClick = {
                status.value = DroneStatus.NO_DRONE
            })
        }
        Box(modifier = Modifier.weight(1F), contentAlignment = Alignment.Center) {
            CaptureButton(
                enabled =
                status.value == DroneStatus.FLYING, onClick = onTakePhoto
            )
        }
        Box(modifier = Modifier.weight(1F)) {
            if (showMapActions)
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    CenterPhoneButton()
                    CenterDroneButton(
                        enabled =
                        status.value == DroneStatus.FLYING
                    )
                }
        }
    }
}

@Composable
fun StopButton(enabled: Boolean, onClick: () -> Unit) {
    IconButton(
        enabled = enabled,
        onClick = onClick,
        colors = IconButtonColors(
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Gray,
            containerColor = Color.White,
            contentColor = Color.Red
        ),
        modifier = Modifier
            .size(60.dp)
            .shadow(6.dp, CircleShape)
    ) {
        Icon(
            Icons.Default.Stop,
            contentDescription = "Stop",
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .border(2.dp, if (enabled) Color.Red else Color.Gray, CircleShape)
                .padding(1.dp)
        )
    }
}

@Composable
fun CaptureButton(enabled: Boolean, onClick: () -> Unit) {
    val buttonColor = if (enabled) Color.White else Color.Gray
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(enabled = enabled, onClick = onClick)
            .border(3.dp, buttonColor, CircleShape)
            .size(80.dp)
            .shadow(6.dp, CircleShape)
            .padding(6.dp)
            .drawBehind {
                drawCircle(
                    buttonColor,
                    radius = size.width / 2,
                    center = Offset(size.width / 2, size.height / 2)
                )
            })
}

@Composable
fun CenterPhoneButton() {
    IconButton(
        onClick = { },
        colors = IconButtonColors(
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Gray,
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        modifier = Modifier
            .size(48.dp)
            .shadow(6.dp, CircleShape)

    ) {
        Icon(
            Icons.Default.MyLocation,
            contentDescription = "My Location",
        )
    }
}

@Composable
fun CenterDroneButton(enabled: Boolean) {
    IconButton(
        enabled = enabled,
        onClick = {},
        colors = IconButtonColors(
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.Gray,
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        modifier = Modifier
            .size(48.dp)
            .shadow(6.dp, CircleShape)

    ) {
        Icon(
            ImageVector.vectorResource(R.drawable.ic_quadcopter),
            contentDescription = "Stop",
            modifier = Modifier
                .padding(10.dp)
                .border(1.dp, if (enabled) Color.Black else Color.Gray, CircleShape)
                .padding(6.dp)
        )
    }
}

enum class AlertType {
    WARNING,
    ERROR
}

@Preview
@Composable
fun AlertPopup(
    alertType: AlertType = AlertType.WARNING,
    title: String = "Warning",
    message: String = "Something went wrong",
    onDismiss: () -> Unit = {}
) {
    val backgroundColor = Color.White
    val textColor = when (alertType) {
        AlertType.WARNING -> Color.Black
        AlertType.ERROR -> Color.Black
    }
    val iconColor = when (alertType) {
        AlertType.WARNING -> Color(0xFFF7BA37)
        AlertType.ERROR -> Color.Red
    }
    val icon = when (alertType) {
        AlertType.WARNING -> Icons.Default.Warning
        AlertType.ERROR -> Icons.Default.Error
    }

    val dialogProperties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = alertType != AlertType.ERROR
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { },
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(
                    rememberScrollState()
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(message)
            }
        },
        icon = {
            Icon(
                icon,
                contentDescription = "Alert Icon",
                modifier = Modifier.size(60.dp),
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss, colors =
                ButtonDefaults.textButtonColors().copy(
                    contentColor = textColor
                )
            ) {
                Text("Dismiss", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = backgroundColor,
        titleContentColor = textColor,
        textContentColor = textColor,
        iconContentColor = iconColor,
        properties = dialogProperties
    )
}

fun bitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int,
    size: Int = 120,
    color: Color = Color.Black
): BitmapDescriptor {
    val vectorDrawable: Drawable? = ContextCompat.getDrawable(context, vectorResId)
    vectorDrawable?.setBounds(0, 0, size, size)
    vectorDrawable?.alpha = 255
    val bitmap: Bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    vectorDrawable!!.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}
