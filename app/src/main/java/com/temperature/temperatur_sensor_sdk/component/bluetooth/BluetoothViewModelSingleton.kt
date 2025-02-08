package com.temperature.temperatur_sensor_sdk.component.bluetooth

import android.app.Application


object BluetoothViewModelSingleton {
    private var instance: BluetoothViewModel? = null

    fun getInstance(application: Application): BluetoothViewModel {
        return instance ?: synchronized(this) {
            instance ?: BluetoothViewModel(application).also { instance = it }
        }
    }
}