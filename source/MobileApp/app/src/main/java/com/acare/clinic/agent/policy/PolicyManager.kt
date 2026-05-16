package com.acare.clinic.agent.policy
import com.acare.clinic.agent.network.AgentApiService
import com.acare.clinic.agent.storage.AgentPreferences

class PolicyManager(
    private val api: AgentApiService,
    private val preferences: AgentPreferences
) {
    private var cachedPolicy: AgentPolicyResponse? = null

    suspend fun syncPolicy(): AgentPolicyResponse {
        val response = api.getPolicy()
        val policy = response.result ?: defaultPolicy()

        cachedPolicy = policy
        preferences.savePolicyVersion(policy.version)

        return policy
    }

    fun getCachedPolicyOrDefault(): AgentPolicyResponse {
        return cachedPolicy ?: defaultPolicy()
    }

    private fun defaultPolicy(): AgentPolicyResponse {
        return AgentPolicyResponse(
            version = "local-default",
            patterns = listOf(
                PatternRule("CCCD", "\\b0\\d{11}\\b", "HIGH"),
                PatternRule("PHONE", "\\b(84|0[35789])\\d{8}\\b", "MEDIUM"),
                PatternRule("EMAIL", "\\b[\\w.-]+@[\\w.-]+\\.\\w{2,4}\\b", "MEDIUM")
            ),
            keywords = listOf("HIV", "Ung thư", "Tuyệt mật"),
            settings = mapOf(
                "scanForm" to true,
                "scanCopy" to true,
                "scanExport" to true,
                "blockCopyOnViolation" to true,
                "blockExportOnViolation" to true
            )
        )
    }
}