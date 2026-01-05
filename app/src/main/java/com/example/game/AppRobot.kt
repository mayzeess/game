package com.example.game

import ClientGameManager
import ConnectingScreen
import HostGameManager
import OnlineHostScreen
import OnlineJoinScreen
import OnlineMenuScreen
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AppRoot() {
    var screen by remember { mutableStateOf("menu") }
    var playerNickname by remember { mutableStateOf("") }
    val context = LocalContext.current.applicationContext
    val scoreDatabase = remember { ScoreDatabase.getDatabase(context) }
    var hostIp by remember { mutableStateOf("") }
    var onlineGameStarted by remember { mutableStateOf(false) }
    var hostManager by remember { mutableStateOf<HostGameManager?>(null) }
    var clientManager by remember { mutableStateOf<ClientGameManager?>(null) }
    var isHostPlayer by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val dao = scoreDatabase.scoreDao()
        withContext(Dispatchers.IO) {
            val list = dao.getTopScores()
            Log.d("DB_TEST", "Содержимое таблицы: $list")
        }
    }

    when (screen) {

        "menu" -> MenuScreen(
            onStartLevels = { screen = "levels" },
            onStartEndless = { screen = "endless_nick" },
            onOnline = { screen = "online_menu" },
            TopPlayer = { screen = "top" }
        )

        "levels" -> GameScreen(
            onExit = { screen = "menu" }
        )

        "endless_nick" -> EnterNicknameScreen(
            nickname = "",
            onStartGame = { nickname ->
                playerNickname = nickname
                screen = "endless"
            },
            onPlayWithoutSaving = {
                playerNickname = ""
                screen = "endless"
            },
            onExit = { screen = "menu" }
        )

        "endless" -> EndlessGameScreen(
            nickname = playerNickname,
            scoreDatabase = scoreDatabase,
            onExit = { screen = "menu" }
        )

        "top" -> LeaderboardScreen(
            scoreDatabase = scoreDatabase,
            onExit = { screen = "menu" }
        )

        "online_menu" -> OnlineMenuScreen(
            nickname = playerNickname,
            onNicknameChange = { playerNickname = it },
            onHost = { screen = "online_host" },
            onJoin = { screen = "online_join" },
            onExit = { screen = "menu" }
        )

        "online_host" -> OnlineHostScreen(
            nickname = playerNickname,
            onExit = {
                hostManager = null
                onlineGameStarted = false
                screen = "menu"
            },
            onStartGame = {
                screen = "online_game"
            },
            onHostReady = { manager ->
                hostManager = manager
                isHostPlayer = true
            },
            setOnlineGameStarted = { started ->
                onlineGameStarted = started
            }
        )


        "online_game" -> OnlineGameScreen(
            nickname = playerNickname,
            isHost = isHostPlayer,
            hostManager = hostManager,
            clientManager = clientManager,
            onExit = {
                hostManager = null
                clientManager = null
                onlineGameStarted = false
                screen = "menu"
            },
            gameStarted = onlineGameStarted
        )


        "online_join" -> OnlineJoinScreen(
            onJoin = { ip ->
                hostIp = ip
                screen = "connecting"
            },
            onExit = { screen = "online_menu" }
        )

        "connecting" -> ConnectingScreen(
            ip = hostIp,
            nickname = playerNickname,
            onConnected = { manager ->
                clientManager = manager
                isHostPlayer = false
                screen = "online_client" },
            onCancel = { screen = "online_join" }
        )

        "online_client" -> OnlineClientScreen(
            ip = hostIp,
            nickname = playerNickname,
            clientManager = clientManager!!,
            onGameStart = {
                screen = "online_game"
            },
            onExit = { screen = "menu" }
        )

    }
}
