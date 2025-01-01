package co.javos.watchflyphoneapp.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.javos.watchflyphoneapp.models.AuthorDevice
import co.javos.watchflyphoneapp.models.Command
import co.javos.watchflyphoneapp.models.CommandType
import co.javos.watchflyphoneapp.models.Message
import co.javos.watchflyphoneapp.repository.DJIController
import co.javos.watchflyphoneapp.repository.WatchMessageConnection
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import kotlinx.coroutines.launch

class WatchChatViewModel(
    private val watchMessageConnection: WatchMessageConnection,
    private val djiController: DJIController
) : ViewModel(), MessageClient.OnMessageReceivedListener {
    private val _messages = mutableStateListOf<Message>()
    val messages: MutableList<Message> = _messages

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

    fun addMessage(message: Message) {
        try {
            watchMessageConnection.sendMessageToWatch(
                Command(
                    type = CommandType.STATUS_UPDATE,
                    content = message.content
                )
            )
            _messages.add(message)
        } catch (e: Exception) {
            Log.e("WatchChatViewModel", "Error adding message: ${e.message}")
        }
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
            when (command.content) {
                "motors_on" -> {
                    djiController.turnMotorsOn()
                    djiController.droneStatus
                }

                "motors_off" -> {
                    djiController.turnMotorsOff()
                    djiController.droneStatus
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
        Log.d("WatchChatViewModel", "Received message: ${message}")
        try {
            val receivedCommand = Command.fromString(message)
            processCommand(receivedCommand)
            Log.d("WatchChatViewModel", "Received command: $receivedCommand")
        } catch (e: Exception) {
            Log.e("WatchChatViewModel", "Error sending message: ${e.message}")
        }
        val newMessage = Message(
            content = message, author = AuthorDevice.WATCH
        )
        addMessage(newMessage)
    }
}
