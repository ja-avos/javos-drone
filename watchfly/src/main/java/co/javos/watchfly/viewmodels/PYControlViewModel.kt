package co.javos.watchfly.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.javos.watchfly.repository.PhoneMessageConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PYControlViewModel(private val phoneMessageConnection: PhoneMessageConnection) : ViewModel() {

    fun sendPY(x: Float, y: Float) {
        CoroutineScope(Dispatchers.IO).launch {
            phoneMessageConnection.sendCommand(
                "move_py",
                listOf(y.toString(), x.toString())
            )
        }
    }
    class PYControlViewModelFactory(private val phoneMessageConnection: PhoneMessageConnection) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PYControlViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PYControlViewModel(phoneMessageConnection) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}