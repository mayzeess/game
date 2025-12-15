package com.example.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EnterNicknameScreen(
    nickname: String,
    onStartGame: (String) -> Unit,
    onPlayWithoutSaving: () -> Unit,
    onExit: () -> Unit
) {
    var inputNickname by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = inputNickname,
            onValueChange = { inputNickname = it },
            label = { Text("Введите ник") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (inputNickname.isNotBlank()) onStartGame(inputNickname)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Играть")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onPlayWithoutSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Играть без сохранения")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onExit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("В меню")
        }
    }
}

