package co.javos.watchflyphoneapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.javos.watchflyphoneapp.models.VirtualSticks
import co.javos.watchflyphoneapp.repository.DJIController
import kotlinx.coroutines.flow.MutableStateFlow

class JoysticksViewModel(private val djiController: DJIController) : ViewModel() {

    val sticksState: MutableStateFlow<VirtualSticks> = djiController.virtualSticks

    class JoysticksViewModelFactory(private val djiController: DJIController) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(JoysticksViewModel::class.java)) {
                return JoysticksViewModel(djiController) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
