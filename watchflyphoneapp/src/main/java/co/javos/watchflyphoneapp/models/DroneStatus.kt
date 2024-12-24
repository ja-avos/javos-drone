package co.javos.watchflyphoneapp.models

import android.location.Location

enum class DroneState {
    NO_REMOTE,
    NO_DRONE,
    MOTORS_OFF,
    MOTORS_ON,
    FLYING,
    LANDED,
    ERROR
}

data class DroneStatus(
    val state: DroneState = DroneState.NO_REMOTE,
    val battery: Int = 0,
    val altitude: Int = 0,
    val location: Location? = null,
    val verticalSpeed: Float = 0F,
    val horizontalSpeed: Float = 0F,
    val rotation: Float = 0F,
    val signalStrength: Int = 0,
    val gpsSignalStrength: Int = 0
)
