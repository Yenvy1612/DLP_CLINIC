package com.acare.clinic.agent.device

data class DeviceInfo(
    val deviceId: String,
    val deviceName: String,
    val osVersion: String,
    val manufacturer: String,
    val model: String
)