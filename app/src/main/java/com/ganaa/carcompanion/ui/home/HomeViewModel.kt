package com.ganaa.carcompanion.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ganaa.carcompanion.bluetooth.BluetoothManager
import com.ganaa.carcompanion.obd.OBDParser
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.ganaa.carcompanion.VehicleData
import com.ganaa.carcompanion.VehicleDataService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val bluetoothManager = BluetoothManager(application.applicationContext)
    private val obdParser = OBDParser()

    // For UI binding — optional, can be removed if not needed
    private val _text = MutableLiveData<String>().apply {
        value = "This is home fragment"
    }
    val text: LiveData<String> = _text

    // Connection status LiveData
    private val _connectionStatus = MutableLiveData<Boolean>()
    val connectionStatus: LiveData<Boolean> = _connectionStatus

    // OBD data LiveData (replace Any with your actual data type)
    private val _obdData = MutableLiveData<Any>()  // TODO: Replace 'Any' with your actual OBD data type
    val obdData: LiveData<Any> = _obdData

    private val TAG = "HomeViewModel"
    private val vehicleDataService = VehicleDataService()
    private val _vehicleData = MutableLiveData<VehicleData>()
    val vehicleData: LiveData<VehicleData> = _vehicleData

    init {
        startDataFetching()
    }
    
    private fun startDataFetching() {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    val data = vehicleDataService.fetchVehicleData()
                    _vehicleData.postValue(data)
                    _connectionStatus.postValue(true)
                    val connected = !(data.rpm == 0f && data.speed == 0f && data.engineTemp == 0f && data.mapSensor == 0f)
                    _connectionStatus.postValue(connected)
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching data. TESTING!!!!", e)
                    _connectionStatus.postValue(false)
                }
                delay(1000) // Fetch data every second
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            try {
                val data = vehicleDataService.fetchVehicleData()
                _vehicleData.value = data
                _connectionStatus.value = true
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing data", e)
                _connectionStatus.value = false
            }
        }
    }

    // You can add more logic here for updating these values
}
