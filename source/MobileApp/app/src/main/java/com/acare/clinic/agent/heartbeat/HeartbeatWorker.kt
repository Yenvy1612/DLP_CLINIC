package com.acare.clinic.agent.heartbeat

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result
import com.acare.clinic.agent.core.AgentInitializer
class HeartbeatWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val agent = AgentInitializer.getAgentManager()
            agent.sendHeartbeat()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}