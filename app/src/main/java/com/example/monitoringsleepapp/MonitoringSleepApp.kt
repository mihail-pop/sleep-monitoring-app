package com.example.monitoringsleepapp

import android.app.Application
import com.example.monitoringsleepapp.data.SensorDataRepository
import com.example.monitoringsleepapp.data.SensorDataRepositoryImpl
import com.example.monitoringsleepapp.data.SensorDatabase

class MonitoringSleepApp : Application() {

    lateinit var sensorDataRepository: SensorDataRepository

    override fun onCreate() {
        super.onCreate()
        val sensorDataDao = SensorDatabase.getInstance(this).sensorDataDao()
        sensorDataRepository = SensorDataRepositoryImpl(sensorDataDao)
    }
}