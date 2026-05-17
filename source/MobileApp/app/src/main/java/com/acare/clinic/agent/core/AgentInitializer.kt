package com.acare.clinic.agent.core

import android.content.Context
import android.util.Log
import com.acare.clinic.agent.audit.AgentEventTracker
import com.acare.clinic.agent.device.DeviceIdProvider
import com.acare.clinic.agent.device.DeviceInfoCollector
import com.acare.clinic.agent.dlp.DlpScanner
import com.acare.clinic.agent.heartbeat.HeartbeatScheduler
import com.acare.clinic.agent.network.RetrofitClient
import com.acare.clinic.agent.policy.PolicyManager
import com.acare.clinic.agent.queue.EventQueueRepository
import com.acare.clinic.agent.queue.EventSyncScheduler
import com.acare.clinic.agent.storage.AgentDatabase
import com.acare.clinic.agent.storage.AgentPreferences
import okhttp3.OkHttpClient

/**
 * AgentInitializer — điểm khởi tạo duy nhất cho Agent module.
 *
 * Sử dụng OkHttpClient có sẵn CookieJar từ NetworkClient
 * để gửi request agent với cùng session xác thực của user.
 */
object AgentInitializer {

    private const val TAG = "AgentInitializer"

    private lateinit var agentManager: AgentManager
    private lateinit var eventTracker: AgentEventTracker

    private var initialized = false

    /**
     * Khởi tạo Agent module dùng OkHttpClient có CookieJar (cookie-based auth).
     * Đây là cách ưu tiên vì app dùng HttpOnly cookie.
     */
    fun init(
        context: Context,
        config: AgentConfig,
        okHttpClient: OkHttpClient
    ) {
        if (initialized) {
            Log.d(TAG, "Agent already initialized, skipping")
            return
        }

        val appContext = context.applicationContext

        val database = AgentDatabase.getInstance(appContext)
        val eventDao = database.agentEventDao()

        val preferences = AgentPreferences(appContext)
        val deviceIdProvider = DeviceIdProvider(appContext)
        val deviceInfoCollector = DeviceInfoCollector(deviceIdProvider)

        val api = RetrofitClient.create(
            baseUrl = config.backendBaseUrl,
            okHttpClient = okHttpClient
        )

        val policyManager = PolicyManager(api, preferences)
        val dlpScanner = DlpScanner(policyManager)
        val queueRepository = EventQueueRepository(eventDao)

        agentManager = AgentManager(
            config = config,
            api = api,
            deviceIdProvider = deviceIdProvider,
            deviceInfoCollector = deviceInfoCollector,
            policyManager = policyManager,
            queueRepository = queueRepository,
            eventDao = eventDao
        )

        eventTracker = AgentEventTracker(
            deviceIdProvider = deviceIdProvider,
            dlpScanner = dlpScanner,
            queueRepository = queueRepository
        )

        HeartbeatScheduler.start(appContext)
        EventSyncScheduler.start(appContext)

        initialized = true
        Log.i(TAG, "Agent initialized successfully")
    }

    fun isInitialized(): Boolean = initialized

    fun getAgentManager(): AgentManager = agentManager

    fun getEventTracker(): AgentEventTracker = eventTracker
}