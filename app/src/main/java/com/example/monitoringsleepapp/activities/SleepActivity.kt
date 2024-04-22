package com.example.monitoringsleepapp.activities

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.monitoringsleepapp.R
import com.example.monitoringsleepapp.SleepViewModelProvider
import com.example.monitoringsleepapp.data.SleepViewModel
import com.example.monitoringsleepapp.data.SensorData
import com.example.monitoringsleepapp.data.SensorDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val NOTIFICATION_ID = 123
private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 12345

class SleepActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometerSensor: Sensor? = null
    private var gyroscopeSensor: Sensor? = null
    private lateinit var viewModel: SleepViewModel
    private var isMonitoring = false
    private lateinit var database: SensorDatabase

    private val CHANNEL_ID = "MonitoringChannel"
    private val CHANNEL_NAME = "Monitoring Channel"
    private val NOTIFICATION_TEXT = "Currently Monitoring"
    private lateinit var notificationManager: NotificationManagerCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sleep)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)


        val startButton = findViewById<Button>(R.id.sleepstart)
        val stopButton = findViewById<Button>(R.id.stopsleep)

        val sleepButton = findViewById<Button>(R.id.sleep)
        val journalButton = findViewById<Button>(R.id.journal)
        val statisticsButton = findViewById<Button>(R.id.statistics)
        val settingsButton = findViewById<Button>(R.id.settings)

        // Initialize Room database instance
        database = Room.databaseBuilder(
            applicationContext,
            SensorDatabase::class.java,
            "sensor_database"
        ).build()


        viewModel = ViewModelProvider(
            this,
        SleepViewModelProvider.Factory
        ).get(SleepViewModel::class.java)

        notificationManager = NotificationManagerCompat.from(this)
        createNotificationChannel()

        startButton.setOnClickListener {
            startMonitoring()
        }

        stopButton.setOnClickListener {
            stopMonitoring()
        }

        sleepButton.setOnClickListener {
            val intent = Intent(this, SleepActivity::class.java)
            startActivity(intent)
        }

        journalButton.setOnClickListener {
            val intent = Intent(this, JournalActivity::class.java)
            startActivity(intent)
        }

        statisticsButton.setOnClickListener {
            val intent = Intent(this, StatisticsActivity::class.java)
            startActivity(intent)
        }

        settingsButton.setOnClickListener {
            val intent = Intent(this, PreferencesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                lightColor = Color.BLUE
                enableLights(true)
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(NOTIFICATION_TEXT)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Show the notification
        if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, send the notification
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        } else {
            // Permission is not granted, request it from the user
            requestNotificationPermission()
        }

    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                showNotification()
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hideNotification() {
        notificationManager.cancel(NOTIFICATION_ID)
    }

    private fun startMonitoring() {
        if (!isMonitoring) {

            viewModel.deleteAllSensorData()
            viewModel.getSensorDataFromDatabase()
            showNotification()

            sensorManager.registerListener(this, accelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL)
            isMonitoring = true
            Toast.makeText(this, "Monitoring started", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.getSensorDataFromDatabase()
            Toast.makeText(this, "Monitoring already started", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopMonitoring() {
        if (isMonitoring) {
            hideNotification()
            sensorManager.unregisterListener(this)
            isMonitoring = false
            Toast.makeText(this, "Monitoring stopped", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Monitoring not started", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER || event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            val sensorData = SensorData(
                accelerationX = event?.values?.getOrNull(0) ?: 0f,
                accelerationY = event?.values?.getOrNull(1) ?: 0f,
                accelerationZ = event?.values?.getOrNull(2) ?: 0f,
                rotationX = event?.values?.getOrNull(0) ?: 0f,
                rotationY = event?.values?.getOrNull(1) ?: 0f,
                rotationZ = event?.values?.getOrNull(2) ?: 0f,
                timestamp = System.currentTimeMillis()
            )
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.insertSensorData(sensorData)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }
}