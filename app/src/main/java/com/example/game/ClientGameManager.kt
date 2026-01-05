import java.io.*
import java.net.Socket
import kotlin.concurrent.thread

class ClientGameManager {

    private lateinit var socket: Socket
    private lateinit var out: PrintWriter
    private lateinit var input: BufferedReader
    var onState: ((GameState) -> Unit)? = null
    var onFullState: ((FullGameState) -> Unit)? = null
    fun connect(ip: String, nickname: String, onConnected: (() -> Unit)? = null) {
        thread {
            try {
                socket = Socket(ip, 7777)
                out = PrintWriter(socket.getOutputStream(), true)
                input = BufferedReader(InputStreamReader(socket.getInputStream()))

                val joinMsg: NetMessage = NetMessage.Join(nickname)
                send(NetworkJson.encodeToString(NetMessage.serializer(), joinMsg))
                onConnected?.invoke()
                while (!socket.isClosed) {
                    val msg = input.readLine() ?: break
                    try {
                        val message = NetworkJson.decodeFromString(NetMessage.serializer(), msg)
                        when (message) {
                            is NetMessage.State -> onState?.invoke(message.state)
                            is NetMessage.FullState -> onFullState?.invoke(message.state)
                            else -> {}
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendMove(dir: String) {
        thread {
            val moveMsg: NetMessage = NetMessage.Move(dir)
            send(NetworkJson.encodeToString(NetMessage.serializer(), moveMsg))
        }
    }

    private fun send(msg: String) {
        if (!socket.isClosed) {
            out.println(msg)
        }
    }

    fun close() {
        try {
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
