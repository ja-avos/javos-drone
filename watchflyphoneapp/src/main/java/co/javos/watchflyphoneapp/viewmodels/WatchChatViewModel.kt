package co.javos.watchflyphoneapp.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.javos.watchflyphoneapp.models.Alert
import co.javos.watchflyphoneapp.models.AuthorDevice
import co.javos.watchflyphoneapp.models.Command
import co.javos.watchflyphoneapp.models.CommandType
import co.javos.watchflyphoneapp.models.Message
import co.javos.watchflyphoneapp.repository.DJIController
import co.javos.watchflyphoneapp.repository.WatchMessageConnection
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import dji.common.flightcontroller.virtualstick.FlightControlData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class WatchChatViewModel(
    private val watchMessageConnection: WatchMessageConnection,
    private val djiController: DJIController
) : ViewModel(), MessageClient.OnMessageReceivedListener {
    private val _messages = mutableStateListOf<Message>()
    val messages: MutableList<Message> = _messages

    private val _virtualFlightControlData = MutableStateFlow(FlightControlData(0F, 0F, 0F, 0F))
    val virtualFlightControlData: MutableStateFlow<FlightControlData> = _virtualFlightControlData

    init {
        viewModelScope.launch {
            djiController.droneStatus.collect { value ->
                // Handle state updates
                Log.d("WatchChatViewModel", "Drone Status Changed sending message")
                val json = value.toJsonObject()
                val message = Message(
                    content = json.toString()
                )
                addMessage(message)
            }
        }
    }

    fun addMessage(message: Message, type: CommandType = CommandType.STATUS_UPDATE) {
        try {
            watchMessageConnection.sendMessageToWatch(
                Command(
                    type = type,
                    content = message.content
                )
            )
            _messages.add(message)
        } catch (e: Exception) {
            Log.e("WatchChatViewModel", "Error adding message: ${e.message}")
        }
    }

    fun addAlert(alert: Alert) {
        Log.d("WatchChatViewModel", "Adding alert: $alert")
        val message = Message(
            content = alert.toString()
        )
        addMessage(
            message,
            CommandType.ALERT
        )
    }

    fun clearMessages() {
        _messages.clear()
    }

    fun removeMessage(message: Message) {
        _messages.remove(message)
    }

    fun updateMessage(message: Message) {
        val index = _messages.indexOf(message)
        if (index != -1) {
            _messages[index] = message
        }
    }

    fun getMessage(index: Int): Message {
        return _messages[index]
    }

    fun getMessageCount(): Int {
        return _messages.size
    }

    private fun processCommand(command: Command) {
        if (command.type == CommandType.ACTION) {
            when (command.content.split(" ").first()) {
                "motors_on" -> {
                    djiController.turnMotorsOn()
                }

                "motors_off" -> {
                    djiController.turnMotorsOff()
                }

                "take_off" -> {
                    djiController.takeOff()
                }

                "land" -> {
                    djiController.landDrone()
                }

                "confirm_landing" -> {
                    djiController.confirmLanding()
                }

                "cancel_landing" -> {
                    djiController.cancelLanding()
                }

                "go_home" -> {
                    djiController.returnToHome()
                }

                "stop" -> {
                    djiController.stopDrone()
                }

                "move_rpy" -> {
                    val args = command.content.split(" ")
                    if (args.size == 4) {
                        val x = args[1].toFloatOrNull()
                        val y = args[2].toFloatOrNull()
                        val z = args[3].toFloatOrNull()
                        Log.d("WatchChatViewModel", "Decoded RPY: $x, $y, $z")
                        if (x != null && y != null && z != null) {
                            Log.d("WatchChatViewModel", "Moving RPY: $x, $y, $z")
                            _virtualFlightControlData.value =
                                djiController.changeRPY(x, y, z) ?: FlightControlData(
                                    0F,
                                    0F,
                                    0F,
                                    0F
                                )
                        } else {
                            Log.e("WatchChatViewModel", "Invalid arguments for move_rpy command")
                        }
                    } else {
                        Log.e("WatchChatViewModel", "Invalid arguments for move_rpy command")
                    }
                }

                "move_py" -> {
                    val args = command.content.split(" ")
                    if (args.size == 3) {
                        val x = args[1].toFloatOrNull()
                        val y = args[2].toFloatOrNull()
                        if (x != null && y != null) {
                            _virtualFlightControlData.value =
                                djiController.changeRPY(0F, x, y) ?: FlightControlData(
                                    0F,
                                    0F,
                                    0F,
                                    0F
                                )
                        }
                        else {
                            Log.e("WatchChatViewModel", "Invalid arguments for move_py command")
                        }
                    } else {
                        Log.e("WatchChatViewModel", "Invalid arguments for move_py command")
                    }
                }

                "move_altitude" -> {
                    val args = command.content.split(" ")
                    if (args.size == 2) {
                        val z = args[1].toFloatOrNull()
                        if (z != null) {
                            _virtualFlightControlData.value = djiController.changeAltitude(z) ?: FlightControlData(0F, 0F, 0F, 0F)
                        }
                        else {
                            Log.e("WatchChatViewModel", "Invalid arguments for move_altitude command")
                        }
                    } else {
                        Log.e("WatchChatViewModel", "Invalid arguments for move_altitude command")
                    }
                }

                else -> {
                    Log.d("WatchChatViewModel", "Unknown command: ${command.content}")
                }
            }
        }
    }

    class WatchChatViewModelFactory(
        private val watchMessageConnection: WatchMessageConnection,
        private val djiController: DJIController
    ) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WatchChatViewModel::class.java)) {
                return WatchChatViewModel(watchMessageConnection, djiController) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    override fun onMessageReceived(event: MessageEvent) {
        val message = String(event.data)
        var addMessage = true
        Log.d("WatchChatViewModel", "Received message: ${message}")
        try {
            val receivedCommand = Command.fromString(message)
            processCommand(receivedCommand)
            if (receivedCommand.type == CommandType.ACTION) {
                addMessage = false
            }
            Log.d("WatchChatViewModel", "Received command: $receivedCommand")
        } catch (e: Exception) {
            Log.e("WatchChatViewModel", "Error sending message: ${e.message}")
        }
        val newMessage = Message(
            content = message, author = AuthorDevice.WATCH
        )
        if (addMessage)
            addMessage(newMessage)
    }
}
