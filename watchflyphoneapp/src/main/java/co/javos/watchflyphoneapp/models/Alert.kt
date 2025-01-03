package co.javos.watchflyphoneapp.models

enum class AlertType {
    INFO,
    WARNING,
    ERROR
}

data class Alert(
    val type: AlertType?,
    val title: String,
    val code: String? = null,
    val message: String,
    val solution: String? = null
)
