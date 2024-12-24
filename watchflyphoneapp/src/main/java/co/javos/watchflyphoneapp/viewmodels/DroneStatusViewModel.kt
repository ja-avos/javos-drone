package co.javos.watchflyphoneapp.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import co.javos.watchflyphoneapp.models.DroneStatus
import co.javos.watchflyphoneapp.repository.DJIController

class DroneStatusViewModel: ViewModel() {
    private val repository = DJIController()
    private val _droneStatus = mutableStateOf(DroneStatus())
    val droneStatus: MutableState<DroneStatus> = _droneStatus
}
