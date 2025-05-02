package com.example.oniongarlicrun

import android.content.Context
import android.os.CountDownTimer
import android.util.Log
import android.widget.ImageView
import com.example.oniongarlicrun.utils.Smanager

class GameLogic(
    private val context: Context,
    private val cellMatrix: Array<Array<ImageView>>,
    private val onEggplantDraw: (lane: Int) -> Unit,
    private val onHeartUpdate: (lives: Int) -> Unit
) {
    private val numRows = cellMatrix.size
    private val numCols = cellMatrix[0].size

    private var lane = 1
    private var lives = 3
    private var dropDelay = 500L
    private var bombsSpawned = 0

    // מטריצה שמסמנת היכן יש פצצות
    private val bombMatrix = Array(numRows) { BooleanArray(numCols) }

    fun moveLeft() {
        if (lane > 0) {
            lane--
            onEggplantDraw(lane)
            checkCollisionWithBomb()
        }
    }

    fun moveRight() {
        if (lane < 2) {
            lane++
            onEggplantDraw(lane)
            checkCollisionWithBomb()
        }
    }

    fun drawEggplantInitial() {
        onEggplantDraw(lane)
        checkCollisionWithBomb()
    }

    fun spawnBomb() {
        val col = (0..2).random()
        val type = when ((0..2).random()) {
            0 -> R.drawable.garlic
            1 -> R.drawable.purpleonion
            else -> R.drawable.whiteonion
        }

        dropBomb(col, type)

        bombsSpawned++
        if (bombsSpawned % 15 == 0) {
            dropDelay = (dropDelay / 1.2).toLong().coerceAtLeast(100L)
            Log.d("GameSpeed", "⬆️ Drop speed increased! New delay: $dropDelay ms")
        }
    }

    private fun dropBomb(col: Int, drawableId: Int) {
        var currentRow = 0

        object : CountDownTimer(dropDelay * numRows, dropDelay) {
            override fun onTick(millisUntilFinished: Long) {
                val row = currentRow

                if (row > 0 && row - 1 != numRows - 1) {
                    cellMatrix[row - 1][col].setImageDrawable(null)
                    bombMatrix[row - 1][col] = false
                }

                if (row == numRows) return

                if (row != numRows - 1 || col != lane) {
                    cellMatrix[row][col].setImageResource(drawableId)
                    bombMatrix[row][col] = true
                }

                if (row == numRows - 1 && col == lane) {
                    Log.d("GameDebug", "💥 Eggplant got hit!")
                    loseLife()
                    bombMatrix[row][col] = false
                    // לא מוחקים את הציור – נשאיר את החציל
                }

                currentRow++
            }

            override fun onFinish() {
                if (currentRow == numRows && col != lane) {
                    cellMatrix[numRows - 1][col].setImageDrawable(null)
                    bombMatrix[numRows - 1][col] = false
                }
            }
        }.start()
    }

    private fun checkCollisionWithBomb() {
        if (bombMatrix[numRows - 1][lane]) {
            Log.d("GameDebug", "💥 Eggplant moved into a bomb!")
            loseLife()
            bombMatrix[numRows - 1][lane] = false

        }
    }

    private fun loseLife() {
        lives--
        onHeartUpdate(lives)

        if (lives == 0) {
            Smanager.getInstance().toast("☠️ Game Over!")
            Smanager.getInstance().vibrate()
        }
    }
}