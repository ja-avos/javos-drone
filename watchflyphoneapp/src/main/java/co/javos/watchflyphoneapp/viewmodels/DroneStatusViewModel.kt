package co.javos.watchflyphoneapp.viewmodels

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.javos.watchflyphoneapp.models.DroneStatus
import co.javos.watchflyphoneapp.repository.DJIController
import kotlinx.coroutines.flow.MutableStateFlow

class DroneStatusViewModel(private val djiController: DJIController): ViewModel() {
    val droneStatus: MutableStateFlow<DroneStatus> = djiController.droneStatus

    class DroneStatusViewModelFactory(private val djiController: DJIController) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DroneStatusViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DroneStatusViewModel(djiController) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
