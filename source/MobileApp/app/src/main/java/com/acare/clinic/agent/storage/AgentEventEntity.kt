package com.acare.clinic.agent.storage

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "agent_events")
data class AgentEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val deviceId: String,
    val platform: String,
    val userId: Long?,
    val sourceType: String,
    val eventType: String,
    val action: String,
    val violationType: String?,
    val severity: String,
    val contentSnippet: String?,
    val detailsJson: String,
    val timestamp: String,

    val synced: Boolean = false
)