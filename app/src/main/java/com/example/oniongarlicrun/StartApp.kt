package com.example.oniongarlicrun
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class StartApp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.start_app)

        val btnSlow = findViewById<Button>(R.id.btnSlow)
        val btnFast = findViewById<Button>(R.id.btnFast)
        val btnSensor = findViewById<Button>(R.id.btnSensor)

        btnSlow.setOnClickListener {
            launchGame("slow")
        }

        btnFast.setOnClickListener {
            launchGame("fast")
        }

        btnSensor.setOnClickListener {
            launchGame("sensor")
        }
    }

    private fun launchGame(mode: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("MODE", mode)
        startActivity(intent)
        finish()

    }
}
