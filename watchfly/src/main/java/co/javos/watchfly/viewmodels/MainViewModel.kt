package co.javos.watchfly.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.javos.watchfly.models.ControlMode
import co.javos.watchfly.models.DroneState
import co.javos.watchfly.repository.PhoneMessageConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val phoneMessageConnection: PhoneMessageConnection) : ViewModel() {

    private val _controlMode = MutableStateFlow(ControlMode.RPY)
    val controlMode: MutableStateFlow<ControlMode> = _controlMode

    val droneStatus = phoneMessageConnection.droneStatus

    fun toggleMotors() {
        CoroutineScope(Dispatchers.IO).launch {
            if (droneStatus.value.state == DroneState.MOTORS_OFF) {
                phoneMessageConnection.sendCommand("motors_on")
            } else {
                phoneMessageConnection.sendCommand("motors_off")
            }
        }
    }

    fun togglePYRPYControlMode() {
        _controlMode.value =
            if (_controlMode.value == ControlMode.RPY) ControlMode.PY else ControlMode.RPY

    }

    fun takeOff() {
        CoroutineScope(Dispatchers.IO).launch {
            phoneMessageConnection.sendCommand("take_off")
        }
    }

    fun land() {
        CoroutineScope(Dispatchers.IO).launch {
            phoneMessageConnection.sendCommand("land")
        }
    }

    fun confirmLanding(confirm: Boolean) {
        Log.d("MainViewModel", "confirmLanding: $confirm")
        CoroutineScope(Dispatchers.IO).launch {
            if (confirm)
                phoneMessageConnection.sendCommand("confirm_landing")
            else
                phoneMessageConnection.sendCommand("cancel_landing")
        }
    }

    fun stopDrone() {
        CoroutineScope(Dispatchers.IO).launch {
            phoneMessageConnection.sendCommand("stop")
        }
    }

    fun droneRTH() {
        CoroutineScope(Dispatchers.IO).launch {
            phoneMessageConnection.sendCommand("rth")
        }
    }

    fun changeControlMode(mode: ControlMode) {
        _controlMode.value = mode
    }

    class MainViewModelFactory(private val phoneMessageConnection: PhoneMessageConnection) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(phoneMessageConnection) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}