package com.acare.clinic.agent.heartbeat

data class HeartbeatRequest(
    val deviceId: String,
    val platform: String = "ANDROID",
    val status: String = "ONLINE",
    val batteryLevel: Int? = null,
    val networkType: String? = null,
    val appVersion: String? = null
)