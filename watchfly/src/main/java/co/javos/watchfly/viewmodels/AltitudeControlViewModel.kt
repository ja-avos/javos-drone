package co.javos.watchfly.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.javos.watchfly.repository.PhoneMessageConnection

class AltitudeControlViewModel(private val phoneMessageConnection: PhoneMessageConnection) :
    ViewModel() {

        fun sendAltitudeVelocity(altitude: Float) {
            Log.d("AltitudeControlViewModel", "Sending altitude velocity: $altitude")
//            phoneMessageConnection.sendAltitudeVelocity(altitude)
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