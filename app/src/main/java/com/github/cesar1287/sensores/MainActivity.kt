package com.github.cesar1287.sensores

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.github.cesar1287.sensores.databinding.ActivityMainBinding
import java.lang.Math.abs

class MainActivity : AppCompatActivity(), SensorEventListener {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private lateinit var tempSensor: Sensor

    private lateinit var accelerometerSensor: Sensor

    private var currentX = 0.0f
    private var currentY = 0.0f
    private var currentZ = 0.0f

    private var lastX: Float = 0.0f
    private var lastY: Float = 0.0f
    private var lastZ: Float = 0.0f

    private var xDifference: Float = 0.0f
    private var yDifference: Float = 0.0f
    private var zDifference: Float = 0.0f

    private val shakeThreshold = 5.0f

    private var itIsNotFirstTime = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        printSensors()
        specificSensor()

//        if(sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) == null) {
//            Log.i("SENSOR", "SENSOR DE TEMPERATURA INDISPONÍVEL")
//        } else {
//            tempSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
//        }

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) {
            Log.i("SENSOR", "ACELEROMETRO INDISPONÍVEL")
        } else {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }

    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private fun printSensors() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorList: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        for(sensor in sensorList) {
            Log.i("SENSOR: ", "Nome: ${sensor.name} - Tipo ${sensor.type} - ${sensor.stringType} ")
        }
    }

    private fun specificSensor() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if(sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) == null) {
            Log.i("SENSOR", "Device não possui sensor de temperatura")
        } else {
            Log.i("SENSOR", "Device possui sensor de temperatura")
        }
    }

    private fun shakeDetection(it: SensorEvent) {
        currentX = it.values[0]
        currentY = it.values[1]
        currentZ = it.values[2]

        if (itIsNotFirstTime) {
            xDifference = kotlin.math.abs(lastX - currentX)
            yDifference = kotlin.math.abs(lastY - currentY)
            zDifference = kotlin.math.abs(lastZ - currentZ)

            if ((xDifference > shakeThreshold && yDifference > shakeThreshold) ||
                (xDifference > shakeThreshold && zDifference > shakeThreshold) ||
                (yDifference > shakeThreshold && zDifference > shakeThreshold)
            ) {
                Toast.makeText(this, "Shake", Toast.LENGTH_SHORT).show()
            }
        }

        lastX = currentX
        lastY = currentY
        lastZ = currentZ
        itIsNotFirstTime = true
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent?.let {
            shakeDetection(it)
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }
}