package com.example.game

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource

object GameAssets {
    lateinit var playerImg: ImageBitmap
    lateinit var playerImg2: ImageBitmap
    lateinit var up: ImageBitmap
    lateinit var down: ImageBitmap
    lateinit var left: ImageBitmap
    lateinit var right: ImageBitmap
    lateinit var enemyImg: ImageBitmap

    fun load(resources: android.content.res.Resources) {
        playerImg = ImageBitmap.imageResource(resources, R.drawable.player1)
        playerImg2 = ImageBitmap.imageResource(resources, R.drawable.player2)
        up = ImageBitmap.imageResource(resources, R.drawable.up1)
        down = ImageBitmap.imageResource(resources, R.drawable.down1)
        left = ImageBitmap.imageResource(resources, R.drawable.left1)
        right = ImageBitmap.imageResource(resources, R.drawable.right1)
        enemyImg = ImageBitmap.imageResource(resources, R.drawable.enemy1)
    }
}
data class Level(
    val background: Int,
    val enemyCount: Int,
    val enemySpeed: Float,
    val playerSpeed: Float
)

val gameLevels = listOf(
    Level(
        background = R.drawable.background01,
        enemyCount = 3,
        enemySpeed = 5f,
        playerSpeed = 7f
    ),
    Level(
        background = R.drawable.background02,
        enemyCount = 5,
        enemySpeed = 7f,
        playerSpeed = 9f
    ),
    Level(
        background = R.drawable.background03,
        enemyCount = 6,
        enemySpeed = 11f,
        playerSpeed = 12f
    )
)

val endlessLevel = Level(
    background = R.drawable.background01,
    enemyCount = 1,
    enemySpeed = 5f,
    playerSpeed = 7f
)

val OnlineLevel = Level(
    background = R.drawable.background01,
    enemyCount = 1,
    enemySpeed = 2f,
    playerSpeed = 5f
)

object OnlineWorld {
    const val W = 1000f
    const val H = 1600f
}