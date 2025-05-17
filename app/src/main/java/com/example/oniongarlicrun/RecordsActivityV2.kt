package com.example.oniongarlicrun

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.oniongarlicrun.utils.HighScoreAdapter
import com.example.oniongarlicrun.utils.ScoreManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class RecordsActivityV2 : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records)

        recyclerView = findViewById(R.id.records_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val scores = ScoreManager.load(this)
        recyclerView.adapter = HighScoreAdapter(scores) { lat, lon ->
            showMapAtLocation(lat, lon)
        }

        mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment?.getMapAsync { map ->
            googleMap = map
        }

        findViewById<Button>(R.id.btnBackToMenu).setOnClickListener {
            val intent = Intent(this, FirstScreen::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun showMapAtLocation(lat: Double, lon: Double) {
        val position = LatLng(lat, lon)
        googleMap?.clear()
        googleMap?.addMarker(MarkerOptions().position(position).title("מיקום שיא"))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
        findViewById<View>(R.id.map_fragment).visibility = View.VISIBLE
    }
}
