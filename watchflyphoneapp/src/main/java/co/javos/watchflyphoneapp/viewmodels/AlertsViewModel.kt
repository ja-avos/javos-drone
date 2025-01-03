package co.javos.watchflyphoneapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import co.javos.watchflyphoneapp.models.Alert
import co.javos.watchflyphoneapp.models.AlertType
import co.javos.watchflyphoneapp.models.Message
import co.javos.watchflyphoneapp.repository.DJIController
import dji.common.healthmanager.WarningLevel
import dji.sdk.base.DJIDiagnostics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AlertsViewModel(private val djiController: DJIController, private val watchChatViewModel: WatchChatViewModel): ViewModel() {

    private val TAG = "AlertsViewModel"

    val alerts: MutableStateFlow<List<Alert>> = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
            djiController.diagnostics.collect { value ->
                // Handle state updates
                Log.d(TAG, "Diagnostics update")
                alerts.value = value.map {
                    transformDiagnosticToAlert(it)
                }
                if (alerts.value.isNotEmpty()) {
                    watchChatViewModel.addAlert(alerts.value.first())
                }
            }
        }
    }

    private fun transformDiagnosticToAlert(diagnostic: DJIDiagnostics): Alert {
        return Alert(
            mapWarningToAlertType(diagnostic.healthInformation?.warningLevel),
            diagnostic.type.toString(),
            "${diagnostic.code} - ${diagnostic.subCode}",
            diagnostic.reason,
            diagnostic.solution
        )
    }

    private fun mapWarningToAlertType(warning: WarningLevel?): AlertType? {
        return when (warning) {
            WarningLevel.SERIOUS_WARNING -> AlertType.ERROR
            WarningLevel.WARNING -> AlertType.WARNING
            WarningLevel.CAUTION -> AlertType.WARNING
            WarningLevel.NOTICE -> AlertType.INFO
            else -> null
        }
    }

    class AlertsViewModelFactory(private val djiController: DJIController, private val watchChatViewModel: WatchChatViewModel) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlertsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AlertsViewModel(djiController, watchChatViewModel) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}