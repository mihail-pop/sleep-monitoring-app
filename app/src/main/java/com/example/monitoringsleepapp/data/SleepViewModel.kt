package com.example.monitoringsleepapp.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SleepViewModel(private val repository: SensorDataRepository) : ViewModel() {

    fun insertSensorData(sensorData: SensorData) {
        viewModelScope.launch {
            repository.insert(sensorData)
        }
    }

    fun deleteAllSensorData() {
        viewModelScope.launch {
            repository.deleteAllSensorData()
        }
    }

    fun getSensorDataFromDatabase() {
        viewModelScope.launch {
            val sensorDataList = repository.getAllSensorData()
            // Log the number of retrieved sensor data items
            Log.d("SensorData", "Number of items: ${sensorDataList.size}")

            // Log each sensor data object if needed
            for (sensorData in sensorDataList) {
                Log.d("SensorData", sensorData.toString())
            }
        }
    }
}
