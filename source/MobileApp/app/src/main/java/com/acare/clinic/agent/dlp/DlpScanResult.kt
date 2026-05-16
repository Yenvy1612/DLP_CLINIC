package com.acare.clinic.agent.dlp

data class DlpScanResult(
    val isViolation: Boolean,
    val violations: List<String>,
    val severity: String
)
