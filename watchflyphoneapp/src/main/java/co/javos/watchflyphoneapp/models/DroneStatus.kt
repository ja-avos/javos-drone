package co.javos.watchflyphoneapp.models

import android.location.Location
import com.google.gson.JsonObject

enum class DroneState {
    SDK_NOT_INITIALIZED,
    SDK_INITIALIZING,
    SDK_ERROR,
    NO_REMOTE,
    NO_DRONE,
    MOTORS_OFF,
    MOTORS_ON,
    TAKING_OFF,
    FLYING,
    CONFIRM_LANDING,
    LANDED,
    GOING_HOME,
    ERROR
}

class DroneStatus(
    var state: DroneState = DroneState.SDK_NOT_INITIALIZED,
    var battery: Int = 0,
    var altitude: Float = 0F,
    var location: Location? = null,
    var verticalSpeed: Float = 0F,
    var horizontalSpeed: Float = 0F,
    var rotation: Float = 0F,
    var signalStrength: Int = 0,
    var gpsSignalStrength: Int = 0,
    var homeLocation: Location? = null
) {
    fun toState(state: DroneState): DroneStatus {
        return DroneStatus(
            state = state,
            battery = this.battery,
            altitude = this.altitude,
            location = this.location,
            verticalSpeed = this.verticalSpeed,
            horizontalSpeed = this.horizontalSpeed,
            rotation = this.rotation,
            signalStrength = this.signalStrength,
            gpsSignalStrength = this.gpsSignalStrength,
            homeLocation = this.homeLocation
        )
    }

    fun isDroneConnected(): Boolean {
        return state in listOf(
            DroneState.MOTORS_OFF,
            DroneState.MOTORS_ON,
            DroneState.FLYING,
            DroneState.LANDED,
            DroneState.ERROR
        )
    }

    fun copy(
        state: DroneState = this.state,
        battery: Int = this.battery,
        altitude: Float = this.altitude,
        location: Location? = this.location,
        verticalSpeed: Float = this.verticalSpeed,
        horizontalSpeed: Float = this.horizontalSpeed,
        rotation: Float = this.rotation,
        signalStrength: Int = this.signalStrength,
        gpsSignalStrength: Int = this.gpsSignalStrength,
        homeLocation: Location? = this.homeLocation
    ): DroneStatus {
        return DroneStatus(
            state = state,
            battery = battery,
            altitude = altitude,
            location = location,
            verticalSpeed = verticalSpeed,
            horizontalSpeed = horizontalSpeed,
            rotation = rotation,
            signalStrength = signalStrength,
            gpsSignalStrength = gpsSignalStrength,
            homeLocation = homeLocation
        )
    }

    override fun toString(): String {
        return "DroneStatus(state=$state, battery=$battery, altitude=$altitude, location=$location, verticalSpeed=$verticalSpeed, horizontalSpeed=$horizontalSpeed, rotation=$rotation, signalStrength=$signalStrength, gpsSignalStrength=$gpsSignalStrength, homeLocation=$homeLocation)"
    }

    fun toJsonObject(): JsonObject {
        val jsonObject = JsonObject()
        jsonObject.addProperty("state", state.name)
        jsonObject.addProperty("battery", battery)
        jsonObject.addProperty("altitude", altitude)
        jsonObject.addProperty("verticalSpeed", verticalSpeed)
        jsonObject.addProperty("horizontalSpeed", horizontalSpeed)
        jsonObject.addProperty("rotation", rotation)
        jsonObject.addProperty("signalStrength", signalStrength)
        jsonObject.addProperty("gpsSignalStrength", gpsSignalStrength)
        return jsonObject
    }
}
