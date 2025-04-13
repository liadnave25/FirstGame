package com.example.oniongarlicrun
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import android.content.Context

class MainActivity : AppCompatActivity() {

    private lateinit var cellMatrix: Array<Array<ImageView>>
    private lateinit var bLeft: ImageButton
    private lateinit var bRight: ImageButton
    private lateinit var heart1: ImageView
    private lateinit var heart2: ImageView
    private lateinit var heart3: ImageView
    private var lives = 3
    private var lane = 1
    private val numRows = 6
    private val numCols = 3
    private var dropDelay = 500L
    private var bombsSpawned = 0
    private var isGameOver = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        findViews()
        setupGrid()
        initViews()
        startDropBombs()
    }
    private fun findViews(){
        bLeft = findViewById(R.id.bLeft)
        bRight = findViewById(R.id.bRight)
        heart1 = findViewById(R.id.heart1)
        heart2 = findViewById(R.id.heart2)
        heart3 = findViewById(R.id.heart3)
    }
    private fun initViews() {
        bLeft.setOnClickListener { if (lane > 0) { lane--
            drawEggplant() }
        }
        bRight.setOnClickListener { if (lane < 2) { lane++
            drawEggplant() }
        }
        drawEggplant()
    }

    private fun spawnBomb() {
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




    private fun setupGrid() {
        val gridLayout = findViewById<GridLayout>(R.id.grid)
        gridLayout.rowCount = numRows
        gridLayout.columnCount = numCols

        cellMatrix = Array(numRows) { row ->
            Array(numCols) { col ->
                val imageView = ImageView(this)
                imageView.layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 0
                    rowSpec = GridLayout.spec(row, 1f)
                    columnSpec = GridLayout.spec(col, 1f)
                }
                imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                gridLayout.addView(imageView)
                imageView
            }
        }
    }

    private fun drawEggplant() {
        for (i in 0 until numCols) {
            cellMatrix[numRows - 1][i].setImageDrawable(null)
        }

        cellMatrix[numRows - 1][lane].setImageResource(R.drawable.eggplant)

    }

    private fun startDropBombs() {
        object : CountDownTimer(Long.MAX_VALUE, 2000) {
            override fun onTick(millisUntilFinished: Long) {
                spawnBomb()
            }

            override fun onFinish() {
                startDropBombs()
            }
        }.start()
    }


    private fun dropBomb(col: Int, drawableId: Int) {
        var currentRow = 0
        val delay: Long = dropDelay

        object : CountDownTimer(delay * numRows, delay) {
            override fun onTick(millisUntilFinished: Long) {
                val row = currentRow

                if (row > 0 && row - 1 != numRows - 1) {
                    cellMatrix[row - 1][col].setImageDrawable(null)
                }


                if (row == numRows) return


                if (row != numRows - 1 || col != lane) {

                    cellMatrix[row][col].setImageResource(drawableId)
                }


                if (row == numRows - 1 && col == lane && !isGameOver) {
                    Log.d("GameDebug", "💥 Hazil got hit!")
                    loseLife()
                }


                currentRow++
            }

            override fun onFinish() {
                if (currentRow == numRows && col != lane) {
                    cellMatrix[numRows - 1][col].setImageDrawable(null)
                }
            }
        }.start()
    }


    private fun loseLife() {
        lives--
        when (lives) {
            2 -> heart3.setImageDrawable(null)
            1 -> heart2.setImageDrawable(null)
            0 -> {
                heart1.setImageDrawable(null)
                Toast.makeText(this, " :( Game Over!", Toast.LENGTH_LONG).show()
                isGameOver = true
            }
        }
    }


}
