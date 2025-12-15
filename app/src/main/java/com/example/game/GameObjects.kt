package com.example.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap

class Player(
    position: Offset,
    var image: ImageBitmap,
    var speed: Float = 3f
) {
    var position by mutableStateOf(position)

    private var velocity = Offset(speed, 0f)

    fun update(screenWidth: Float, screenHeight: Float) {
        position += velocity

        val w = image.width.toFloat()
        val h = image.height.toFloat()

        if (position.x < 0 || position.x + w > screenWidth) {
            velocity = velocity.copy(x = -velocity.x)
        }
        if (position.y < 0 || position.y + h > screenHeight) {
            velocity = velocity.copy(y = -velocity.y)
        }
    }

    fun turnLeft() { velocity = Offset(-speed, 0f) }
    fun turnRight() { velocity = Offset(speed, 0f) }
    fun turnUp() { velocity = Offset(0f, -speed) }
    fun turnDown() { velocity = Offset(0f, speed) }
}


class Enemy(
    position: Offset,
    var image: ImageBitmap,
    private var speed: Offset = Offset(2f, 2f)
) {
    var position by mutableStateOf(position)

    fun update(screenWidth: Float, screenHeight: Float) {
        position += speed

        val w = image.width.toFloat()
        val h = image.height.toFloat()

        if (position.x < 0 || position.x + w > screenWidth) {
            speed = speed.copy(x = -speed.x)
        }

        if (position.y < 0 || position.y + h > screenHeight) {
            speed = speed.copy(y = -speed.y)
        }
    }
}
