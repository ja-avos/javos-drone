package co.javos.watchfly.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.javos.watchfly.repository.PhoneMessageConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AltitudeControlViewModel(private val phoneMessageConnection: PhoneMessageConnection) :
    ViewModel() {

    val droneStatus = phoneMessageConnection.droneStatus

    fun sendAltitudeVelocity(altitude: Float) {
        CoroutineScope(Dispatchers.IO).launch {
            phoneMessageConnection.sendCommand("move_altitude", listOf(altitude.toString()))
        }

    }

    class AltitudeControlViewModelFactory(private val phoneMessageConnection: PhoneMessageConnection) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AltitudeControlViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AltitudeControlViewModel(phoneMessageConnection) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}