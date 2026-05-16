package com.acare.clinic.agent.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.agentDataStore by preferencesDataStore(name = "agent_preferences")

class AgentPreferences(
    private val context: Context
) {
    private val policyVersionKey = stringPreferencesKey("policy_version")

    suspend fun savePolicyVersion(version: String) {
        context.agentDataStore.edit { prefs ->
            prefs[policyVersionKey] = version
        }
    }

    suspend fun getPolicyVersion(): String? {
        val prefs = context.agentDataStore.data.first()
        return prefs[policyVersionKey]
    }
}