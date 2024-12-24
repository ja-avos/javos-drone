package co.javos.watchflyphoneapp.models

enum class AlertType {
    WARNING,
    ERROR
}
data class Alert(
    val type: AlertType,
    val title: String,
    val message: String
)
