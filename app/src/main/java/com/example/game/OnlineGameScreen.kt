package com.example.game

import ClientGameManager
import HostGameManager
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun OnlineGameScreen(
    nickname: String,
    isHost: Boolean,
    hostManager: HostGameManager? = null,
    clientManager: ClientGameManager? = null,
    onExit: () -> Unit,
    gameStarted: Boolean
) {
    val playerImg = GameAssets.playerImg
    val playerImg2 = GameAssets.playerImg2
    val up = GameAssets.up
    val down = GameAssets.down
    val left = GameAssets.left
    val right = GameAssets.right
    val enemyImg = GameAssets.enemyImg
    val currentLevel = OnlineLevel
    var gameOverText by remember { mutableStateOf("") }
    var isGameOver by remember { mutableStateOf(false) }
    var timeSurvived by remember { mutableStateOf(0.0) }
    val textMeasurer = rememberTextMeasurer()
    val enemies = remember { mutableStateListOf<Enemy>() }
    var player1 by remember {
        mutableStateOf(
            Player(
                id = 1,
                position = Offset(600f, 100f),
                image = playerImg,
                speed = currentLevel.playerSpeed
            )
        )
    }

    var player2 by remember {
        mutableStateOf(
            Player(
                id = 2,
                position = Offset(100f, 300f),
                image = playerImg2,
                speed = currentLevel.playerSpeed
            )
        )

    }

    fun spawnEnemies(count: Int) {
        enemies.clear()
        repeat(count) { i ->
            enemies.add(
                Enemy(
                    position = Offset(
                        x = 200f + i * 150f,
                        y = 600f
                    ),
                    image = enemyImg,
                    speed = Offset(OnlineLevel.enemySpeed, OnlineLevel.enemySpeed)
                )
            )
        }
    }

    fun checkCollision(player: Player, enemy: Enemy): Boolean {
        val pw = player.image.width
        val ph = player.image.height
        val ew = enemy.image.width
        val eh = enemy.image.height

        return player.position.x < enemy.position.x + ew &&
                player.position.x + pw > enemy.position.x &&
                player.position.y < enemy.position.y + eh &&
                player.position.y + ph > enemy.position.y
    }

    fun playersCollide(p1: Player, p2: Player): Boolean {
        val w1 = p1.image.width.toFloat()
        val h1 = p1.image.height.toFloat()
        val w2 = p2.image.width.toFloat()
        val h2 = p2.image.height.toFloat()

        return p1.position.x < p2.position.x + w2 &&
                p1.position.x + w1 > p2.position.x &&
                p1.position.y < p2.position.y + h2 &&
                p1.position.y + h1 > p2.position.y
    }

    LaunchedEffect(isHost) {
        if (isHost) {
            spawnEnemies(OnlineLevel.enemyCount)
            val interval = 16L
            while (true) {
                if (!isGameOver) {
                    hostManager?.consumePlayer2Input()?.let { dir ->
                        when (dir) {
                            "UP" -> player2.turnUp()
                            "DOWN" -> player2.turnDown()
                            "LEFT" -> player2.turnLeft()
                            "RIGHT" -> player2.turnRight()
                        }
                    }
                    val delta = interval / 1000.0

                    timeSurvived += delta
                    player1.update(OnlineWorld.W, OnlineWorld.H)
                    player2.update(OnlineWorld.W, OnlineWorld.H)
                    enemies.forEach { it.update(OnlineWorld.W, OnlineWorld.H) }

                    val p1Hit = enemies.any { checkCollision(player1, it) }
                    val p2Hit = enemies.any { checkCollision(player2, it) }

                    if (p1Hit || p2Hit) {
                        isGameOver = true

                        val p1Name = nickname.ifBlank { "Игрок 1" }
                        val p2Name =
                            hostManager?.player2Nickname?.ifBlank { "Игрок 2" } ?: "Игрок 2"

                        val who = if (p1Hit) p1Name else p2Name
                        gameOverText = "$who столкнулся с врагом"
                    }
                    if (playersCollide(player1, player2)) {
                        isGameOver = true
                        val p1Name = nickname.ifBlank { "Игрок 1" }
                        val p2Name = hostManager?.player2Nickname?.ifBlank { "Игрок 2" } ?: "Игрок 2"
                        gameOverText = "$p1Name и $p2Name столкнулись"
                    }
                }
                hostManager?.sendGameState(
                    player1,
                    player2,
                    enemies,
                    timeSurvived,
                    isGameOver,
                    gameOverText
                )

                delay(interval)
            }
        }
    }


    LaunchedEffect(clientManager) {
        if (!isHost) {
            Log.d("DEBUG_CLIENT", "Клиентский блок выполняется!")
            clientManager?.onFullState = { state ->
                player1.position = Offset(state.player1.x, state.player1.y)
                player2.position = Offset(state.player2.x, state.player2.y)
                enemies.clear()
                enemies.addAll(state.enemies.map { Enemy(Offset(it.x, it.y), enemyImg) })
                timeSurvived = state.time.toDouble()
                isGameOver = state.isGameOver
                gameOverText = state.gameOverText
            }
        }
    }

    fun restartGame() {
        isGameOver = false
        gameOverText = ""
        timeSurvived = 0.0

        player1 = Player(
            id = 1,
            position = Offset(600f, 100f),
            image = playerImg,
            speed = currentLevel.playerSpeed
        )
        player2 = Player(
            id = 2,
            position = Offset(100f, 300f),
            image = playerImg2,
            speed = currentLevel.playerSpeed
        )

        if (isHost) {
            spawnEnemies(OnlineLevel.enemyCount)
        } else {
            enemies.clear()
        }
    }


    Column(Modifier.fillMaxSize()) {
        val backgroundImg = ImageBitmap.imageResource(id = currentLevel.background)
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            val worldW = OnlineWorld.W
            val worldH = OnlineWorld.H

            val scale = minOf(size.width / worldW, size.height / worldH)
            val offsetX = (size.width - worldW * scale) / 2f
            val offsetY = (size.height - worldH * scale) / 2f

            fun toScreen(p: Offset): Offset =
                Offset(offsetX + p.x * scale, offsetY + p.y * scale)

            drawImage(
                image = backgroundImg,
                dstOffset = IntOffset(offsetX.toInt(), offsetY.toInt()),
                dstSize = IntSize(
                    (worldW * scale).toInt(),
                    (worldH * scale).toInt()
                )
            )

            drawImage(player1.image, topLeft = toScreen(player1.position))
            drawImage(player2.image, topLeft = toScreen(player2.position))

            enemies.forEach { drawImage(it.image, topLeft = toScreen(it.position)) }
            val formattedTime = String.format("%.3f", timeSurvived)
            val timeLayout = textMeasurer.measure(
                text = "Время: $formattedTime сек.",
                style = TextStyle(color = Color.Black, fontSize = 15.sp)
            )
            drawText(
                textMeasurer,
                text = "Время: $formattedTime сек.",
                Offset(size.width / 4 - timeLayout.size.width / 2, timeLayout.size.height.toFloat()),
                TextStyle(Color.Black, 15.sp)
            )

            if (isGameOver) {
                drawRect(Color(0x88000000))
                val gameOverLayout = textMeasurer.measure(
                    text = "GAME OVER",
                    style = TextStyle(color = Color.Red, fontSize = 40.sp)
                )
                drawText(
                    textMeasurer,
                    "GAME OVER",
                    Offset(size.width / 2 - gameOverLayout.size.width / 2f, size.height / 2 - gameOverLayout.size.height / 2f - 30f),
                    TextStyle(Color.Red, 40.sp)
                )
                val reasonLayout = textMeasurer.measure(
                    text = gameOverText,
                    style = TextStyle(color = Color.White, fontSize = 20.sp)
                )

                drawText(
                    textMeasurer,
                    gameOverText,
                    Offset(
                        size.width / 2 - reasonLayout.size.width / 2f,
                        size.height / 2 + 130f
                    ),
                    TextStyle(Color.White, 22.sp)
                )

                val finalTimeLayout = textMeasurer.measure(
                    text = "Время: $formattedTime сек.",
                    style = TextStyle(color = Color.Yellow, fontSize = 28.sp)
                )
                drawText(
                    textMeasurer,
                    "Время: $formattedTime сек.",
                    Offset(size.width / 2 - finalTimeLayout.size.width / 2f, size.height / 2 + 30f),
                    TextStyle(Color.Yellow, 28.sp)
                )
            }
        }

        if (isGameOver) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isHost) {
                    Button(
                        onClick = { restartGame() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.restart))
                    }
                    Spacer(Modifier.height(12.dp))
                }

                Spacer(Modifier.height(12.dp))
                Button(onClick = {
                    hostManager?.close()
                    clientManager?.close()
                    onExit()
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Отмена")
                }
            }
        }
        if (!isGameOver) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ImageButton(up) {
                    if (isHost) {player1.turnUp() } else if (!isHost){ clientManager?.sendMove("UP")}
                }
                Row(
                    Modifier.fillMaxWidth(), Arrangement.SpaceEvenly
                ) {
                    ImageButton(left) { if (isHost) {player1.turnLeft() } else if (!isHost){clientManager?.sendMove("LEFT")} }
                    ImageButton(down) { if (isHost){player1.turnDown() } else if (!isHost){clientManager?.sendMove("DOWN")}}
                    ImageButton(right) { if (isHost){player1.turnRight()} else if (!isHost){clientManager?.sendMove("RIGHT")}}
                }
            }
        }
    }
}