package com.acare.clinic.agent.queue

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object EventSyncScheduler {

    fun start(context: Context) {
        val request = PeriodicWorkRequestBuilder<EventSyncWorker>(
            15,
            TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "agent_event_sync",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}