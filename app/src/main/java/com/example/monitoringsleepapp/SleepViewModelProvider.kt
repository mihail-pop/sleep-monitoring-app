package com.example.monitoringsleepapp

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.monitoringsleepapp.data.SleepViewModel

object SleepViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            SleepViewModel(
                repository = monitoringSleepApp().sensorDataRepository
            )
        }
    }
}

fun CreationExtras.monitoringSleepApp(): MonitoringSleepApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MonitoringSleepApp)