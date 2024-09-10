package co.javos.watchflyphoneapp

import android.Manifest
import android.os.Bundle
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

    private val permissionList = arrayOf<String>(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Request permissions
        requestPermissions(permissionList, 0)

        enableEdgeToEdge()
        setContent {
            JAVOSDroneTheme {
                NavigationStack()
            }
        }
    }
}

@Composable
fun NavigationStack() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppScreens.MAIN.name) {
        composable(AppScreens.MAIN.name) {
            MainScreen(navController)
        }
        composable(AppScreens.CHAT.name) {
            ChatScreen(navController)
        }
    }
}
