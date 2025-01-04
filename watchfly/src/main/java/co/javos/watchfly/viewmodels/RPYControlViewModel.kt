package co.javos.watchfly.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.javos.watchfly.repository.PhoneMessageConnection

class RPYControlViewModel(private val phoneMessageConnection: PhoneMessageConnection) : ViewModel() {

    fun sendRPY(x: Float, y: Float, z: Float) {
//        phoneMessageConnection.sendRPY(x, y, z)
        Log.d("RPYControlViewModel", "RPY: $x, $y, $z")
    }

    class RPYControlViewModelFactory(private val phoneMessageConnection: PhoneMessageConnection) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RPYControlViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RPYControlViewModel(phoneMessageConnection) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}