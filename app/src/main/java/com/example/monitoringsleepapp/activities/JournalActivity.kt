package com.example.monitoringsleepapp.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.monitoringsleepapp.R

class JournalActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val TAG = "JournalActivity"
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.journal)

        sharedPreferences = getSharedPreferences("JournalPreferences", MODE_PRIVATE)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val btnBad = findViewById<Button>(R.id.btnBad)
        val btnAverage = findViewById<Button>(R.id.btnAverage)
        val btnGood = findViewById<Button>(R.id.btnGood)
        val tvSelectedChoice = findViewById<TextView>(R.id.tvSelectedChoice)

        val sleepButton = findViewById<Button>(R.id.sleep)
        val journalButton = findViewById<Button>(R.id.journal)
        val statisticsButton = findViewById<Button>(R.id.statistics)
        val settingsButton = findViewById<Button>(R.id.settings)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/${year}"
            val selectedChoice = sharedPreferences.getString(selectedDate, "Nothing chosen") ?: "Nothing chosen"
            tvSelectedChoice.text = selectedChoice

            Log.d(TAG, "for date '$selectedDate'")
        }

        btnBad.setOnClickListener {
            saveChoice("Bad")
            tvSelectedChoice.text = "Bad"
        }

        btnAverage.setOnClickListener {
            saveChoice("Average")
            tvSelectedChoice.text = "Average"
        }

        btnGood.setOnClickListener {
            saveChoice("Good")
            tvSelectedChoice.text = "Good"
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

    private fun saveChoice(choice: String) {
        if (selectedDate.isNullOrEmpty()) {
            Log.e(TAG, "Selected date is null or empty.")
            return
        }

        Log.d(TAG, "Saving choice '$choice' for date '$selectedDate'")

        with(sharedPreferences.edit()) {
            putString(selectedDate, choice)
            apply()
        }
    }
}
