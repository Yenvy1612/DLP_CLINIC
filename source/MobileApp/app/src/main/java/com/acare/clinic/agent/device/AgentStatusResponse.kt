package com.acare.clinic.agent.device

data class AgentStatusResponse(
    val installed: Boolean,
    val trusted: Boolean,
    val deviceId: String,
    val platform: String?,
    val status: String?,
    val message: String?
)