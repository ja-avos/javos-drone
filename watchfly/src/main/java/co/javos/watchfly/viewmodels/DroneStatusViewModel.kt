package co.javos.watchfly.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.javos.watchfly.repository.PhoneMessageConnection

class DroneStatusViewModel(private val phoneMessageConnection: PhoneMessageConnection): ViewModel() {

    val droneStatus = phoneMessageConnection.droneStatus
    val isPhoneConnected = phoneMessageConnection.isPhoneConnected

    class DroneStatusViewModelFactory(private val phoneMessageConnection: PhoneMessageConnection) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DroneStatusViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DroneStatusViewModel(phoneMessageConnection) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
