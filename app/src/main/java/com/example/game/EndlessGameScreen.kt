package com.example.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun EndlessGameScreen(
    nickname: String,
    scoreDatabase: ScoreDatabase,
    onExit: () -> Unit) {
    val playerImg = GameAssets.playerImg
    val up = GameAssets.up
    val down = GameAssets.down
    val left = GameAssets.left
    val right = GameAssets.right
    val enemyImg = GameAssets.enemyImg
    var restartLevel by remember { mutableStateOf(false) }

    var isGameOver by remember { mutableStateOf(false) }

    val currentLevel = endlessLevel
    val enemies = remember { mutableStateListOf<Enemy>() }

    var screenWidth by remember { mutableStateOf(0f) }
    var screenHeight by remember { mutableStateOf(0f) }

    val textMeasurer = rememberTextMeasurer()

    var timeSurvived by remember { mutableStateOf(0.0) }

    val timeEnemy = 10.0
    var enemySpawnTimer by remember { mutableStateOf(0.0) }

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

    var player by remember {
        mutableStateOf(
            Player(
                position = Offset(500f, 100f),
                image = playerImg,
                speed = currentLevel.playerSpeed
            )
        )
    }

    LaunchedEffect(restartLevel) {
        enemies.clear()
        repeat(currentLevel.enemyCount) {

            val dx = listOf(-1f, 1f).random()
            val dy = listOf(-1f, 1f).random()

            enemies.add(
                Enemy(
                    position = Offset(
                        x = (450..750).random().toFloat(),
                        y = (1000..1500).random().toFloat()
                    ),
                    image = enemyImg,
                    speed = Offset(
                        dx * currentLevel.enemySpeed,
                        dy * currentLevel.enemySpeed
                    )
                )
            )
        }
        player.speed = currentLevel.playerSpeed
        player.turnDown()
        isGameOver = false
    }

    fun restartGame() {
        isGameOver = false
        restartLevel = !restartLevel
        timeSurvived = 0.0
        player = Player(
            position = Offset(500f, 100f),
            image = playerImg,
            speed = currentLevel.playerSpeed
        )
    }

    LaunchedEffect(Unit) {
        val interval = 16L
        while (true) {
            if (!isGameOver) {
                val delta = interval / 1000.0
                timeSurvived += delta
                enemySpawnTimer += delta
                player.update(screenWidth, screenHeight)
                enemies.forEach { it.update(screenWidth, screenHeight) }

                enemies.forEach { enemy ->
                    if (checkCollision(player, enemy)) {
                        isGameOver = true
                    }
                }
            }
            delay(interval)
        }
    }

    val scope = rememberCoroutineScope()

    LaunchedEffect(isGameOver) {
        if (isGameOver && nickname.isNotBlank()) {
            scope.launch(Dispatchers.IO) {
                val score = Score(nickname = nickname, timeSurvived = timeSurvived.toFloat())
                scoreDatabase.scoreDao().insert(score)
                scoreDatabase.scoreDao().deleteExtraScores()
            }
        }
    }

    var stage by remember { mutableStateOf(0) }

    val stageBackgrounds = listOf(
        R.drawable.background01,
        R.drawable.background02,
        R.drawable.background03,
    )
    val currentBackground = stageBackgrounds[stage % stageBackgrounds.size]

    LaunchedEffect(isGameOver) {
        while (!isGameOver) {
            delay(1000)

            if (enemySpawnTimer >= timeEnemy) {
                stage++
                enemies.add(
                    Enemy(
                        position = Offset(
                            x = (0..screenWidth.toInt()).random().toFloat() -150,
                            y = (0..screenHeight.toInt()).random().toFloat() - 150
                        ),
                        image = enemyImg,
                        speed = Offset(
                            listOf(-1f, 1f).random() * currentLevel.enemySpeed,
                            listOf(-1f, 1f).random() * currentLevel.enemySpeed
                        )
                    )
                )
                enemySpawnTimer = 0.0
            }
        }
    }



    Column(Modifier.fillMaxSize()) {

        val backgroundImg = ImageBitmap.imageResource(id = currentBackground)
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            screenWidth = size.width
            screenHeight = size.height

            drawImage(
                image = backgroundImg,
                dstSize = IntSize(size.width.toInt(), size.height.toInt())
            )

            drawImage(player.image, topLeft = player.position)

            enemies.forEach {
                drawImage(it.image, topLeft = it.position)
            }
            val formattedTime = String.format("%.3f", timeSurvived)
            val time = textMeasurer.measure(
                text = "Время: $formattedTime сек.",
                style = TextStyle(color = Color.Black, fontSize = 15.sp)
            )
            drawText(
                textMeasurer,
                text = "Время: $formattedTime сек.",
                Offset(
                    size.width / 4 - time.size.width / 2,
                    time.size.height.toFloat()
                ),
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
                    Offset(
                        size.width / 2 - gameOverLayout.size.width / 2f,
                        size.height / 2 - gameOverLayout.size.height / 2f - 30f
                    ),
                    TextStyle(Color.Red, 40.sp)
                )
                val finaltimeLayout = textMeasurer.measure(
                    text = "Время: $formattedTime сек.",
                    style = TextStyle(color = Color.Yellow, fontSize = 28.sp)
                )
                drawText(
                    textMeasurer,
                    "Время: $formattedTime сек.",
                    Offset(
                        size.width / 2 - finaltimeLayout.size.width / 2f,
                        size.height / 2 + 30f
                    ),
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
                Button(
                    onClick = { restartGame() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.restart))
                }

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("В меню")
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
                ImageButton(up) { player.turnUp() }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ImageButton(left) { player.turnLeft() }
                    ImageButton(down) { player.turnDown() }
                    ImageButton(right) { player.turnRight() }
                }
            }
        }

    }

}
