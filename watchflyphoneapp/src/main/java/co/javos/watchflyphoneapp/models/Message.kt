package co.javos.watchflyphoneapp.models

enum class AuthorDevice {
    PHONE, WATCH
}

data class Message(
    val content: String = "NO MESSAGE",
    val author: AuthorDevice = AuthorDevice.PHONE,
    val timestamp: Long = System.currentTimeMillis()
)
