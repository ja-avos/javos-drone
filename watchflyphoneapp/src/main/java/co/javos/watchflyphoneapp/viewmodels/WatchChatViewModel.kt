package co.javos.watchflyphoneapp.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.javos.watchflyphoneapp.models.AuthorDevice
import co.javos.watchflyphoneapp.models.Message
import co.javos.watchflyphoneapp.repository.WatchMessageConnection
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent

class WatchChatViewModel(private val watchMessageConnection: WatchMessageConnection) : ViewModel(), MessageClient.OnMessageReceivedListener {
    private val _messages = mutableStateListOf<Message>()
    val messages: MutableList<Message> = _messages

    init {
        watchMessageConnection.addMessageListener(this)
    }

    fun addMessage(message: Message) {
        try {
            watchMessageConnection.sendMessageToWatch(message.content)
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

    class WatchChatViewModelFactory(private val watchMessageConnection: WatchMessageConnection) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WatchChatViewModel::class.java)) {
                return WatchChatViewModel(watchMessageConnection) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    override fun onMessageReceived(event: MessageEvent) {
        val message = String(event.data)
        val newMessage = Message(
            content = message,
            author = AuthorDevice.WATCH
        )
        addMessage(newMessage)
    }
}
