package com.ganaa.carcompanion.obd

import android.util.Log

class OBDParser {
    companion object {
        private const val TAG = "OBDParser"
    }

    fun parseRPM(rawData: String): Float {
        try {
            // Example response: "41 0C 1A F8"
            val data = rawData.trim().split(" ")
            if (data.size >= 4) {
                val a = data[2].toInt(16)
                val b = data[3].toInt(16)
                return (a * 256 + b) / 4f
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing RPM data: $rawData", e)
        }
        return 0f
    }

    fun parseSpeed(rawData: String): Float {
        try {
            // Example response: "41 0D 45"
            val data = rawData.trim().split(" ")
            if (data.size >= 3) {
                return data[2].toInt(16).toFloat()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Speed data: $rawData", e)
        }
        return 0f
    }

    fun parseEngineTemp(rawData: String): Float {
        try {
            // Example response: "41 05 7B"
            val data = rawData.trim().split(" ")
            if (data.size >= 3) {
                return data[2].toInt(16) - 40f
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Engine Temperature data: $rawData", e)
        }
        return 0f
    }

    fun parseIntakeManifoldPressure(rawData: String): Float {
        try {
            // Example response: "41 0B 1A"
            val data = rawData.trim().split(" ")
            if (data.size >= 3) {
                return data[2].toInt(16).toFloat()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Intake Manifold Pressure data: $rawData", e)
        }
        return 0f
    }

    fun parseTirePressure(rawData: String): Float {
        try {
            // Example response: "62 1A2x yy"
            val data = rawData.trim().split(" ")
            if (data.size >= 3) {
                return data[2].toInt(16) * 0.1f  // Convert to PSI or Bar as needed
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Tire Pressure data: $rawData", e)
        }
        return 0f
    }
}