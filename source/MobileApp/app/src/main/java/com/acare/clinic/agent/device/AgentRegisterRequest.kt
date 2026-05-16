package com.acare.clinic.agent.device

data class AgentRegisterRequest(
    val deviceId: String,
    val platform: String = "ANDROID",
    val hostname: String? = null,
    val deviceName: String,
    val osVersion: String,
    val agentVersion: String,
    val appVersion: String,
    val username: String?
)