package com.acare.clinic.agent.core

class AgentManager(
    private val config: AgentConfig,
    private val api: AgentApiService,
    private val deviceIdProvider: DeviceIdProvider,
    private val deviceInfoCollector: DeviceInfoCollector,
    private val policyManager: PolicyManager,
    private val queueRepository: EventQueueRepository,
    private val eventDao: AgentEventDao
) {
    suspend fun register(username: String?) {
        val info = deviceInfoCollector.collect()

        val request = AgentRegisterRequest(
            deviceId = info.deviceId,
            platform = config.platform,
            deviceName = info.deviceName,
            osVersion = info.osVersion,
            agentVersion = config.agentVersion,
            appVersion = config.appVersion,
            username = username
        )

        api.registerAgent(request)
    }

    suspend fun sendHeartbeat() {
        val deviceId = deviceIdProvider.getDeviceId()

        api.sendHeartbeat(
            deviceId = deviceId,
            request = HeartbeatRequest(
                deviceId = deviceId,
                appVersion = config.appVersion
            )
        )
    }

    suspend fun syncPolicy() {
        policyManager.syncPolicy()
    }

    suspend fun syncPendingEvents() {
        val pendingEvents = eventDao.getPendingEvents(limit = 50)

        pendingEvents.forEach { entity ->
            val request = queueRepository.toRequest(entity)
            api.sendAgentEvent(request)
            eventDao.markSynced(entity.id)
        }
    }
}