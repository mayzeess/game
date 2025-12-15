package com.example.game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LeaderboardScreen(
    scoreDatabase: ScoreDatabase,
    onExit: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var topScores by remember { mutableStateOf(listOf<Score>()) }

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            val scores = scoreDatabase.scoreDao().getTopScores()
            withContext(Dispatchers.Main) {
                topScores = scores
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.10f))
        Text("Топ игроков",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            itemsIndexed(topScores) { index, score ->
                Text(
                    text = "${index + 1}. ${score.nickname} — ${String.format("%.3f", score.timeSurvived)} сек.",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }

        Button(
            onClick = onExit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("В меню")
        }
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    scoreDatabase.scoreDao().clearAll()
                    topScores = emptyList()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сбросить топ")
        }

    }
}
