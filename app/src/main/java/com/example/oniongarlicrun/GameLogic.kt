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
    private val onHeartUpdate: (lives: Int) -> Unit,
    private var dropDelay: Long,
    private val onMeterUpdate: (meters: Int) -> Unit,
    private val onCoinUpdate: (coins: Int) -> Unit,
    private val onGameOver: () -> Unit,
    private val onSound: (soundResId: Int) -> Unit
) {
    private val numRows = cellMatrix.size
    private val numCols = cellMatrix[0].size
    private var lane = numCols / 2
    private var lives = 3
    private var bombsSpawned = 0
    private var metersPassed = 0
    private var coinsCollected = 0
    private var isGameOver = false
    private var meterTimer: CountDownTimer? = null

    fun isGameOver(): Boolean = isGameOver

    fun startMeterCounter() {
        meterTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (!isGameOver) {
                    metersPassed++
                    onMeterUpdate(metersPassed)
                }
            }
            override fun onFinish() {}
        }.start()
    }

    fun moveLeft() {
        if (lane > 0) {
            lane--
            checkCollisionOnMove()
            onEggplantDraw(lane)
        }
    }

    fun moveRight() {
        if (lane < numCols - 1) {
            lane++
            checkCollisionOnMove()
            onEggplantDraw(lane)
        }
    }

    private fun checkCollisionOnMove() {
        val tag = cellMatrix[numRows - 1][lane].tag
        if (tag == "bomb") {
            loseLife()
            Smanager.getInstance().toast("Eggplant ran into a bomb!")
            onSound(R.raw.boom)
            cellMatrix[numRows - 1][lane].setImageDrawable(null)
            cellMatrix[numRows - 1][lane].tag = null
        } else if (tag == "coin") {
            coinsCollected++
            onCoinUpdate(coinsCollected)
            Smanager.getInstance().toast("💰 Got a coin!")
            onSound(R.raw.coin)
            cellMatrix[numRows - 1][lane].setImageDrawable(null)
            cellMatrix[numRows - 1][lane].tag = null
        }
    }

    fun drawEggplantInitial() {
        onEggplantDraw(lane)
    }

    fun spawnBomb() {
        if (isGameOver) return

        val isCoin = (0..4).random() == 0
        if (isCoin) {
            spawnCoin()
            return
        }

        val col = (0 until numCols).random()
        val type = when ((0..2).random()) {
            0 -> R.drawable.garlic
            1 -> R.drawable.purpleonion
            else -> R.drawable.whiteonion
        }

        dropBomb(col, type)

        bombsSpawned++
        if (bombsSpawned % 30 == 0 && dropDelay > 300L) {
            dropDelay = (dropDelay * 0.85).toLong()
        }
    }

    private fun dropBomb(col: Int, drawableId: Int) {
        var currentRow = 0
        var hit = false

        object : CountDownTimer(dropDelay * numRows, dropDelay) {
            override fun onTick(millisUntilFinished: Long) {
                if (hit || isGameOver) return

                val row = currentRow

                if (row > 0 && row - 1 != numRows - 1) {
                    cellMatrix[row - 1][col].setImageDrawable(null)
                    cellMatrix[row - 1][col].tag = null
                }

                if (row == numRows) {
                    if (col != lane) {
                        cellMatrix[numRows - 1][col].setImageDrawable(null)
                        cellMatrix[numRows - 1][col].tag = null
                    }
                    return
                }

                if (row == numRows - 1 && col == lane) {
                    loseLife()
                    Smanager.getInstance().toast("Eggplant got hit!")
                    onSound(R.raw.boom)
                    onEggplantDraw(lane)
                    hit = true
                    return
                }

                cellMatrix[row][col].setImageResource(drawableId)
                cellMatrix[row][col].tag = "bomb"

                currentRow++
            }

            override fun onFinish() {
                if (col != lane) {
                    cellMatrix[numRows - 1][col].setImageDrawable(null)
                    cellMatrix[numRows - 1][col].tag = null
                }
            }
        }.start()
    }

    private fun spawnCoin() {
        val col = (0 until numCols).random()
        val drawableId = R.drawable.coin
        var currentRow = 0

        object : CountDownTimer(dropDelay * numRows, dropDelay) {
            override fun onTick(millisUntilFinished: Long) {
                if (isGameOver) return

                val row = currentRow

                if (row > 0 && row - 1 != numRows - 1) {
                    cellMatrix[row - 1][col].setImageDrawable(null)
                    cellMatrix[row - 1][col].tag = null
                }

                if (row == numRows) {
                    if (col != lane) {
                        cellMatrix[numRows - 1][col].setImageDrawable(null)
                        cellMatrix[numRows - 1][col].tag = null
                    }
                    return
                }

                if (row == numRows - 1 && col == lane) {
                    coinsCollected++
                    onCoinUpdate(coinsCollected)
                    Smanager.getInstance().toast("💰 Got a coin!")
                    onSound(R.raw.coin)
                    cellMatrix[row][col].setImageDrawable(null)
                    cellMatrix[row][col].tag = null
                    onEggplantDraw(lane)
                    return
                }

                cellMatrix[row][col].setImageResource(drawableId)
                cellMatrix[row][col].tag = "coin"

                currentRow++
            }

            override fun onFinish() {
                if (col != lane) {
                    cellMatrix[numRows - 1][col].setImageDrawable(null)
                    cellMatrix[numRows - 1][col].tag = null
                }
            }
        }.start()
    }

    private fun loseLife() {
        lives--
        onHeartUpdate(lives)

        if (lives == 0) {
            isGameOver = true
            meterTimer?.cancel()
            Smanager.getInstance().toast("☠️ Game Over!")
            Smanager.getInstance().vibrate()
            onGameOver()
        }
    }

    fun adjustDropDelay(faster: Boolean) {
        if (isGameOver) return
        val factor = if (faster) 0.9 else 1.1
        dropDelay = (dropDelay * factor).toLong()
            .coerceIn(300L, 1300L)
        Log.d("DROP_DELAY", "dropDelay = $dropDelay ms")
    }


}
