package com.acare.clinic.agent.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AgentEventDao {

    @Insert
    suspend fun insert(event: AgentEventEntity)

    @Query("SELECT * FROM agent_events WHERE synced = 0 ORDER BY id ASC LIMIT :limit")
    suspend fun getPendingEvents(limit: Int): List<AgentEventEntity>

    @Query("UPDATE agent_events SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: Long)

    @Query("DELETE FROM agent_events WHERE synced = 1")
    suspend fun deleteSynced()
}