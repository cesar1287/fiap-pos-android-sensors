package com.github.cesar1287.sensores

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.cesar1287.sensores.databinding.ActivityMainBinding
import java.lang.Math.abs
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), SensorEventListener {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val sensorManager: SensorManager by lazy {
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    private lateinit var tempSensor: Sensor

    private lateinit var accelerometerSensor: Sensor

    private lateinit var gravitySensor: Sensor

    private lateinit var stepCounter: Sensor
    private lateinit var stepDetector: Sensor

    private var stepDetect = 0

    private lateinit var mStepsDBHelper: StepsDBHelper
    private lateinit var mStepCountList: ArrayList<Step>

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

//        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) {
//            Log.i("SENSOR", "ACELEROMETRO INDISPONÍVEL")
//        } else {
//            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//        }

//        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) == null) {
//            Log.i("SENSOR", "SENSOR DE GRAVIDADE INDISPONÍVEL")
//        } else {
//            gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
//        }
        checkPermissions()

        binding.ivRefresh.setOnClickListener {
            initList()
        }

    }

    private fun initList() {
        mStepsDBHelper = StepsDBHelper(this)
        mStepCountList = mStepsDBHelper.readStepsEntries()

        val mCalendar = Calendar.getInstance()
        val todayDate =
            (mCalendar.get(Calendar.DAY_OF_MONTH)
                .toString() + "/" + (mCalendar.get(Calendar.MONTH) + 1).toString()
                    + "/" + mCalendar.get(Calendar.YEAR).toString())

        if(mStepCountList.size > 0) {
            if(todayDate == mStepCountList[0].mDate) {
                stepDetect = mStepCountList[0].mStepCount
                setUpTodayStep()
            }
        }

        binding.lvSteps.adapter = StepsListAdapter(this, mStepCountList)
    }

    private fun initSensors() {
        initList()
        initStepCounter()
        initStepDetect()
    }

    private fun initStepCounter() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) == null) {
            Log.i("SENSOR", "Sensor contador de passos indisponivel")
        } else {
            stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        }
    }

    private fun initStepDetect() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) == null) {
            Log.i("SENSOR", "SENSOR detector de passos indisponivel")
        } else {
            stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        }
    }

    override fun onResume() {
        super.onResume()
        if (this::gravitySensor.isInitialized) {
            sensorManager.registerListener(
                this,
                gravitySensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        if (this::stepCounter.isInitialized) {
            sensorManager.registerListener(
                this,
                stepCounter,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        if (this::stepDetector.isInitialized) {
            sensorManager.registerListener(
                this,
                stepDetector,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
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

    private fun setUpTodayStep() {
        binding.tvSteps.text = "$stepDetect / 3000"
    }


    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent?.let {
            //shakeDetection(it)
            //flippedDetection(it)
            when (it.sensor) {
                stepCounter -> {
                    //binding.tvCounted.text = it.values[0].toString()
                }
                stepDetector -> {
                    StepsDBHelper(this).createStepsEntry()
                    stepDetect += it.values[0].toInt()
                    setUpTodayStep()
                }
                gravitySensor -> {

                }
                accelerometerSensor -> {

                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Verifica o status da permissão
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        android.Manifest.permission.ACTIVITY_RECOGNITION
                    )
                ) {
                    // O usuário se recusa completamente a conceder permissão e geralmente solicita que o usuário entre na interface de configuração de permissão.
                    // O usuário irá precisar entrar na interface de configuração de permissão para abrir
                } else {
                    //Solicitação do consentimento do usuario
                    val permissions = listOf(android.Manifest.permission.ACTIVITY_RECOGNITION)
                    ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 1);
                }
            } else {
                // Permissão concedida pelo usuário
                initSensors()
            }
        } else {
            initSensors()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            for (i in permissions.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    // Permissão concedida
                    initSensors()
                } else {
                    // Permissão Negada
                    // application failed
                    Log.d("TAG", "[Permission]" + "ACTIVITY_RECOGNITION application failed")
                }
            }
        }
    }


private fun flippedDetection(it: SensorEvent) {
        if (it.values[2] < -9.7) {
            Log.i("SENSOR", "Media paused")
        } else {
            Log.i("SENSOR", "Media started")
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
}