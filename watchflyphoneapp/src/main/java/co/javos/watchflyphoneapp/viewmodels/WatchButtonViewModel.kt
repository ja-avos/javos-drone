package co.javos.watchflyphoneapp.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.javos.watchflyphoneapp.repository.WatchMessageConnection

class WatchButtonViewModel(private val watchMessageConnection: WatchMessageConnection) : ViewModel() {

    val enabled: Boolean
        get() = watchMessageConnection.isWatchConnected

    class WatchButtonViewModelFactory(private val watchMessageConnection: WatchMessageConnection) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WatchButtonViewModel::class.java)) {
                return WatchButtonViewModel(watchMessageConnection) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
