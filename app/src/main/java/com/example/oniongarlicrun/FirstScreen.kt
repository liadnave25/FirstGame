package com.example.oniongarlicrun
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class FirstScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.first_screen)

        val btnPlay = findViewById<Button>(R.id.btnPlay)
        val btnRecords = findViewById<Button>(R.id.btnRecords)

        btnPlay.setOnClickListener {
            startActivity(Intent(this, StartApp::class.java))
        }

        btnRecords.setOnClickListener {
            startActivity(Intent(this, RecordActivity::class.java))
        }
    }
}
