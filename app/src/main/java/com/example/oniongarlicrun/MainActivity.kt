package com.example.oniongarlicrun

import android.os.Bundle
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.oniongarlicrun.utils.Smanager
import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.widget.EditText
import com.example.oniongarlicrun.utils.ScoreManager
import com.example.oniongarlicrun.utils.TiltDetector
import com.example.oniongarlicrun.utils.TiltCallback
import com.example.oniongarlicrun.utils.Sounds


class MainActivity : AppCompatActivity() {

    private lateinit var cellMatrix: Array<Array<ImageView>>
    private lateinit var bLeft: ImageButton
    private lateinit var bRight: ImageButton
    private lateinit var heart1: ImageView
    private lateinit var heart2: ImageView
    private lateinit var heart3: ImageView
    private lateinit var metersTextView: TextView
    private lateinit var coinTextView: TextView
    private lateinit var gameLogic: GameLogic
    private var currentSpawnInterval: Long = 0L
    private var bombTimer: android.os.CountDownTimer? = null
    private var lastSpeedUpMeterMark: Int = 0
    private lateinit var sounds: Sounds

    private lateinit var tiltDetector: TiltDetector
    private var sensorModeEnabled = false

    private val numRows = 8
    private val numCols = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Smanager.init(applicationContext)
        findViews()
        setupGrid()
        sounds = Sounds(this)

        val mode = intent.getStringExtra("MODE") ?: "slow"
        sensorModeEnabled = (mode == "sensor")

        val baseDropDelay = when (mode) {
            "fast" -> 500L
            "sensor" -> 700L
            else -> 1000L
        }

        val spawnInterval = when (mode) {
            "fast" -> 750L
            "sensor" -> 1200L
            else -> 1300L
        }

        gameLogic = GameLogic(
            context = this,
            cellMatrix = cellMatrix,
            onEggplantDraw = { drawEggplantAtLane(it) },
            onHeartUpdate = { updateHearts(it) },
            dropDelay = baseDropDelay,
            onMeterUpdate = { meters ->
                metersTextView.text = "Meters Passed: $meters"
                if (meters - lastSpeedUpMeterMark >= 45) {
                    lastSpeedUpMeterMark = meters
                    currentSpawnInterval = (currentSpawnInterval / 1.2).toLong().coerceAtLeast(200L)
                    restartBombTimer()
                }
            },
            onCoinUpdate = { coins -> coinTextView.text = "Coins: $coins" },
            onGameOver = {
                val meters = metersTextView.text.toString().substringAfter(": ").toInt()
                val coins = coinTextView.text.toString().substringAfter(": ").toInt()
                val totalScore = meters + coins
                runOnUiThread { showNameDialog(totalScore) }
            },
            onSound = { soundResId -> sounds.playSound(soundResId)}
        )

        if (sensorModeEnabled) {
            initTiltControl()
        }

        gameLogic.drawEggplantInitial()
        gameLogic.startMeterCounter()
        startDropBombs(spawnInterval)
        initControls()
    }

    private fun findViews() {
        bLeft = findViewById(R.id.bLeft)
        bRight = findViewById(R.id.bRight)
        heart1 = findViewById(R.id.heart1)
        heart2 = findViewById(R.id.heart2)
        heart3 = findViewById(R.id.heart3)
        metersTextView = findViewById(R.id.metersTextView)
        coinTextView = findViewById(R.id.coinsTextView)
    }

    private fun initControls() {
        if (!sensorModeEnabled) {
            bLeft.setOnClickListener { gameLogic.moveLeft() }
            bRight.setOnClickListener { gameLogic.moveRight() }
        } else {
            bLeft.visibility = View.GONE
            bRight.visibility = View.GONE
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
            val imageView = cellMatrix[numRows - 1][i]
            imageView.setImageDrawable(null)
            imageView.scaleX = 1.0f
            imageView.scaleY = 1.0f
        }
        val imageView = cellMatrix[numRows - 1][lane]
        imageView.setImageResource(R.drawable.eggplant)
        imageView.scaleX = 2.0f
        imageView.scaleY = 2.0f

    }


    private fun updateHearts(lives: Int) {
        when (lives) {
            2 -> heart3.setImageDrawable(null)
            1 -> heart2.setImageDrawable(null)
            0 -> heart1.setImageDrawable(null)
        }
    }

    private fun startDropBombs(initialInterval: Long) {
        currentSpawnInterval = initialInterval
        restartBombTimer()
    }

    private fun restartBombTimer() {
        bombTimer?.cancel()

        bombTimer = object : android.os.CountDownTimer(Long.MAX_VALUE, currentSpawnInterval) {
            var isFirst = true
            override fun onTick(millisUntilFinished: Long) {
                if (isFirst) {
                    isFirst = false
                    return
                }
                gameLogic.spawnBomb()
            }

            override fun onFinish() {
                restartBombTimer()
            }
        }.start()
    }

    private fun initTiltControl() {
        tiltDetector = TiltDetector(
            context = this,
            tiltCallback = object : TiltCallback {
                override fun tiltX() {
                    if (tiltDetector.tiltCounterX > 0) {
                        gameLogic.moveRight()
                    } else {
                        gameLogic.moveLeft()
                    }
                }

                override fun tiltY() {
                    if (tiltDetector.tiltCounterY < 0) {
                        Log.d("TILT_Y", "faster")
                        gameLogic.adjustDropDelay(faster = true)
                    } else if (tiltDetector.tiltCounterY > 0) {
                        Log.d("TILT_Y", "slower")
                        gameLogic.adjustDropDelay(faster = false)
                    }
                }
            }
        )
    }

    override fun onResume() {
        super.onResume()
        if (sensorModeEnabled) tiltDetector.start()
    }

    override fun onPause() {
        super.onPause()
        if (sensorModeEnabled) tiltDetector.stop()
    }

    fun showNameDialog(finalScore: Int) {
        val editText = EditText(this)
        editText.hint = "Your name"

        AlertDialog.Builder(this)
            .setTitle("☠️ Game Over!")
            .setMessage("Enter your name for the records")
            .setView(editText)
            .setCancelable(false)
            .setPositiveButton("Submit") { _, _ ->
                val name = editText.text.toString().ifBlank { "Anonymous" }
                val currentLat = FirstScreen.lat
                val currentLon = FirstScreen.lon

                val newHighScore = HighScore(name, finalScore, currentLat, currentLon)
                ScoreManager.tryInsert(this, newHighScore)

                startActivity(Intent(this, RecordsActivityV2::class.java))
                finish()
            }
            .show()
    }
}
