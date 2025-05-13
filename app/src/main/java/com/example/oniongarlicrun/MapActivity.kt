package com.example.oniongarlicrun

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.widget.Button



class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity)


        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Player Location"

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<Button>(R.id.btnBackToRecords).setOnClickListener {
            finish()
        }
    }

    // התנהגות של כפתור חזור ← מסיים את הפעילות
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val lat = intent.getDoubleExtra("lat", 32.1062)
        val lon = intent.getDoubleExtra("lon", 34.8177)
        val playerName = intent.getStringExtra("playerName") ?: "Player"
        val loc = LatLng(lat, lon)

        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.place)
        val smallIcon = Bitmap.createScaledBitmap(bitmap, 80, 80, false)

        mMap.addMarker(
            MarkerOptions()
                .position(loc)
                .title(playerName)
                .icon(BitmapDescriptorFactory.fromBitmap(smallIcon))
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f))
    }
}
