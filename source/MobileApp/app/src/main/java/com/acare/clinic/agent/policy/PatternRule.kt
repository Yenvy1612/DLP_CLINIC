package com.acare.clinic.agent.policy

data class PatternRule(
    val name: String,
    val regex: String,
    val severity: String
)