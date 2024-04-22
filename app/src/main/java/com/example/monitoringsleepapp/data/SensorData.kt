package com.example.monitoringsleepapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "sensor_data")
data class SensorData(
    val accelerationX: Float,
    val accelerationY: Float,
    val accelerationZ: Float,
    val rotationX: Float,
    val rotationY: Float,
    val rotationZ: Float,
    val timestamp: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id: Long = 0
)