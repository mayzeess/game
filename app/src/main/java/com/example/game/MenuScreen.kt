package com.example.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuScreen(
    onStartLevels: () -> Unit,
    onStartEndless: () -> Unit,
    TopPlayer: () -> Unit,
    onOnline: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onStartLevels,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Уровни")
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onStartEndless,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("На время")
        }
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onOnline,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Онлайн")
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = TopPlayer,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Топ игроков")
        }
    }
}
