package com.example.monitoringsleepapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.lifecycle.LiveData
import com.example.monitoringsleepapp.data.SensorData

@Dao
interface SensorDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sensorData: SensorData)

    @Query("SELECT * FROM sensor_data")
    fun getAllSensorData(): LiveData<List<SensorData>>

    @Query("SELECT * FROM sensor_data")
    suspend fun getAllSensorDataSync(): List<SensorData>

    @Query("DELETE FROM sensor_data")
    suspend fun deleteAllSensorData()
}