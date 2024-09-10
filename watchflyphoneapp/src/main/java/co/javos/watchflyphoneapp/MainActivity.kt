package co.javos.watchflyphoneapp

import android.Manifest
import android.media.AudioManager
import android.os.Bundle
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.javos.watchflyphoneapp.ui.theme.JAVOSDroneTheme

enum class AppScreens {
    MAIN,
    CHAT
}

class MainActivity : ComponentActivity() {

    lateinit var audioManager: AudioManager
    lateinit var vibratorManager: VibratorManager

    private val permissionList = arrayOf<String>(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

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
                NavigationStack(audioManager, vibratorManager)
            }
        }
    }
}

@Composable
fun NavigationStack(audioManager: AudioManager, vibratorManager: VibratorManager) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreens.MAIN.name) {
        composable(AppScreens.MAIN.name) {
            MainScreen(navController, audioManager, vibratorManager)
        }
        composable(AppScreens.CHAT.name) {
            ChatScreen(navController)
        }
    }
}
