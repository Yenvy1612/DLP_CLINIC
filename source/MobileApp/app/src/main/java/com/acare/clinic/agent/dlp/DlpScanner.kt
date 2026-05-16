package com.acare.clinic.agent.dlp
import com.acare.clinic.agent.policy.PolicyManager

class DlpScanner(
    private val policyManager: PolicyManager
) {
    fun scan(text: String): DlpScanResult {
        if (text.isBlank()) {
            return DlpScanResult(
                isViolation = false,
                violations = emptyList(),
                severity = "LOW"
            )
        }

        val policy = policyManager.getCachedPolicyOrDefault()
        val violations = mutableListOf<String>()
        var maxSeverity = "LOW"

        policy.patterns.forEach { rule ->
            val regex = Regex(rule.regex, RegexOption.IGNORE_CASE)

            if (regex.containsMatchIn(text)) {
                violations.add(rule.name)
                maxSeverity = higherSeverity(maxSeverity, rule.severity)
            }
        }

        policy.keywords.forEach { keyword ->
            if (text.contains(keyword, ignoreCase = true)) {
                violations.add("KEYWORD:$keyword")
                maxSeverity = higherSeverity(maxSeverity, "CRITICAL")
            }
        }

        return DlpScanResult(
            isViolation = violations.isNotEmpty(),
            violations = violations,
            severity = maxSeverity
        )
    }

    private fun higherSeverity(current: String, incoming: String): String {
        val order = mapOf(
            "LOW" to 1,
            "MEDIUM" to 2,
            "HIGH" to 3,
            "CRITICAL" to 4
        )

        val currentValue = order[current.uppercase()] ?: 1
        val incomingValue = order[incoming.uppercase()] ?: 1

        return if (incomingValue > currentValue) {
            incoming.uppercase()
        } else {
            current.uppercase()
        }
    }
}