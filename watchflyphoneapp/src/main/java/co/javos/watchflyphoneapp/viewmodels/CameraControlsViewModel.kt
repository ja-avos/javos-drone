package co.javos.watchflyphoneapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import co.javos.watchflyphoneapp.repository.DJIController

class CameraControlsViewModel(private val djiController: DJIController): ViewModel() {

    val isCameraReady = djiController.isCameraReady

    val isDroneConnected = djiController.isDroneConnected

    fun stopDrone() {
        Log.d("CameraControlsViewModel", "stopDrone: called")
        djiController.stopDrone()
    }

    fun takePhoto() {
        Log.d("CameraControlsViewModel", "takePhoto: called")
        djiController.takePhoto()
    }

    class CameraControlsViewModelFactory(private val djiController: DJIController) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CameraControlsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CameraControlsViewModel(djiController) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
