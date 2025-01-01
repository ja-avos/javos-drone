package co.javos.watchflyphoneapp.repository

import android.app.Activity
import android.location.Location
import android.util.Log
import androidx.compose.ui.text.font.FontVariation
import co.javos.watchflyphoneapp.models.DroneState
import co.javos.watchflyphoneapp.models.DroneStatus
import co.javos.watchflyphoneapp.models.RemoteType
import co.javos.watchflyphoneapp.models.Stick
import co.javos.watchflyphoneapp.models.VirtualSticks
import dji.common.battery.BatteryState
import dji.common.camera.SettingsDefinitions
import dji.common.error.DJIError
import dji.common.error.DJISDKError
import dji.common.flightcontroller.FlightControllerState
import dji.common.flightcontroller.virtualstick.FlightControlData
import dji.common.healthmanager.WarningLevel
import dji.sdk.airlink.AirLink
import dji.sdk.base.BaseComponent
import dji.sdk.base.BaseProduct
import dji.sdk.base.BaseProduct.ComponentKey
import dji.sdk.base.DJIDiagnostics
import dji.sdk.battery.Battery
import dji.sdk.camera.Camera
import dji.sdk.flightcontroller.FlightController
import dji.sdk.products.Aircraft
import dji.sdk.remotecontroller.RemoteController
import dji.sdk.sdkmanager.DJISDKInitEvent
import dji.sdk.sdkmanager.DJISDKManager
import dji.sdk.sdkmanager.DJISDKManager.SDKManagerCallback
import dji.thirdparty.afinal.core.AsyncTask
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs

class DJIController(private val manager: DJISDKManager, private val activity: Activity) :
    DroneController, SDKManagerCallback {

    private val _droneStatus = MutableStateFlow(DroneStatus())
    val droneStatus: MutableStateFlow<DroneStatus> = _droneStatus

    private val _virtualSticks = MutableStateFlow(VirtualSticks())
    val virtualSticks: MutableStateFlow<VirtualSticks> = _virtualSticks

    val isCameraReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isDroneConnected: MutableStateFlow<Boolean> = MutableStateFlow(false)

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
            baseProduct.setDiagnosticsInformationCallback { diagnostics ->
                Log.d(TAG, "Drone Diagnostics: $diagnostics")
                for (diagnostic in diagnostics) {
                    Log.d(TAG, "Drone Diagnostic: $diagnostic")
                    Log.d(
                        TAG,
                        "Drone Diagnostic Level: ${diagnostic.healthInformation?.warningLevel}"
                    )
                }

            }
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

        state = if (flightState.isFlying) {
            DroneState.FLYING
        } else {
            if (flightState.areMotorsOn()) {
                DroneState.MOTORS_ON
            } else {
                DroneState.MOTORS_OFF
            }
        }

        if (flightState.isGoingHome) {
            state = DroneState.GOING_HOME
        }

        if (flightState.isLandingConfirmationNeeded) {
            state = DroneState.CONFIRM_LANDING
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
                if (newComponent != null && newComponent.isConnected) {
                    (newComponent as FlightController).setStateCallback {
                        onStatusUpdate(it)
                    }
                    isDroneConnected.value = true
                } else {
                    isDroneConnected.value = false
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
                    (newComponent as AirLink).setUplinkSignalQualityCallback {
                        onAirLinkStatusUpdate(it)
                    }
                }
            }

            ComponentKey.CAMERA -> {
                if (newComponent != null && newComponent.isConnected && newComponent is Camera) {
                    isCameraReady.value = false

                    newComponent.setFlatMode(SettingsDefinitions.FlatCameraMode.PHOTO_SMART) { error ->
                        Log.d(TAG, "Setting flat mode: ${error ?: "success"}")
                        if (error != null) {
                            Log.d(TAG, "Error setting flat mode: $error")
                            isCameraReady.value = false
                        } else {
                            isCameraReady.value = true
                        }
                    }

                } else {
                    isCameraReady.value = false
                }
            }

            ComponentKey.REMOTE_CONTROLLER -> {
                if (newComponent != null && newComponent.isConnected && newComponent is RemoteController) {
                    Log.d(TAG, "Registering remote sticks listener")
                    registerRemoteSticksListener(newComponent)
                }
            }

            else -> {
                Log.d(TAG, "Unknown Component: ${key?.name}")
            }
        }
    }

    override fun onInitProcess(p0: DJISDKInitEvent?, p1: Int) {
        Log.d(TAG, "onInitProcess")
    }

    override fun onDatabaseDownloadProgress(p0: Long, p1: Long) {
        Log.d(TAG, "onDatabaseDownloadProgress")
    }

    fun turnMotorsOn() {
        Log.d(TAG, "turnMotorsOn: ")
        val flightController = (manager.product as Aircraft?)?.flightController
        flightController?.turnOnMotors {
            Log.d(TAG, "turnMotorsOn: ${it?.description ?: "success"}")
        }
    }

    fun turnMotorsOff() {
        Log.d(TAG, "turnMotorsOff: ")
        val flightController = (manager.product as Aircraft?)?.flightController
        flightController?.turnOffMotors {
            Log.d(TAG, "turnMotorsOff: ${it?.description ?: "success"}")
        }
    }

    fun takePhoto() {
        val camera = manager.product?.camera
        Log.d(TAG, "takePhoto: Camera ready ${isCameraReady.value} and camera $camera")
        if (isCameraReady.value) {
            camera?.startShootPhoto { error ->
                Log.d(TAG, "takePhoto: ${error?.description ?: "success"}")
                if (error != null) {
                    Log.d(TAG, "Error taking photo: $error")
                }
            }
        }
    }

    fun stopDrone() {
        Log.d(TAG, "STOP Drone: ")
        if (isDroneConnected.value) {
            val flightController = (manager.product as Aircraft?)?.flightController

            Log.d(TAG, "STOP Drone: Stopping flight controller")

            flightController?.cancelGoHome { error ->
                Log.d(TAG, "cancelGoHome: ${error?.description ?: "success"}")
                flightController.cancelLanding { error2 ->
                    Log.d(TAG, "cancelLanding: ${error2?.description ?: "success"}")

                    flightController.cancelTakeoff { error3 ->
                        Log.d(TAG, "cancelTakeOff: ${error3?.description ?: "success"}")
                    }
                }
            }
        } else {
            Log.d(TAG, "STOP Drone: Drone not connected")
        }
    }

    fun returnToHome() {
        Log.d(TAG, "returnToHome: ")
        if (!isDroneConnected.value) {
            val flightController = (manager.product as Aircraft?)?.flightController
            flightController?.startGoHome {
                Log.d(TAG, "returnToHome: ${it?.description ?: "success"}")
            }
        } else {
            Log.d(TAG, "returnToHome: Drone not connected")
        }
    }

    fun takeOff() {
        Log.d(TAG, "takeOff: ")
        if (!isDroneConnected.value) {
            _droneStatus.value = _droneStatus.value.toState(DroneState.TAKING_OFF)
            val flightController = (manager.product as Aircraft?)?.flightController
            flightController?.startTakeoff {
                Log.d(TAG, "takeOff: ${it?.description ?: "success"}")
            }
        } else {
            Log.d(TAG, "takeOff: Drone not connected")
        }
    }

    fun landDrone() {
        Log.d(TAG, "landDrone: ")
        if (!isDroneConnected.value) {
            val flightController = (manager.product as Aircraft?)?.flightController
            flightController?.startLanding {
                Log.d(TAG, "landDrone: ${it?.description ?: "success"}")
            }
        } else {
            Log.d(TAG, "landDrone: Drone not connected")
        }
    }

    fun confirmLanding() {
        Log.d(TAG, "confirmLanding: ")
        if (!isDroneConnected.value) {
            val flightController = (manager.product as Aircraft?)?.flightController
            if (flightController?.state?.isLandingConfirmationNeeded == true) {
                flightController.confirmLanding {
                    Log.d(TAG, "confirmLanding: ${it?.description ?: "success"}")
                }
            } else {
                Log.d(TAG, "confirmLanding: Landing not needed")
            }
        } else {
            Log.d(TAG, "confirmLanding: Drone not connected")
        }
    }

    fun registerRemoteSticksListener(remoteController: RemoteController) {
        remoteController.setHardwareStateCallback { state ->
            val maxLimit = 660
            _virtualSticks.value = VirtualSticks(
                RemoteType.REMOTE_CONTROLLER,
                Stick(
                    (state.rightStick?.horizontalPosition ?: 0) * 100 / maxLimit,
                    (state.rightStick?.verticalPosition ?: 0) * 100 / maxLimit
                ),
                Stick(
                    (state.leftStick?.horizontalPosition ?: 0) * 100 / maxLimit,
                    (state.leftStick?.verticalPosition ?: 0) * 100 / maxLimit
                )
            )
        }
        Log.d(TAG, "Remote hardware state callback registered successfully")

    }

//    fun changeVirtualSticks() {
//
//        val flightControlData = FlightControlData()
//
//        (manager.product as Aircraft?)?.flightController?.sendVirtualStickFlightControlData() {
//
//        }
//    }

}
