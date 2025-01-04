package co.javos.watchfly.repository

import android.util.Log
import co.javos.watchfly.MainActivity
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
    private val capabilityClient: CapabilityClient,
    private val mainActivity: MainActivity?
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
        when (command.type) {
            CommandType.STATUS_UPDATE -> {
                try {
                    val droneStatus = DroneStatus.fromString(command.content)
                    _droneStatus.value = droneStatus
                } catch (e: Exception) {
                    Log.e(TAG, "processCommand: ${e.message}")
                }
            }

            CommandType.ALERT -> {
                mainActivity?.showToast("New Alert!!! Check phone app")
            }

            else -> {
                Log.d(TAG, "processCommand: Unknown command type: ${command.type}")
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

    suspend fun sendCommand(command: String, args: List<String> = emptyList()) {
        when (command) {
            "motors_on" -> {
                sendMessage(
                    Command(
                        CommandType.ACTION,
                        "motors_on"
                    )
                )
            }

            "motors_off" -> {
                sendMessage(
                    Command(
                        CommandType.ACTION,
                        "motors_off"
                    )
                )
            }

            "flying" -> {
                sendMessage(
                    Command(
                        CommandType.ACTION,
                        "flying"
                    )
                )
            }

            "land" -> {
                sendMessage(
                    Command(
                        CommandType.ACTION,
                        "land"
                    )
                )
            }

            "take_off" -> {
                sendMessage(
                    Command(
                        CommandType.ACTION,
                        "take_off"
                    )
                )
            }

            "rth" -> {
                sendMessage(
                    Command(
                        CommandType.ACTION,
                        "go_home"
                    )
                )
            }

            "stop" -> {
                sendMessage(
                    Command(
                        CommandType.ACTION,
                        "stop"
                    )
                )
            }

            "confirm_landing" -> {
                sendMessage(
                    Command(
                        CommandType.ACTION,
                        "confirm_landing"
                    )
                )
            }

            "cancel_landing" -> {
                sendMessage(
                    Command(
                        CommandType.ACTION,
                        "cancel_landing"
                    )
                )
            }

            "move_rpy" -> {

                if (args.size != 3) {
                    Log.e("PhoneMessageConnection", "Invalid number of arguments for move_rpy command")
                    return
                }
                sendMessage(
                    Command(
                        CommandType.ACTION,
                        "move_rpy ${args[0]} ${args[1]} ${args[2]}"
                    )
                )
            }

            "move_py" -> {
                if (args.size != 2) {
                    Log.e("PhoneMessageConnection", "Invalid number of arguments for move_py command")
                    return
                }
                sendMessage(
                    Command(
                        CommandType.ACTION,
                        "move_py ${args[0]} ${args[1]}"
                    )
                )
            }

            "move_altitude" -> {
                if (args.size != 1) {
                    Log.e("PhoneMessageConnection", "Invalid number of arguments for move_altitude command")
                    return
                }
                sendMessage(
                    Command(
                        CommandType.ACTION,
                        "move_altitude ${args[0]}"
                    )
                )
            }

            else -> {
                Log.d("MainActivity", "Unknown command: $command")
            }
        }
    }
}