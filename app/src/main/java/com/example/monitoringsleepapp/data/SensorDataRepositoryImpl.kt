package com.example.monitoringsleepapp.data

import androidx.lifecycle.LiveData

class SensorDataRepositoryImpl(private val sensorDataDao: SensorDataDao) : SensorDataRepository {

    override val allSensorData: LiveData<List<SensorData>> = sensorDataDao.getAllSensorData()

    override suspend fun insert(sensorData: SensorData) {
        sensorDataDao.insert(sensorData)
    }

    override suspend fun getAllSensorData(): List<SensorData> {
        return sensorDataDao.getAllSensorDataSync()
    }

    override suspend fun deleteAllSensorData() {
        sensorDataDao.deleteAllSensorData()
    }
}

