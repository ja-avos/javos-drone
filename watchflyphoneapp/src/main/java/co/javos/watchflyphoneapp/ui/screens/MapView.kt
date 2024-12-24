package co.javos.watchflyphoneapp.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import co.javos.watchflyphoneapp.R
import co.javos.watchflyphoneapp.Utils
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

class MapView {

    @Preview
    @Composable
    fun Map() {
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
                icon = Utils.bitmapDescriptorFromVector(LocalContext.current, R.drawable.home_icon, 150),
                title = "Home",
                snippet = "Marker in Home location"
            )
            Marker(
                state = MarkerState(position = dronePosition),
                icon = Utils.bitmapDescriptorFromVector(
                    LocalContext.current,
                    R.drawable.drone_control_logo,
                    150
                ),
                title = "Drone",
                snippet = "Marker in drone location"
            )
        }
    }
}
