package co.javos.watchflyphoneapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.javos.watchflyphoneapp.models.RemoteType
import co.javos.watchflyphoneapp.models.Stick
import co.javos.watchflyphoneapp.models.VirtualSticks
import co.javos.watchflyphoneapp.repository.DJIController
import dji.common.flightcontroller.virtualstick.FlightControlData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class JoysticksViewModel(private val djiController: DJIController, private val watchChatViewModel: WatchChatViewModel) : ViewModel() {

    val sticksState = MutableStateFlow(VirtualSticks())

    init {
        viewModelScope.launch {
            djiController.virtualSticks.collect { value ->
                // Handle state updates
                sticksState.value = value
            }
        }
        viewModelScope.launch {
            watchChatViewModel.virtualFlightControlData.collect { value ->
                Log.d("JoysticksViewModel", "Virtual Flight Control Data Changed: $value")
                // Handle state updates
                sticksState.value = VirtualSticks(
                    type = RemoteType.WATCH,
                    right = Stick(
                        x = value.roll * 100,
                        y = value.pitch * 100
                    ),
                    left = Stick(
                        x = value.yaw * 100,
                        y = value.verticalThrottle * 100
                    )
                )
            }
        }
    }

    class JoysticksViewModelFactory(private val djiController: DJIController, private val watchChatViewModel: WatchChatViewModel) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(JoysticksViewModel::class.java)) {
                return JoysticksViewModel(djiController, watchChatViewModel) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
