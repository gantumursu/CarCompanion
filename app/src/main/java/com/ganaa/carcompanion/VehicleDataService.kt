package com.ganaa.carcompanion

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class VehicleDataService {
    companion object {
        private const val TAG = "VehicleDataService"
        private const val API_URL = "https://pro-together-kitten.ngrok-free.app/api/values"
    }

    suspend fun fetchVehicleData(): VehicleData {
        return withContext(Dispatchers.IO) {
            try {
                val response = URL(API_URL).readText()
                parseVehicleData(response)

            } catch (e: Exception) {
                Log.e(TAG, "Error fetching vehicle data", e)
                VehicleData() // Return empty data on error
            }
        }
    }

    private fun parseVehicleData(jsonString: String): VehicleData {
        try {
            val jsonObject = JSONObject(jsonString)
            Log.d(jsonString, "Parsed JSON: $jsonObject")
            val tirePressure = TirePressure(
                frontLeft = jsonObject.optDouble("tirefl", 0.0).toFloat(),
                frontRight = jsonObject.optDouble("tirefr", 0.0).toFloat(),
                rearLeft = jsonObject.optDouble("tirerl", 0.0).toFloat(),
                rearRight = jsonObject.optDouble("tirerr", 0.0).toFloat(),
            )

            return VehicleData(
                rpm = jsonObject.optDouble("rpm", 0.0).toFloat(),
                speed = jsonObject.optDouble("speed", 0.0).toFloat(),
                engineTemp = jsonObject.optDouble("enginetemp", 0.0).toFloat(),
                mapSensor = jsonObject.optDouble("mapsensor", 0.0).toFloat(),
                tirePressure = tirePressure
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing vehicle data", e)
            return VehicleData()
        }
    }
}