package co.javos.watchflyphoneapp.repository

import co.javos.watchflyphoneapp.models.DroneStatus

interface DroneController {
    fun connect()
    fun disconnect()
    fun sendCommand(command: String)
    fun getDroneStatus(): DroneStatus
}
