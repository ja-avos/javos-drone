package co.javos.watchfly.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.javos.watchfly.repository.PhoneMessageConnection

class AltitudeControlViewModel(private val phoneMessageConnection: PhoneMessageConnection) :
    ViewModel() {

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