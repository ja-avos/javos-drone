package co.javos.watchfly.repository

import android.util.Log
import co.javos.watchfly.models.Command
import co.javos.watchfly.models.CommandType
import co.javos.watchfly.models.DroneState
import co.javos.watchfly.models.DroneStatus
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PhoneMessageConnection(
    private val dataClient: DataClient,
    private val messageClient: MessageClient,
    private val capabilityClient: CapabilityClient
) : DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

    private val TAG = "PhoneMessageConnection"

    private var capabilityInfo: CapabilityInfo? = null

    private val _droneStatus: MutableStateFlow<DroneStatus> = MutableStateFlow(DroneStatus())
    val droneStatus: MutableStateFlow<DroneStatus> = _droneStatus

    private val _isPhoneConnected: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isPhoneConnected: MutableStateFlow<Boolean> = _isPhoneConnected

    private fun processCommand(command: Command) {
        Log.d(TAG, "processCommand: $command")
        if (command.type == CommandType.STATUS_UPDATE) {
            try {
                val droneStatus = DroneStatus.fromString(command.content)
                _droneStatus.value = droneStatus
            } catch (e: Exception) {
                Log.e(TAG, "processCommand: ${e.message}")
            }
        }
    }

    override fun onDataChanged(p0: DataEventBuffer) {
        TODO("Not yet implemented")
    }

    override fun onMessageReceived(event: MessageEvent) {
        val message = String(event.data)
        Log.d(TAG, "onMessageReceived: ${message}")
        try {
            val receivedCommand = Command.fromString(message)
            processCommand(receivedCommand)
            Log.d("WatchChatViewModel", "Received command: $receivedCommand")
        } catch (e: Exception) {
            Log.e("WatchChatViewModel", "Error sending message: ${e.message}")
        }
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        Log.d(TAG, "onCapabilityChanged: $capabilityInfo")

        this.capabilityInfo = capabilityInfo

        _isPhoneConnected.value =
            capabilityInfo.nodes.isNotEmpty() && capabilityInfo.nodes.first().isNearby
        Log.d(TAG, "Connected to phone: ${_isPhoneConnected.value}")
    }

    private fun sendMessage(message: Command) {
        Log.d(TAG, "sendMessage: $message")
        Log.d(TAG, "sendMessage: capabilityInfo: $capabilityInfo ${capabilityInfo?.nodes}")
        val nodes = capabilityInfo?.nodes
        nodes?.forEach { node ->
            Log.d(TAG, "sendMessage: NODE ID: ${node.id} NODE IS NEARBY: ${node.isNearby}")
            messageClient.sendMessage(node.id, "/message", message.toString().toByteArray())
        }
    }

    suspend fun sendCommand(command: String) {
        when (command) {
            "motors_on" -> {
                _droneStatus.value = _droneStatus.value.copy(state = DroneState.MOTORS_ON)
                sendMessage(Command(
                    CommandType.ACTION,
                    "motors_on"
                ))
            }

            "motors_off" -> {
                _droneStatus.value = _droneStatus.value.copy(state = DroneState.MOTORS_OFF)
                sendMessage(Command(
                    CommandType.ACTION,
                    "motors_off"
                ))
            }

            "flying" -> {
                _droneStatus.value = _droneStatus.value.copy(state = DroneState.FLYING)
                sendMessage(Command(
                    CommandType.ACTION,
                    "flying"
                ))
            }

            "land" -> {
                _droneStatus.value = _droneStatus.value.copy(state = DroneState.LANDING)
                sendMessage(Command(
                    CommandType.ACTION,
                    "land"
                ))
                delay(1000)
                _droneStatus.value = _droneStatus.value.copy(state = DroneState.MOTORS_OFF)
            }

            "take_off" -> {
                _droneStatus.value = _droneStatus.value.copy(state = DroneState.TAKING_OFF)
                sendMessage(Command(
                    CommandType.ACTION,
                    "take_off"
                ))
                delay(1000)
                _droneStatus.value = _droneStatus.value.copy(state = DroneState.FLYING)
            }

            "rth" -> {
                _droneStatus.value = _droneStatus.value.copy(state = DroneState.GOING_HOME)
                sendMessage(Command(
                    CommandType.ACTION,
                    "go_home"
                ))
                delay(1000)
                _droneStatus.value = _droneStatus.value.copy(state = DroneState.MOTORS_OFF)
            }

            "stop" -> {
                sendMessage(Command(
                    CommandType.ACTION,
                    "stop"
                ))
                _droneStatus.value = _droneStatus.value.copy(state = DroneState.FLYING)
            }

            else -> {
                Log.d("MainActivity", "Unknown command: $command")
            }
        }
    }
}