package com.example.monitoringsleepapp.data

import androidx.lifecycle.LiveData

interface SensorDataRepository {
    val allSensorData: LiveData<List<SensorData>>

    suspend fun insert(sensorData: SensorData)

    suspend fun getAllSensorData(): List<SensorData>

    suspend fun deleteAllSensorData()
}