import com.example.game.Enemy
import com.example.game.Player
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread

class HostGameManager {
    private lateinit var serverSocket: ServerSocket
    private lateinit var clientSocket: Socket
    private lateinit var out: PrintWriter
    private lateinit var input: BufferedReader

    private val player2InputRef = AtomicReference<String?>(null)
    @Volatile
    private var running = true


    @Volatile
    var player2Nickname: String = "Игрок 2"

    fun start(onClientConnected: () -> Unit) {
        thread {
            try {
                serverSocket = ServerSocket(7777)
                clientSocket = serverSocket.accept()
                out = PrintWriter(clientSocket.getOutputStream(), true)
                input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                onClientConnected()

                thread { listen() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun listen() {
        try {
            while (running && !clientSocket.isClosed) {
                val msg = input.readLine() ?: break
                handleMessage(msg)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendGameState(player1: Player, player2: Player, enemies: List<Enemy>,
                      time: Double, isGameOver: Boolean, gameOverText: String) {
        thread {
            val state = FullGameState(
                player1 = PlayerState(player1.id, player1.position.x, player1.position.y),
                player2 = PlayerState(player2.id, player2.position.x, player2.position.y),
                enemies = enemies.mapIndexed { i, e -> PlayerState(i, e.position.x, e.position.y) },
                time = time.toFloat(),
                isGameOver = isGameOver,
                gameOverText = gameOverText
            )
        val msg = NetworkJson.encodeToString(NetMessage.serializer(), NetMessage.FullState(state))
        out.println(msg)
    }
}

    fun sendStartGame() {
        thread {
            val msg = NetworkJson.encodeToString(NetMessage.serializer(), NetMessage.State(GameState.Started))
            out.println(msg)
        }
    }

    fun consumePlayer2Input(): String? = player2InputRef.getAndSet(null)
    private fun handleMessage(msg: String) {
        runCatching {
            val message = NetworkJson.decodeFromString<NetMessage>(msg)
            when (message) {
                is NetMessage.Move -> {
                    player2InputRef.set(message.dir)
                }
                is NetMessage.Join -> {
                    player2Nickname = message.nickname.ifBlank { "Игрок 2" }
                }
                else -> {}
            }
        }
    }

    fun close() {
        try {
            running = false
            if (::clientSocket.isInitialized) clientSocket.close()
            if (::serverSocket.isInitialized) serverSocket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}