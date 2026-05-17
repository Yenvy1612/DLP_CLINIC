package com.acare.clinic.agent.queue;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0086@\u00a2\u0006\u0002\u0010\u000bJ\u000e\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Lcom/acare/clinic/agent/queue/EventQueueRepository;", "", "dao", "Lcom/acare/clinic/agent/storage/AgentEventDao;", "(Lcom/acare/clinic/agent/storage/AgentEventDao;)V", "gson", "Lcom/google/gson/Gson;", "enqueue", "", "request", "Lcom/acare/clinic/agent/audit/AgentEventRequest;", "(Lcom/acare/clinic/agent/audit/AgentEventRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toRequest", "entity", "Lcom/acare/clinic/agent/storage/AgentEventEntity;", "app_debug"})
public final class EventQueueRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.acare.clinic.agent.storage.AgentEventDao dao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.gson.Gson gson = null;
    
    public EventQueueRepository(@org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.storage.AgentEventDao dao) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object enqueue(@org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.audit.AgentEventRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.acare.clinic.agent.audit.AgentEventRequest toRequest(@org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.storage.AgentEventEntity entity) {
        return null;
    }
}