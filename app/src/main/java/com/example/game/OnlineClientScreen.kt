package com.example.game

import ClientGameManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OnlineClientScreen(
    ip: String,
    nickname: String,
    clientManager: ClientGameManager,
    onGameStart: () -> Unit,
    onExit: () -> Unit
) {

    LaunchedEffect(Unit) {
        clientManager.onState = { state ->
            if (state == GameState.Started) {
                onGameStart()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Подключено к хосту: $ip")
        Text("Ожидание начала игры")
        Spacer(Modifier.height(24.dp))
        Button(onClick = onExit, modifier = Modifier.fillMaxWidth()) {
            Text("Выйти")
        }
    }
}
