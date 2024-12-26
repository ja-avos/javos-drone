package co.javos.watchflyphoneapp.ui.screens

import android.location.Location
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import co.javos.watchflyphoneapp.R
import co.javos.watchflyphoneapp.Utils
import co.javos.watchflyphoneapp.viewmodels.MapViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

class MapView {

    @Preview
    @Composable
    fun Map(viewModel: MapViewModel? = null) {

        val droneStatus = viewModel?.droneStatus?.collectAsState()

        val home = getLatLngFromLocation(droneStatus?.value?.homeLocation)
        val dronePosition = getLatLngFromLocation(droneStatus?.value?.location)

        val mapUISettings =
            MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
        val mapProperties = MapProperties(isMyLocationEnabled = viewModel?.locationPermission ?: false)
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            uiSettings = mapUISettings,
            properties = mapProperties,
            cameraPositionState = viewModel?.cameraPositionState ?: return,
        ) {
            Marker(
                state = MarkerState(position = home),
                icon = Utils.bitmapDescriptorFromVector(
                    LocalContext.current,
                    R.drawable.home_icon,
                    150
                ),
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

    private fun getLatLngFromLocation(location: Location?): LatLng {
        if (location == null) {
            return LatLng(0.0, 0.0)
        }
        return LatLng(location.latitude, location.longitude)
    }
}
