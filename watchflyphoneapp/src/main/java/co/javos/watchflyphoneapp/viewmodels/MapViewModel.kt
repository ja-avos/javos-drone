package co.javos.watchflyphoneapp.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.javos.watchflyphoneapp.Utils
import co.javos.watchflyphoneapp.models.DroneStatus
import co.javos.watchflyphoneapp.repository.DJIController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.flow.MutableStateFlow

class MapViewModel(private val djiController: DJIController, context: Context) : ViewModel() {
    val droneStatus: MutableStateFlow<DroneStatus> = djiController.droneStatus

    // Get location permission
    val locationPermission = Utils.checkPermission(
        context,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val cameraPositionState: CameraPositionState = CameraPositionState(
        position = CameraPosition.fromLatLngZoom(LatLng(4.0, -72.0), 14f)
    )

    init {
        centerDeviceLocation()
    }

    fun centerOnDrone() {
        val droneLocation = droneStatus.value.location
        updateCameraPosition(droneLocation ?: Location("home"))
    }

    fun centerOnHome() {
        val homeLocation = droneStatus.value.homeLocation
        updateCameraPosition(homeLocation ?: Location("home"))
    }

    @SuppressLint("MissingPermission")
    fun centerDeviceLocation() {
        if (locationPermission) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                updateCameraPosition(it)
            }
        }
    }

    private fun updateCameraPosition(location: Location) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(
            LatLng(location.latitude, location.longitude),
            14f
        )
    }

    class MapViewModelFactory(
        private val djiController: DJIController,
        private val context: Context
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return MapViewModel(djiController, context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
