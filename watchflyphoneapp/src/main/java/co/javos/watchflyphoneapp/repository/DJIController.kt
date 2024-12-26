package co.javos.watchflyphoneapp.repository

import android.app.Activity
import android.location.Location
import android.util.Log
import co.javos.watchflyphoneapp.models.DroneState
import co.javos.watchflyphoneapp.models.DroneStatus
import dji.common.battery.BatteryState
import dji.common.error.DJIError
import dji.common.error.DJISDKError
import dji.common.flightcontroller.FlightControllerState
import dji.sdk.airlink.AirLink
import dji.sdk.base.BaseComponent
import dji.sdk.base.BaseProduct
import dji.sdk.base.BaseProduct.ComponentKey
import dji.sdk.battery.Battery
import dji.sdk.flightcontroller.FlightController
import dji.sdk.products.Aircraft
import dji.sdk.sdkmanager.DJISDKInitEvent
import dji.sdk.sdkmanager.DJISDKManager
import dji.sdk.sdkmanager.DJISDKManager.SDKManagerCallback
import dji.thirdparty.afinal.core.AsyncTask
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs

class DJIController(private val manager: DJISDKManager, private val activity: Activity) :
    DroneController, SDKManagerCallback {

    private val _droneStatus = MutableStateFlow(DroneStatus())
    val droneStatus: MutableStateFlow<DroneStatus> = _droneStatus

    private val TAG = "DJIController"

    private val isRegistrationInProgress: AtomicBoolean = AtomicBoolean(false)

    init {
        this.startSDKRegistration()
    }

    private fun startSDKRegistration() {
        if (isRegistrationInProgress.compareAndSet(false, true)) {
            AsyncTask.execute(Runnable {
                Log.d(TAG, "registering, pls wait...")
                _droneStatus.value = _droneStatus.value.toState(DroneState.SDK_INITIALIZING)
                manager.registerApp(activity.applicationContext, this)
            })
        }
    }

    override fun connect() {
        TODO("Not yet implemented")
    }

    override fun disconnect() {
        TODO("Not yet implemented")
    }

    override fun sendCommand(command: String) {
        TODO("Not yet implemented")
    }

    override fun getDroneStatus(): DroneStatus {
        TODO("Not yet implemented")
    }

    override fun onRegister(djiError: DJIError?) {
        Log.d(TAG, "onRegister")
        if (djiError === DJISDKError.REGISTRATION_SUCCESS) {
            Log.d(TAG, "Register Success")
            _droneStatus.value = _droneStatus.value.toState(DroneState.NO_REMOTE)
            manager.startConnectionToProduct()
        } else {
            Log.d(TAG, "Register sdk fails, please check the bundle id and network connection!")
            Log.d(TAG, djiError?.description ?: "No Error")
            _droneStatus.value = _droneStatus.value.toState(DroneState.SDK_ERROR)
        }
        Log.v(TAG, djiError?.description ?: "No Error")
        isRegistrationInProgress.set(false)
    }

    override fun onProductDisconnect() {
        Log.d(TAG, "onProductDisconnect")
        _droneStatus.value = _droneStatus.value.toState(DroneState.NO_REMOTE)
    }

    override fun onProductConnect(baseProduct: BaseProduct?) {
        Log.d(TAG, String.format("onProductConnect newProduct:%s", baseProduct))
        _droneStatus.value = _droneStatus.value.toState(DroneState.NO_DRONE)
    }

    override fun onProductChanged(baseProduct: BaseProduct?) {
        Log.d(TAG, String.format("onProductChange newProduct:%s", baseProduct))
        if (baseProduct?.isConnected == true && baseProduct is Aircraft) {
            Log.d(TAG, "onProductChanged: connected")
            _droneStatus.value = _droneStatus.value.toState(DroneState.MOTORS_OFF)
        } else {
            _droneStatus.value = _droneStatus.value.toState(DroneState.NO_DRONE)
        }
    }

    private fun onStatusUpdate(flightState: FlightControllerState) {

        // Create a Location object to store the latitude and longitude
        val altitude = flightState.aircraftLocation.altitude
        val droneLocation = Location("drone")
        droneLocation.latitude = flightState.aircraftLocation.latitude
        droneLocation.longitude = flightState.aircraftLocation.longitude

        val homeLocation = Location("home")
        homeLocation.longitude = flightState.homeLocation.longitude
        homeLocation.latitude = flightState.homeLocation.latitude

        val gpsSignalStrength = flightState.satelliteCount

        var state = _droneStatus.value.state

        if (flightState.areMotorsOn()) {
            state = DroneState.MOTORS_ON
        } else {
            state = DroneState.MOTORS_OFF
        }

        if (flightState.isFlying) {
            state = DroneState.FLYING
        }

        val verticalSpeed = flightState.velocityZ * -1
        val horizontalSpeed = abs(flightState.velocityX + flightState.velocityY)

        _droneStatus.value = _droneStatus.value.copy(
            state = state,
            altitude = altitude,
            location = droneLocation,
            verticalSpeed = verticalSpeed,
            horizontalSpeed = horizontalSpeed,
            gpsSignalStrength = gpsSignalStrength,
            homeLocation = homeLocation
        )
    }

    private fun onBatteryStatusUpdate(batteryState: BatteryState) {
        val batteryLevel = batteryState.chargeRemainingInPercent

        _droneStatus.value = _droneStatus.value.copy(
            battery = batteryLevel
        )
    }

    private fun onAirLinkStatusUpdate(signalQuality: Int) {
        Log.d(TAG, "onAirLinkStatusUpdate: $signalQuality")
        _droneStatus.value = _droneStatus.value.copy(
            signalStrength = signalQuality
        )
    }

    override fun onComponentChange(
        key: ComponentKey?, oldComponent: BaseComponent?, newComponent: BaseComponent?
    ) {
        newComponent?.setComponentListener { isConnected ->
            Log.d(TAG, "onComponentConnectivityChanged: $isConnected")
        }
        Log.d(
            TAG, String.format(
                "onComponentChange key:%s, oldComponent:%s, newComponent:%s",
                key,
                oldComponent,
                newComponent
            )
        )
        when (key) {
            ComponentKey.FLIGHT_CONTROLLER -> {
                if (newComponent != null) {
                    (newComponent as FlightController).setStateCallback {
                        onStatusUpdate(it)
                    }
                }
            }

            ComponentKey.BATTERY -> {
                if (newComponent != null) {
                    (newComponent as Battery).setStateCallback {
                        onBatteryStatusUpdate(it)
                    }
                }
            }

            ComponentKey.AIR_LINK -> {
                if (newComponent != null && newComponent is AirLink) {
                    Log.d(TAG, "onComponentChange: AirLink")
                    Log.d(TAG, "AirLink: ${newComponent.isConnected}")
                    Log.d(TAG, "AirLink: ${newComponent.isOcuSyncLinkSupported}")
                    (newComponent as AirLink).setUplinkSignalQualityCallback {
                        onAirLinkStatusUpdate(it)
                    }
                }
            }

            else -> {
                Log.d(TAG, "Unknown Component")
            }
        }
    }

    override fun onInitProcess(p0: DJISDKInitEvent?, p1: Int) {
        Log.d(TAG, "onInitProcess")
    }

    override fun onDatabaseDownloadProgress(p0: Long, p1: Long) {
        Log.d(TAG, "onDatabaseDownloadProgress")
    }


}
