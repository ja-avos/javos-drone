import android.annotation.SuppressLint
import android.icu.text.Transliterator.Position
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.SurfaceHolder.BadSurfaceTypeException
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import co.javos.drone.screens.ConnectionStatusPopup
import dji.common.error.DJIError
import dji.common.error.DJISDKError
import dji.common.util.CommonCallbacks.CompletionCallbackWith
import dji.keysdk.DJIKey
import dji.keysdk.RemoteControllerKey
import dji.sdk.products.Aircraft
import dji.sdk.sdkmanager.DJISDKManager
import kotlinx.coroutines.launch

data class MenuItem(
    val title: String,
    val path: String,
    val icon: ImageVector,
    val content: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenu(items: List<MenuItem>) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
// icons to mimic drawer destinations
    val selectedItem = remember { mutableStateOf<MenuItem?>(null) }

    androidx.compose.material3.ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(24.dp))
                Text("JAVOS Drone")
                Spacer(Modifier.height(12.dp))
                Divider()
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = selectedItem.value == null,
                    onClick = {
                        scope.launch { drawerState.close() }
                        selectedItem.value = null
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Divider()

                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.title) },
                        selected = item == selectedItem.value,
                        onClick = {
                            scope.launch { drawerState.close() }
                            selectedItem.value = item
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        drawerState = drawerState,
    ) {
        Box(Modifier.fillMaxSize().background(color = MaterialTheme.colorScheme.background),) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                if (selectedItem.value == null) {
                    Home()
                } else {
                    selectedItem.value?.content?.invoke()
                }
            }
            FloatingActionButton(
                onClick = { scope.launch { drawerState.open() } }, modifier = Modifier
                    .align(
                        Alignment.TopStart
                    )
                    .padding(16.dp),
                shape = CircleShape
            ) {
                Icon(Icons.Default.Menu, "Menu")
            }
            ShowStatus(modifier = Modifier.align(Alignment.TopEnd))
        }
    }
}

@Composable
fun ShowStatus(modifier: Modifier = Modifier) {

    var popupControl by remember { mutableStateOf(false) }

    var connectionState by remember { mutableStateOf(0) }


    fun checkStatus() {
        var product = DJISDKManager.getInstance().product;
        if (product != null) {

            if (
                product is Aircraft &&
                product.isConnected &&
                (product.flightController?.isConnected == true)
            ) {
                connectionState = 2
            } else {
                connectionState = 1

            }

        } else {
            connectionState = 0
        }
    }

    fun getStatusColor(): Color {
        when (connectionState) {
            0 -> return Color.Red
            1 -> return Color.Yellow
            2 -> return Color.Green
        }
        return Color.Black
    }

    val mainHandler = Handler(Looper.getMainLooper())

    mainHandler.post(object : Runnable {
        override fun run() {
            checkStatus()
            mainHandler.postDelayed(this, 100)
        }
    })

    Surface(
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
            .padding(top = 24.dp, end = 16.dp),
        tonalElevation = 24.dp,
        shadowElevation = 24.dp
    ) {
        Row(
            modifier = Modifier
                .clickable { popupControl = !popupControl }
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Status:")
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
                shape = CircleShape, modifier = Modifier
                    .width(12.dp)
                    .height(12.dp), color = getStatusColor()
            ) {

            }
        }

    }

    if (popupControl) {
        Popup(alignment = Alignment.Center) {
            ConnectionStatusPopup()
        }
    }
}

@Composable
fun Home() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "JAVOS Drone Home", modifier = Modifier.align(Alignment.Center))
    }
}