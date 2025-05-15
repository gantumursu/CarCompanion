package com.ganaa.carcompanion.obd

enum class OBDCommand(val command: String, val description: String) {
    RPM("01 0C", "Engine RPM"),
    SPEED("01 0D", "Vehicle Speed"),
    ENGINE_COOLANT_TEMP("01 05", "Engine Coolant Temperature"),
    INTAKE_MANIFOLD_PRESSURE("01 0B", "Intake Manifold Pressure"),
    TIRE_PRESSURE_FL("22 1A21", "Tire Pressure Front Left"),
    TIRE_PRESSURE_FR("22 1A22", "Tire Pressure Front Right"),
    TIRE_PRESSURE_RL("22 1A23", "Tire Pressure Rear Left"),
    TIRE_PRESSURE_RR("22 1A24", "Tire Pressure Rear Right"),
    INIT("AT Z", "Reset OBD"),
    ECHO_OFF("AT E0", "Echo Off"),
    PROTOCOL_AUTO("AT SP 0", "Set Protocol Auto"),
    GET_DTC("03", "Get Diagnostic Trouble Codes")
}

