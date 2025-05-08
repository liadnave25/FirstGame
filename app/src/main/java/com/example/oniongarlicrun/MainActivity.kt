package com.example.oniongarlicrun

import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.oniongarlicrun.utils.Smanager

class MainActivity : AppCompatActivity() {

    private lateinit var cellMatrix: Array<Array<ImageView>>
    private lateinit var bLeft: ImageButton
    private lateinit var bRight: ImageButton
    private lateinit var heart1: ImageView
    private lateinit var heart2: ImageView
    private lateinit var heart3: ImageView
    private lateinit var gameLogic: GameLogic

    private val numRows = 6
    private val numCols = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        Smanager.init(applicationContext)

        findViews()
        setupGrid()

        gameLogic = GameLogic(
            context = this,
            cellMatrix = cellMatrix,
            onEggplantDraw = { lane -> drawEggplantAtLane(lane) },
            onHeartUpdate = { updateHearts(it) }
        )

        gameLogic.drawEggplantInitial()
        startDropBombs()
        initControls()
    }

    private fun findViews() {
        bLeft = findViewById(R.id.bLeft)
        bRight = findViewById(R.id.bRight)
        heart1 = findViewById(R.id.heart1)
        heart2 = findViewById(R.id.heart2)
        heart3 = findViewById(R.id.heart3)
    }

    private fun initControls() {
        bLeft.setOnClickListener {
            gameLogic.moveLeft()
        }
        bRight.setOnClickListener {
            gameLogic.moveRight()
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

    private fun drawEggplantAtLane(lane: Int) {
        for (i in 0 until numCols) {
            cellMatrix[numRows - 1][i].setImageDrawable(null)
        }
        cellMatrix[numRows - 1][lane].setImageResource(R.drawable.eggplant)
    }

    private fun updateHearts(lives: Int) {
        when (lives) {
            2 -> heart3.setImageDrawable(null)
            1 -> heart2.setImageDrawable(null)
            0 -> heart1.setImageDrawable(null)
        }
    }

    private fun startDropBombs() {
        object : android.os.CountDownTimer(Long.MAX_VALUE, 2000) {
            override fun onTick(millisUntilFinished: Long) {
                    gameLogic.spawnBomb()
            }

            override fun onFinish() {
                startDropBombs()
            }
        }.start()
    }
}
