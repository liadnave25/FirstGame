package com.example.oniongarlicrun
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.oniongarlicrun.MapActivity
import com.example.oniongarlicrun.utils.ScoreManager
import android.widget.Button


class RecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.record_activity)

        findViewById<Button>(R.id.btnBackToMenu).setOnClickListener {
            val intent = Intent(this, FirstScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        val scores = ScoreManager.load(this)
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)

        // כותרת
        val header = TableRow(this)
        header.addView(TextView(this).apply {
            text = "Player"
            setPadding(16, 16, 16, 16)
        })
        header.addView(TextView(this).apply {
            text = "Score"
            setPadding(16, 16, 16, 16)
        })
        header.addView(TextView(this).apply {
            text = "Location"
            setPadding(16, 16, 16, 16)
        })
        tableLayout.addView(header)

        // שורות עם אייקון
        for (score in scores) {
            val row = TableRow(this)
            row.addView(TextView(this).apply {
                text = score.name
                setPadding(16, 16, 16, 16)
            })
            row.addView(TextView(this).apply {
                text = score.score.toString()
                setPadding(16, 16, 16, 16)
            })
            val icon = ImageView(this).apply {
                setImageResource(R.drawable.place)
                val sizeInDp = (32 * resources.displayMetrics.density).toInt()
                layoutParams = TableRow.LayoutParams(sizeInDp, sizeInDp)
                scaleType = ImageView.ScaleType.FIT_CENTER
                contentDescription = "Show location"
                setPadding(8, 8, 8, 8)
                setOnClickListener {
                    val intent = Intent(this@RecordActivity, MapActivity::class.java)
                    intent.putExtra("lat", score.lat)
                    intent.putExtra("lon", score.lon)
                    intent.putExtra("playerName", score.name)
                    startActivity(intent)
                }
            }
            row.addView(icon)
            tableLayout.addView(row)
        }
    }
}
