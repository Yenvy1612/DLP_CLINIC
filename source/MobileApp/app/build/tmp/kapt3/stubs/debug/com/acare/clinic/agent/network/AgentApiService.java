package com.acare.clinic.agent.network;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00a2\u0006\u0002\u0010\u0007J\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0003H\u00a7@\u00a2\u0006\u0002\u0010\nJ\u001e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\f\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u001e\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\b\b\u0001\u0010\f\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0011J(\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00060\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\f\u001a\u00020\u0013H\u00a7@\u00a2\u0006\u0002\u0010\u0014\u00a8\u0006\u0015"}, d2 = {"Lcom/acare/clinic/agent/network/AgentApiService;", "", "getAgentStatus", "Lcom/acare/clinic/agent/network/ApiResponse;", "Lcom/acare/clinic/agent/device/AgentStatusResponse;", "deviceId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getPolicy", "Lcom/acare/clinic/agent/policy/AgentPolicyResponse;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "registerAgent", "request", "Lcom/acare/clinic/agent/device/AgentRegisterRequest;", "(Lcom/acare/clinic/agent/device/AgentRegisterRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sendAgentEvent", "Lcom/acare/clinic/agent/audit/AgentEventRequest;", "(Lcom/acare/clinic/agent/audit/AgentEventRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sendHeartbeat", "Lcom/acare/clinic/agent/heartbeat/HeartbeatRequest;", "(Ljava/lang/String;Lcom/acare/clinic/agent/heartbeat/HeartbeatRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface AgentApiService {
    
    @retrofit2.http.POST(value = "api/agents/register")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object registerAgent(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.device.AgentRegisterRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.acare.clinic.agent.network.ApiResponse<com.acare.clinic.agent.device.AgentStatusResponse>> $completion);
    
    @retrofit2.http.POST(value = "api/agents/{deviceId}/heartbeat")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object sendHeartbeat(@retrofit2.http.Path(value = "deviceId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String deviceId, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.heartbeat.HeartbeatRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.acare.clinic.agent.network.ApiResponse<java.lang.String>> $completion);
    
    @retrofit2.http.GET(value = "api/agents/{deviceId}/status")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAgentStatus(@retrofit2.http.Path(value = "deviceId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String deviceId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.acare.clinic.agent.network.ApiResponse<com.acare.clinic.agent.device.AgentStatusResponse>> $completion);
    
    @retrofit2.http.GET(value = "api/agents/policy")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getPolicy(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.acare.clinic.agent.network.ApiResponse<com.acare.clinic.agent.policy.AgentPolicyResponse>> $completion);
    
    @retrofit2.http.POST(value = "api/agent-events")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object sendAgentEvent(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.audit.AgentEventRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.acare.clinic.agent.network.ApiResponse<java.lang.String>> $completion);
}