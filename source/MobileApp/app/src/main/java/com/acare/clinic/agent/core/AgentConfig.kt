package com.acare.clinic.agent.core

data class AgentConfig(
    val backendBaseUrl: String,
    val appVersion: String,
    val agentVersion: String = "1.0.0",
    val platform: String = "ANDROID"
)