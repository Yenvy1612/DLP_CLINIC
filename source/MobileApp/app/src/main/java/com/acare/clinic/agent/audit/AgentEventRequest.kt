package com.acare.clinic.agent.audit

data class AgentEventRequest(
    val deviceId: String,
    val platform: String = "ANDROID",
    val userId: Long?,
    val sourceType: String = "ANDROID_AGENT",
    val eventType: String,
    val action: String,
    val violationType: String?,
    val severity: String,
    val contentSnippet: String?,
    val details: Map<String, Any?> = emptyMap(),
    val timestamp: String
)