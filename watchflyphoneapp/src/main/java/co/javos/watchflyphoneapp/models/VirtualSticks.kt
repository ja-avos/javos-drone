package co.javos.watchflyphoneapp.models

enum class RemoteType {
    REMOTE_CONTROLLER,
    WATCH
}

data class VirtualSticks(
    var type: RemoteType = RemoteType.REMOTE_CONTROLLER,
    var right: Stick = Stick(0F,0F),
    var left: Stick = Stick(0F,0F)
)

data class Stick(
    val x: Float,
    val y: Float
)