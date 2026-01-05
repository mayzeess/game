import kotlinx.serialization.Serializable
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.*


@Serializable
sealed class NetMessage {

    @Serializable
    @SerialName("Join")
    data class Join(val nickname: String) : NetMessage()

    @Serializable
    @SerialName("Move")
    data class Move(val dir: String) : NetMessage()

    @Serializable
    @SerialName("State")
    data class State(val state: GameState) : NetMessage()

    @Serializable
    @SerialName("FullState")
    data class FullState(val state: FullGameState) : NetMessage()
}

@Serializable
data class PlayerState(
    val id: Int,
    val x: Float,
    val y: Float
)

@Serializable
data class FullGameState(
    val player1: PlayerState,
    val player2: PlayerState,
    val enemies: List<PlayerState>,
    val time: Float,
    val isGameOver: Boolean = false,
    val gameOverText: String = ""
)
@Serializable
enum class GameState {
    Started
}



val NetworkJson = Json {
    ignoreUnknownKeys = true
    classDiscriminator = "type"
}
