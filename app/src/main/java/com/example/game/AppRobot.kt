package com.example.game

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
    }
}
