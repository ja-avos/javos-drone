package co.javos.watchfly.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.javos.watchfly.repository.PhoneMessageConnection

class PYControlViewModel(private val phoneMessageConnection: PhoneMessageConnection) : ViewModel() {
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