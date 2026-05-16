package com.acare.clinic.agent.policy

data class AgentPolicyResponse(
    val version: String,
    val patterns: List<PatternRule>,
    val keywords: List<String>,
    val settings: Map<String, Any>
)