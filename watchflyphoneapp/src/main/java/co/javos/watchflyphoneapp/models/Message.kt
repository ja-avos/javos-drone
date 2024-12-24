package co.javos.watchflyphoneapp.models

enum class AuthorDevice {
    PHONE, WATCH
}

data class Message(
    val content: String,
    val author: AuthorDevice,
    val timestamp: Long = System.currentTimeMillis()
)
