package com.example.oniongarlicrun.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.oniongarlicrun.utils.TiltCallback
import kotlin.math.abs

class TiltDetector(context: Context, private var tiltCallback: TiltCallback?) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) as Sensor
    private lateinit var sensorEventListener: SensorEventListener

    var tiltCounterX: Int = 0
        private set

    var tiltCounterY: Int = 0
        private set

    private var timestamp: Long = 0L

    init {
        initEventListener()
    }

    private fun initEventListener() {
        sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val y = event.values[1]
                calculateTilt(x, y)
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }
        }
    }

    private fun calculateTilt(x: Float, y: Float) {
        if (System.currentTimeMillis() - timestamp >= 500) {
            timestamp = System.currentTimeMillis()
            if (abs(x) >= 3.0) {
                tiltCounterX = if (x > 0) 1 else -1
                tiltCallback?.tiltX()
            }

            if (abs(y) >= 3.0) {
                tiltCounterY++
                tiltCallback?.tiltY()
            }
        }
    }

    fun start() {
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stop() {
        sensorManager.unregisterListener(sensorEventListener, sensor)
    }
}
