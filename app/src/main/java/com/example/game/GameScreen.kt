package com.example.game

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun GameScreen(onExit: () -> Unit) {

    val playerImg = GameAssets.playerImg
    val up = GameAssets.up
    val down = GameAssets.down
    val left = GameAssets.left
    val right = GameAssets.right
    val enemyImg = GameAssets.enemyImg
    var restartLevel by remember { mutableStateOf(false) }
    var isGameOver by remember { mutableStateOf(false) }
    var levelCompleted by remember { mutableStateOf(false) }

    var currentLevelIndex by remember { mutableStateOf(0) }
    val currentLevel = gameLevels[currentLevelIndex]
    var timeNextLevel by remember { mutableStateOf(100.0) }
    var player by remember {
        mutableStateOf(
            Player(
                position = Offset(500f, 100f),
                image = playerImg,
                speed = currentLevel.playerSpeed
            )
        )
    }

    val enemies = remember { mutableStateListOf<Enemy>() }

    var screenWidth by remember { mutableStateOf(0f) }
    var screenHeight by remember { mutableStateOf(0f) }

    val textMeasurer = rememberTextMeasurer()


    LaunchedEffect(currentLevelIndex, restartLevel) {

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
        currentLevelIndex = 0
        levelCompleted = false
        isGameOver = false
        restartLevel = !restartLevel

        player = Player(
            position = Offset(500f, 100f),
            image = playerImg,
            speed = gameLevels[0].playerSpeed
        )
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

    LaunchedEffect(currentLevelIndex, restartLevel) {
        delay(100000)
        if (!isGameOver) levelCompleted = true
    }

    LaunchedEffect(Unit) {
        val interval = 16L
        while (true) {
            if (!isGameOver && !levelCompleted) {
                player.update(screenWidth, screenHeight)
                enemies.forEach { it.update(screenWidth, screenHeight) }
                val delta = interval / 1000.0
                timeNextLevel -= delta
                enemies.forEach { enemy ->
                    if (checkCollision(player, enemy)) {
                        isGameOver = true
                    }
                }
            }
            delay(interval)
        }
    }

    fun nextLevel() {
        if (currentLevelIndex < gameLevels.size - 1) {
            currentLevelIndex++
            player.position = Offset(500f, 100f)
            isGameOver = false
            levelCompleted = false
        } else {
            isGameOver = true
        }
    }

    Column(Modifier.fillMaxSize()) {

        val backgroundImg = ImageBitmap.imageResource(id = currentLevel.background)

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
            val formattedTime = String.format("%.3f", timeNextLevel)
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
                val layout = textMeasurer.measure(
                    text = "GAME OVER",
                    style = TextStyle(color = Color.Red, fontSize = 40.sp)
                )
                drawText(
                    textMeasurer,
                    "GAME OVER",
                    Offset(
                        size.width / 2 - layout.size.width / 2f,
                        size.height / 2 - layout.size.height / 2f
                    ),
                    TextStyle(Color.Red, 40.sp)
                )

            }
        }

        if (isGameOver && !levelCompleted) {
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


        if (!isGameOver && !levelCompleted) {
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

        if (currentLevelIndex == gameLevels.size - 1 && levelCompleted) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("YOU WIN!", fontSize = 28.sp, color = Color.Yellow)
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = { restartGame() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("RESTART GAME")
                }

                Button(
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("В меню")
                }
            }
            return
        }
        if (levelCompleted && !isGameOver) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    "LEVEL COMPLETED",
                    fontSize = 24.sp,
                    color = Color.Green
                )
                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = onExit,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("В меню")
                }
                Button(
                    onClick = { nextLevel() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.next))
                }
            }
        }
    }
}


@Composable
fun ImageButton(
    image: ImageBitmap,
    size: Int = 80,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .padding(6.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            bitmap = image,
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}
