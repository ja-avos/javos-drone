package co.javos.watchflyphoneapp.repository

import co.javos.watchflyphoneapp.models.DroneStatus
import dji.sdk.sdkmanager.DJISDKManager

class DJIController(private val manager: DJISDKManager): DroneController {



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
}
