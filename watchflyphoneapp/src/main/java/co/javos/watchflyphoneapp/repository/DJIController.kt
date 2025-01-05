package co.javos.watchflyphoneapp.repository

import android.app.Activity
import android.location.Location
import android.util.Log
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
import dji.common.flightcontroller.simulator.InitializationData
import dji.common.flightcontroller.virtualstick.FlightControlData
import dji.common.flightcontroller.virtualstick.FlightCoordinateSystem
import dji.common.flightcontroller.virtualstick.RollPitchControlMode
import dji.common.flightcontroller.virtualstick.VerticalControlMode
import dji.common.flightcontroller.virtualstick.YawControlMode
import dji.common.model.LocationCoordinate2D
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
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs

class DJIController(private val manager: DJISDKManager, private val activity: Activity) :
    DroneController, SDKManagerCallback {

    private val _droneStatus = MutableStateFlow(DroneStatus())
    val droneStatus: MutableStateFlow<DroneStatus> = _droneStatus

    private val _virtualSticks = MutableStateFlow(VirtualSticks())
    val virtualSticks: MutableStateFlow<VirtualSticks> = _virtualSticks

    private val _diagnostics = MutableStateFlow<List<DJIDiagnostics>>(emptyList())
    val diagnostics: MutableStateFlow<List<DJIDiagnostics>> = _diagnostics

    val isCameraReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isDroneConnected: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val TAG = "DJIController"

    private val isRegistrationInProgress: AtomicBoolean = AtomicBoolean(false)

    init {
        this.startSDKRegistration()
    }

    private fun startSDKRegistration() {
        if (isRegistrationInProgress.compareAndSet(false, true)) {
            AsyncTask.execute {
                Log.d(TAG, "registering, pls wait...")
                _droneStatus.value = _droneStatus.value.toState(DroneState.SDK_INITIALIZING)
                manager.registerApp(activity.applicationContext, this)
            }
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
        // Register diagnostics callback
        if (baseProduct?.isConnected == true) {
            Log.d(TAG, "Adding diagnostics callback")
            baseProduct.setDiagnosticsInformationCallback {
                _diagnostics.value = it.toList()
            }
        }
    }

    override fun onProductChanged(baseProduct: BaseProduct?) {
        Log.d(TAG, String.format("onProductChange newProduct:%s", baseProduct))

        // Register diagnostics callback
        if (baseProduct?.isConnected == true) {
            Log.d(TAG, "Adding diagnostics callback")
            baseProduct.setDiagnosticsInformationCallback {
                _diagnostics.value = it.toList()
            }
        }

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

        var state: DroneState

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
//                    newComponent.simulator.start(
//                        InitializationData.createInstance(
//                            LocationCoordinate2D(
//                                4.0, -72.0
//                            ),
//                            50,
//                            10
//                        )
//                    ) {
//                        Log.d(TAG, "onComponentChange: simulator callback ${it?.description ?: "success"}")
//                    }
                    newComponent.setVirtualStickModeEnabled(true) {
                        Log.d(TAG, "onComponentChange: enabling virtual stick callback ${it?.description ?: "success"}")
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
                    newComponent.setUplinkSignalQualityCallback {
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

            flightController?.setVirtualStickModeEnabled(false) {
                Log.d(TAG, "STOP Drone: virtual stick mode disabled ${it?.description ?: "success"}")
            }

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
        if (isDroneConnected.value) {
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
        if (isDroneConnected.value) {
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
        if (isDroneConnected.value) {
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
        if (isDroneConnected.value) {
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

    fun cancelLanding() {
        Log.d(TAG, "cancelLanding: ")
        if (isDroneConnected.value) {
            val flightController = (manager.product as Aircraft?)?.flightController
            if (flightController?.state?.isLandingConfirmationNeeded == true) {
                flightController.cancelLanding {
                    Log.d(TAG, "cancelLanding: ${it?.description ?: "success"}")
                }
            } else {
                Log.d(TAG, "cancelLanding: Landing not needed")
            }
        } else {
            Log.d(TAG, "cancelLanding: Drone not connected")
        }
    }

    private fun registerRemoteSticksListener(remoteController: RemoteController) {
        remoteController.setHardwareStateCallback { state ->
            // Disable virtual stick mode
            val controller = (manager.product as Aircraft?)?.flightController

            controller?.setVirtualStickModeEnabled(false) {
                Log.d(TAG, "registerRemoteSticksListener: virtual stick mode disabled ${it?.description ?: "success"}")
            }

            val maxLimit = 660
            _virtualSticks.value = VirtualSticks(
                RemoteType.REMOTE_CONTROLLER,
                Stick(
                    (state.rightStick?.horizontalPosition ?: 0) * 100F / maxLimit,
                    (state.rightStick?.verticalPosition ?: 0) * 100F / maxLimit
                ),
                Stick(
                    (state.leftStick?.horizontalPosition ?: 0) * 100F / maxLimit,
                    (state.leftStick?.verticalPosition ?: 0) * 100F / maxLimit
                )
            )
        }
        Log.d(TAG, "Remote hardware state callback registered successfully")

    }

    // Param velocity must be a number between -1 and 1
    fun changeAltitude(velocity: Float): FlightControlData? {
        // According to DJI SDK docs, the maximum velocity is (+/-) 4m/s, limiting to 2
        val MAX_VELOCITY = 2F
        if (velocity > 1 || velocity < -1) {
            Log.d(TAG, "changeAltitude: velocity must be between -1 and 1")
            return null
        }
        val controller = (manager.product as Aircraft?)?.flightController

        // Enable virtual stick mode
        controller?.setVirtualStickModeEnabled(true) {
            Log.d(TAG, "changeRPY: virtual stick mode enabled ${it?.description ?: "success"}")
        }

        controller?.verticalControlMode = VerticalControlMode.VELOCITY
        val controlData = FlightControlData(
            0F,
            0F,
            0F,
            velocity * MAX_VELOCITY
        )
        controller?.sendVirtualStickFlightControlData(controlData) {
            Log.d(TAG, "changeAltitude: ${it?.description ?: "success"}")
        }
        return FlightControlData(
            0F,
            0F,
            0F,
            velocity
        )
    }

    // Each param must be a number between -1 and 1
    fun changeRPY(roll: Float, pitch: Float, yaw: Float): FlightControlData? {

        if (roll > 1 || roll < -1 || pitch > 1 || pitch < -1 || yaw > 1 || yaw < -1) {
            Log.d(TAG, "changeRPY: params must be between -1 and 1")
            return null
        }
        val controller = (manager.product as Aircraft?)?.flightController

        if (controller == null) {
            Log.d(TAG, "changeRPY: controller is null")
//            return null
        }

        // Enable virtual stick mode
        controller?.setVirtualStickModeEnabled(true) {
            Log.d(TAG, "changeRPY: virtual stick mode enabled ${it?.description ?: "success"}")
        }

        val MAX_ROLL_PITCH_ANGLE = 30F
        val MAX_YAW_ANGLE = 180F
        val MAX_YAW_ANGULAR_VELOCITY = 100F

        controller?.yawControlMode = YawControlMode.ANGULAR_VELOCITY
        controller?.rollPitchControlMode = RollPitchControlMode.ANGLE
        controller?.rollPitchCoordinateSystem = FlightCoordinateSystem.BODY

        val controlData = FlightControlData(
            pitch * MAX_ROLL_PITCH_ANGLE * -1,
            roll * MAX_ROLL_PITCH_ANGLE,
            yaw * MAX_YAW_ANGULAR_VELOCITY,
            0F
        )

        controller?.sendVirtualStickFlightControlData(controlData) {
            Log.d(TAG, "changeRPY: ${it?.description ?: "success"}")
        }

        return FlightControlData(
            pitch,
            roll,
            yaw,
            0F
        )
    }

}
