package co.javos.watchflyphoneapp.repository

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.CapabilityInfo
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable

class WatchMessageConnection(
    private val dataClient: DataClient,
    private val messageClient: MessageClient,
    private val capabilityClient: CapabilityClient
) : DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener, CapabilityClient.OnCapabilityChangedListener {

    private var capabilityInfo: CapabilityInfo? = null

    private var _isConnected = mutableStateOf(false)
    val isWatchConnected: Boolean
        get() = _isConnected.value

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        TODO("Not yet implemented")
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d("WatchMessageConnection", "onMessageReceived: $messageEvent")
    }

    override fun onCapabilityChanged(capabilityInfo: CapabilityInfo) {
        Log.d("WatchMessageConnection", "onCapabilityChanged: $capabilityInfo")

        this.capabilityInfo = capabilityInfo

        _isConnected.value =
            capabilityInfo.nodes.isNotEmpty() && capabilityInfo.nodes.first().isNearby
        Log.d("WatchMessageConnection", "Connected a watch: ${_isConnected.value}")
    }

    fun sendMessageToWatch(message: String) {
        val nodes = capabilityInfo?.nodes
        nodes?.forEach { node ->
            messageClient.sendMessage(node.id, "/message", message.toByteArray())
        }
    }

    fun addMessageListener(listener: MessageClient.OnMessageReceivedListener) {
        messageClient.addListener(listener)
    }
}
