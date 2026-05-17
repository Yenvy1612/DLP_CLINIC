package com.acare.clinic.agent.audit;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010$\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\f\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJZ\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u000e2\b\u0010\u0011\u001a\u0004\u0018\u00010\u000e2\b\u0010\u0012\u001a\u0004\u0018\u00010\u000e2\u0014\u0010\u0013\u001a\u0010\u0012\u0004\u0012\u00020\u000e\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u0014H\u0082@\u00a2\u0006\u0002\u0010\u0015J&\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0018\u001a\u00020\f2\u0006\u0010\u0019\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u001aJ&\u0010\u001b\u001a\u00020\u00172\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0018\u001a\u00020\f2\u0006\u0010\u001c\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u001aJ&\u0010\u001d\u001a\u00020\u00172\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u001e\u001a\u00020\u000e2\u0006\u0010\u001f\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010 J\u001e\u0010!\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0018\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010\"R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006#"}, d2 = {"Lcom/acare/clinic/agent/audit/AgentEventTracker;", "", "deviceIdProvider", "Lcom/acare/clinic/agent/device/DeviceIdProvider;", "dlpScanner", "Lcom/acare/clinic/agent/dlp/DlpScanner;", "queueRepository", "Lcom/acare/clinic/agent/queue/EventQueueRepository;", "(Lcom/acare/clinic/agent/device/DeviceIdProvider;Lcom/acare/clinic/agent/dlp/DlpScanner;Lcom/acare/clinic/agent/queue/EventQueueRepository;)V", "enqueue", "", "userId", "", "eventType", "", "action", "severity", "violationType", "contentSnippet", "details", "", "(Ljava/lang/Long;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "trackCopy", "", "patientId", "copiedText", "(JJLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "trackExport", "exportText", "trackFormSubmit", "formName", "formText", "(JLjava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "trackViewPatientDetail", "(JJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class AgentEventTracker {
    @org.jetbrains.annotations.NotNull()
    private final com.acare.clinic.agent.device.DeviceIdProvider deviceIdProvider = null;
    @org.jetbrains.annotations.NotNull()
    private final com.acare.clinic.agent.dlp.DlpScanner dlpScanner = null;
    @org.jetbrains.annotations.NotNull()
    private final com.acare.clinic.agent.queue.EventQueueRepository queueRepository = null;
    
    public AgentEventTracker(@org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.device.DeviceIdProvider deviceIdProvider, @org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.dlp.DlpScanner dlpScanner, @org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.queue.EventQueueRepository queueRepository) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object trackViewPatientDetail(long userId, long patientId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object trackFormSubmit(long userId, @org.jetbrains.annotations.NotNull()
    java.lang.String formName, @org.jetbrains.annotations.NotNull()
    java.lang.String formText, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object trackCopy(long userId, long patientId, @org.jetbrains.annotations.NotNull()
    java.lang.String copiedText, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object trackExport(long userId, long patientId, @org.jetbrains.annotations.NotNull()
    java.lang.String exportText, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    private final java.lang.Object enqueue(java.lang.Long userId, java.lang.String eventType, java.lang.String action, java.lang.String severity, java.lang.String violationType, java.lang.String contentSnippet, java.util.Map<java.lang.String, ? extends java.lang.Object> details, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}