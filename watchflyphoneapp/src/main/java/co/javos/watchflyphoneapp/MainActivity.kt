package co.javos.watchflyphoneapp

import android.Manifest
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.VibratorManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.javos.watchflyphoneapp.repository.DJIController
import co.javos.watchflyphoneapp.repository.WatchMessageConnection
import co.javos.watchflyphoneapp.ui.screens.MainView
import co.javos.watchflyphoneapp.ui.screens.WatchChatView
import co.javos.watchflyphoneapp.ui.theme.JAVOSDroneTheme
import co.javos.watchflyphoneapp.viewmodels.AlertsViewModel
import co.javos.watchflyphoneapp.viewmodels.CameraControlsViewModel
import co.javos.watchflyphoneapp.viewmodels.DroneStatusViewModel
import co.javos.watchflyphoneapp.viewmodels.JoysticksViewModel
import co.javos.watchflyphoneapp.viewmodels.LiveFeedViewModel
import co.javos.watchflyphoneapp.viewmodels.MapViewModel
import co.javos.watchflyphoneapp.viewmodels.WatchButtonViewModel
import co.javos.watchflyphoneapp.viewmodels.WatchChatViewModel
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Wearable
import dji.sdk.sdkmanager.DJISDKManager

enum class AppScreens {
    MAIN, CHAT
}

class MainActivity : ComponentActivity() {

    lateinit var audioManager: AudioManager
    lateinit var vibratorManager: VibratorManager

    private val permissionList = arrayOf<String>(
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Clients required for Watch communication
    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }

    // Repositories
    private val watchMessageConnection by lazy { WatchMessageConnection(
        dataClient, messageClient, capabilityClient
    ) }

    private val djiController by lazy { DJIController(DJISDKManager.getInstance(), this) }

    // ViewModels
    private val droneStatusViewModel: DroneStatusViewModel by viewModels {
        DroneStatusViewModel.DroneStatusViewModelFactory(djiController)
    }
    private val watchChatViewModel: WatchChatViewModel by viewModels {
        WatchChatViewModel.WatchChatViewModelFactory(watchMessageConnection, djiController)
    }
    private val watchButtonViewModel: WatchButtonViewModel by viewModels {
        WatchButtonViewModel.WatchButtonViewModelFactory(watchMessageConnection)
    }
    private val liveFeedViewModel: LiveFeedViewModel by viewModels {
        LiveFeedViewModel.LiveFeedViewModelFactory(djiController)
    }
    private val mapViewModel: MapViewModel by viewModels {
        MapViewModel.MapViewModelFactory(djiController, this.applicationContext)
    }
    private val cameraControlsViewModel: CameraControlsViewModel by viewModels {
        CameraControlsViewModel.CameraControlsViewModelFactory(djiController)
    }
    private val joysticksViewModel: JoysticksViewModel by viewModels {
        JoysticksViewModel.JoysticksViewModelFactory(djiController, watchChatViewModel)
    }
    private val alertsViewModel: AlertsViewModel by viewModels {
        AlertsViewModel.AlertsViewModelFactory(djiController, watchChatViewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Request permissions
        requestPermissions(permissionList, 0)

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        vibratorManager = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager

        audioManager.mode = AudioManager.MODE_NORMAL
        val audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
        audioManager.setCommunicationDevice(
            audioDevices.first()
        )

        enableEdgeToEdge()
        setContent {
            JAVOSDroneTheme {
                NavigationStack(
                    audioManager,
                    vibratorManager,
                    droneStatusViewModel,
                    watchChatViewModel,
                    watchButtonViewModel,
                    liveFeedViewModel,
                    mapViewModel,
                    cameraControlsViewModel,
                    joysticksViewModel,
                    alertsViewModel
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        dataClient.addListener(watchMessageConnection)
        messageClient.addListener(watchMessageConnection)
        messageClient.addListener(watchChatViewModel)
        capabilityClient.addListener(
            watchMessageConnection,
            Uri.parse("wear://"),
            CapabilityClient.FILTER_REACHABLE
        )
        val capabilityQuery =
            capabilityClient.getCapability("wear", CapabilityClient.FILTER_REACHABLE)
        capabilityQuery.addOnSuccessListener {
            Log.d("MainActivity", "onResume: $it")
            watchMessageConnection.onCapabilityChanged(it)
        }
    }

    override fun onPause() {
        super.onPause()
        dataClient.removeListener(watchMessageConnection)
        messageClient.removeListener(watchMessageConnection)
        messageClient.removeListener(watchChatViewModel)
        capabilityClient.removeListener(watchMessageConnection)
    }
}

@Composable
fun NavigationStack(
    audioManager: AudioManager,
    vibratorManager: VibratorManager,
    droneStatusViewModel: DroneStatusViewModel,
    watchChatViewModel: WatchChatViewModel,
    watchButtonViewModel: WatchButtonViewModel,
    liveFeedViewModel: LiveFeedViewModel,
    mapViewModel: MapViewModel,
    cameraControlsViewModel: CameraControlsViewModel,
    joysticksViewModel: JoysticksViewModel,
    alertsViewModel: AlertsViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreens.MAIN.name) {
        composable(AppScreens.MAIN.name) {
            MainView().MainScreen(
                navController,
                audioManager,
                vibratorManager,
                droneStatusViewModel = droneStatusViewModel,
                watchButtonViewModel = watchButtonViewModel,
                liveFeedViewModel = liveFeedViewModel,
                mapViewModel = mapViewModel,
                cameraControlsViewModel = cameraControlsViewModel,
                joysticksViewModel = joysticksViewModel,
                alertsViewModel = alertsViewModel
            )
        }
        composable(AppScreens.CHAT.name) {
            WatchChatView().WatchChat(navController, viewModel = watchChatViewModel)
        }
    }
}
