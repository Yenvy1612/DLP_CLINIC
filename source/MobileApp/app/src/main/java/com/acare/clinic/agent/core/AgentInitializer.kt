package com.acare.clinic.agent.core

import android.content.Context

object AgentInitializer {

    private lateinit var agentManager: AgentManager
    private lateinit var eventTracker: AgentEventTracker

    fun init(
        context: Context,
        config: AgentConfig,
        tokenProvider: TokenProvider
    ) {
        val appContext = context.applicationContext

        val database = AgentDatabase.getInstance(appContext)
        val eventDao = database.agentEventDao()

        val preferences = AgentPreferences(appContext)
        val deviceIdProvider = DeviceIdProvider(appContext)
        val deviceInfoCollector = DeviceInfoCollector(deviceIdProvider)

        val api = RetrofitClient.create(
            baseUrl = config.backendBaseUrl,
            tokenProvider = tokenProvider
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
    }

    fun getAgentManager(): AgentManager = agentManager

    fun getEventTracker(): AgentEventTracker = eventTracker
}