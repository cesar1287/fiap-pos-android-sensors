package com.github.cesar1287.sensores

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.github.cesar1287.sensores.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        printSensors()
        specificSensor()
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


}