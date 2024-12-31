package co.javos.watchflyphoneapp.models

enum class RemoteType {
    REMOTE_CONTROLLER,
    WATCH
}

data class VirtualSticks(
    var type: RemoteType = RemoteType.REMOTE_CONTROLLER,
    var right: Stick = Stick(0,0),
    var left: Stick = Stick(0,0)
)

data class Stick(
    val x: Int,
    val y: Int
)