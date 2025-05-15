package com.ganaa.carcompanion

data class VehicleData(
    val rpm: Float = 0f,
    val speed: Float = 0f,
    val engineTemp: Float = 0f,
    val mapSensor: Float = 0f,
    val tirePressure: TirePressure = TirePressure()
)

data class TirePressure(
    val frontLeft: Float = 0f,
    val frontRight: Float = 0f,
    val rearLeft: Float = 0f,
    val rearRight: Float = 0f
)