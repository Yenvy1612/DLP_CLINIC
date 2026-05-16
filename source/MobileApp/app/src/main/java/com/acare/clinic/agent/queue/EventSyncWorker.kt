package com.acare.clinic.agent.queue

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result
import com.acare.clinic.agent.core.AgentInitializer
class EventSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val manager = AgentInitializer.getAgentManager()
            manager.syncPendingEvents()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}