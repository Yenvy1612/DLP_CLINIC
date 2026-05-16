package com.acare.clinic.agent.queue

import com.google.gson.Gson
import com.acare.clinic.agent.audit.AgentEventRequest
import com.acare.clinic.agent.storage.AgentEventEntity
import com.acare.clinic.agent.storage.AgentEventDao

class EventQueueRepository(
    private val dao: AgentEventDao
) {
    private val gson = Gson()

    suspend fun enqueue(request: AgentEventRequest) {
        dao.insert(
            AgentEventEntity(
                deviceId = request.deviceId,
                platform = request.platform,
                userId = request.userId,
                sourceType = request.sourceType,
                eventType = request.eventType,
                action = request.action,
                violationType = request.violationType,
                severity = request.severity,
                contentSnippet = request.contentSnippet,
                detailsJson = gson.toJson(request.details),
                timestamp = request.timestamp,
                synced = false
            )
        )
    }

    fun toRequest(entity: AgentEventEntity): AgentEventRequest {
        @Suppress("UNCHECKED_CAST")
        val details = gson.fromJson(entity.detailsJson, Map::class.java) as Map<String, Any?>

        return AgentEventRequest(
            deviceId = entity.deviceId,
            platform = entity.platform,
            userId = entity.userId,
            sourceType = entity.sourceType,
            eventType = entity.eventType,
            action = entity.action,
            violationType = entity.violationType,
            severity = entity.severity,
            contentSnippet = entity.contentSnippet,
            details = details,
            timestamp = entity.timestamp
        )
    }
}