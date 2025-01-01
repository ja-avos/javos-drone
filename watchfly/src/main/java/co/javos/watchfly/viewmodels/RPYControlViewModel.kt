package co.javos.watchfly.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.javos.watchfly.repository.PhoneMessageConnection

class RPYControlViewModel(private val phoneMessageConnection: PhoneMessageConnection) : ViewModel() {
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