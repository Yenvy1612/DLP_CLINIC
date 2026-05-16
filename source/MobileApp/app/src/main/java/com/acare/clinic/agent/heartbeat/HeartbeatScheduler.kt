package com.acare.clinic.agent.heartbeat

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object HeartbeatScheduler {

    fun start(context: Context) {
        val request = PeriodicWorkRequestBuilder<HeartbeatWorker>(
            15,
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "agent_heartbeat",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}