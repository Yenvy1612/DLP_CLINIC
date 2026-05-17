package com.acare.clinic.agent.core;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\u0018\u00002\u00020\u0001B=\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r\u0012\u0006\u0010\u000e\u001a\u00020\u000f\u00a2\u0006\u0002\u0010\u0010J\u0018\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0015J\u000e\u0010\u0016\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010\u0017J\u000e\u0010\u0018\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010\u0017J\u000e\u0010\u0019\u001a\u00020\u0012H\u0086@\u00a2\u0006\u0002\u0010\u0017R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Lcom/acare/clinic/agent/core/AgentManager;", "", "config", "Lcom/acare/clinic/agent/core/AgentConfig;", "api", "Lcom/acare/clinic/agent/network/AgentApiService;", "deviceIdProvider", "Lcom/acare/clinic/agent/device/DeviceIdProvider;", "deviceInfoCollector", "Lcom/acare/clinic/agent/device/DeviceInfoCollector;", "policyManager", "Lcom/acare/clinic/agent/policy/PolicyManager;", "queueRepository", "Lcom/acare/clinic/agent/queue/EventQueueRepository;", "eventDao", "Lcom/acare/clinic/agent/storage/AgentEventDao;", "(Lcom/acare/clinic/agent/core/AgentConfig;Lcom/acare/clinic/agent/network/AgentApiService;Lcom/acare/clinic/agent/device/DeviceIdProvider;Lcom/acare/clinic/agent/device/DeviceInfoCollector;Lcom/acare/clinic/agent/policy/PolicyManager;Lcom/acare/clinic/agent/queue/EventQueueRepository;Lcom/acare/clinic/agent/storage/AgentEventDao;)V", "register", "", "username", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sendHeartbeat", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "syncPendingEvents", "syncPolicy", "app_debug"})
public final class AgentManager {
    @org.jetbrains.annotations.NotNull()
    private final com.acare.clinic.agent.core.AgentConfig config = null;
    @org.jetbrains.annotations.NotNull()
    private final com.acare.clinic.agent.network.AgentApiService api = null;
    @org.jetbrains.annotations.NotNull()
    private final com.acare.clinic.agent.device.DeviceIdProvider deviceIdProvider = null;
    @org.jetbrains.annotations.NotNull()
    private final com.acare.clinic.agent.device.DeviceInfoCollector deviceInfoCollector = null;
    @org.jetbrains.annotations.NotNull()
    private final com.acare.clinic.agent.policy.PolicyManager policyManager = null;
    @org.jetbrains.annotations.NotNull()
    private final com.acare.clinic.agent.queue.EventQueueRepository queueRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final com.acare.clinic.agent.storage.AgentEventDao eventDao = null;
    
    public AgentManager(@org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.core.AgentConfig config, @org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.network.AgentApiService api, @org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.device.DeviceIdProvider deviceIdProvider, @org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.device.DeviceInfoCollector deviceInfoCollector, @org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.policy.PolicyManager policyManager, @org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.queue.EventQueueRepository queueRepository, @org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.storage.AgentEventDao eventDao) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object register(@org.jetbrains.annotations.Nullable()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object sendHeartbeat(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object syncPolicy(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object syncPendingEvents(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}