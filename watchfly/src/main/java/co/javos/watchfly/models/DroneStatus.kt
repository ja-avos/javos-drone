package co.javos.watchfly.models

import android.location.Location
import com.google.gson.JsonObject
import com.google.gson.JsonParser

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
    LANDING,
    GOING_HOME,
    ERROR
}

class DroneStatus(
    var state: DroneState = DroneState.FLYING,
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
            DroneState.TAKING_OFF,
            DroneState.FLYING,
            DroneState.CONFIRM_LANDING,
            DroneState.LANDING,
            DroneState.GOING_HOME,
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
        if (location != null) {
            jsonObject.addProperty("latitude", location!!.latitude)
            jsonObject.addProperty("longitude", location!!.longitude)
        }

        if (homeLocation != null) {
            jsonObject.addProperty("homeLatitude", homeLocation!!.latitude)
            jsonObject.addProperty("homeLongitude", homeLocation!!.longitude)
        }
        return jsonObject
    }

    companion object {
        fun fromJsonObject(jsonObject: JsonObject): DroneStatus {
            val state = DroneState.valueOf(jsonObject.get("state").asString)
            val battery = jsonObject.get("battery").asInt
            val altitude = jsonObject.get("altitude").asFloat
            val verticalSpeed = jsonObject.get("verticalSpeed").asFloat
            val horizontalSpeed = jsonObject.get("horizontalSpeed").asFloat
            val rotation = jsonObject.get("rotation").asFloat
            val signalStrength = jsonObject.get("signalStrength").asInt
            val gpsSignalStrength = jsonObject.get("gpsSignalStrength").asInt

            var location: Location? = null

            if (jsonObject.has("latitude") && jsonObject.has("longitude")) {
                location = Location("")
                location.latitude = jsonObject.get("latitude").asDouble
                location.longitude = jsonObject.get("longitude").asDouble
            }

            var homeLocation: Location? = null

            if (jsonObject.has("homeLatitude") && jsonObject.has("homeLongitude")) {
                homeLocation = Location("")
                homeLocation.latitude = jsonObject.get("homeLatitude").asDouble
                homeLocation.longitude = jsonObject.get("homeLongitude").asDouble
            }

            return DroneStatus(
//                state = state, TODO Remove comment
                state = DroneState.FLYING,
                battery = battery,
                altitude = altitude,
                verticalSpeed = verticalSpeed,
                horizontalSpeed = horizontalSpeed,
                rotation = rotation,
                signalStrength = signalStrength,
                gpsSignalStrength = gpsSignalStrength,
                location = location,
                homeLocation = homeLocation
            )
        }

        fun fromString(string: String): DroneStatus {
            val json = JsonParser.parseString(string).asJsonObject
            return fromJsonObject(json)
        }
    }
}