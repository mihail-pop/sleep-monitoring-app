package com.example.monitoringsleepapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.monitoringsleepapp.R
import com.example.monitoringsleepapp.data.SensorData
import com.example.monitoringsleepapp.data.SensorDataRepositoryImpl
import com.example.monitoringsleepapp.data.SensorDatabase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StatisticsActivity : AppCompatActivity() {

    private lateinit var lineChart: LineChart
    private lateinit var lineChart2: LineChart
    private lateinit var sensorDataRepository: SensorDataRepositoryImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistics)

        val sleepButton = findViewById<Button>(R.id.sleep)
        val journalButton = findViewById<Button>(R.id.journal)
        val statisticsButton = findViewById<Button>(R.id.statistics)
        val settingsButton = findViewById<Button>(R.id.settings)

        lineChart = findViewById(R.id.lineChart)
        lineChart2 = findViewById(R.id.lineChart2)

        sensorDataRepository = SensorDataRepositoryImpl(
            SensorDatabase.getInstance(applicationContext).sensorDataDao()
        )

        loadDataAndPopulateCharts()

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

    private fun loadDataAndPopulateCharts() {
        GlobalScope.launch(Dispatchers.Main) {
            val sensorDataList = sensorDataRepository.getAllSensorData()
            val accelerometerEntries = mutableListOf<Entry>()
            val gyroscopeEntries = mutableListOf<Entry>()

            sensorDataList.forEachIndexed { index, sensorData ->

                val accelerometerSum = sensorData.accelerationX + sensorData.accelerationY + sensorData.accelerationZ
                accelerometerEntries.add(Entry(index.toFloat(), accelerometerSum))
                Log.d("StatisticsActivity", "accX: $sensorData.accelerationX")
                val gyroscopeSum = sensorData.rotationX + sensorData.rotationY + sensorData.rotationZ
                gyroscopeEntries.add(Entry(index.toFloat(), gyroscopeSum))
                Log.d("StatisticsActivity", "gyroX: $sensorData.rotationX")
            }

            populateLineChart(lineChart, accelerometerEntries, "Accelerometer Sum", sensorDataList)
            populateLineChart(lineChart2, gyroscopeEntries, "Gyroscope Sum", sensorDataList)
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }

    private fun populateLineChart(lineChart: LineChart, entries: List<Entry>, label: String, sensorDataList: List<SensorData>) {
        val dataSet = LineDataSet(entries, label)
        dataSet.setDrawCircleHole(false)
        dataSet.setDrawCircles(false)
        val lineData = LineData(dataSet)

        lineChart.data = lineData
        lineChart.invalidate()

        // X-axis
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        lineChart.xAxis.setDrawGridLines(false)
        lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                // Convert the X-axis value (index) to timestamp
                val timestamp = sensorDataList[value.toInt()].timestamp
                val formattedTimestamp = formatTimestamp(timestamp)

                return formattedTimestamp
            }
        }



        // Y-axis
        lineChart.axisRight.isEnabled = false

        lineChart.description.text = "Time (HH:mm:ss)"
    }
}