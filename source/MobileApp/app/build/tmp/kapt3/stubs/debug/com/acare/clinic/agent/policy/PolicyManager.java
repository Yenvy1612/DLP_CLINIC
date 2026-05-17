package com.acare.clinic.agent.policy;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\t\u001a\u00020\bH\u0002J\u0006\u0010\n\u001a\u00020\bJ\u000e\u0010\u000b\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/acare/clinic/agent/policy/PolicyManager;", "", "api", "Lcom/acare/clinic/agent/network/AgentApiService;", "preferences", "Lcom/acare/clinic/agent/storage/AgentPreferences;", "(Lcom/acare/clinic/agent/network/AgentApiService;Lcom/acare/clinic/agent/storage/AgentPreferences;)V", "cachedPolicy", "Lcom/acare/clinic/agent/policy/AgentPolicyResponse;", "defaultPolicy", "getCachedPolicyOrDefault", "syncPolicy", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class PolicyManager {
    @org.jetbrains.annotations.NotNull()
    private final com.acare.clinic.agent.network.AgentApiService api = null;
    @org.jetbrains.annotations.NotNull()
    private final com.acare.clinic.agent.storage.AgentPreferences preferences = null;
    @org.jetbrains.annotations.Nullable()
    private com.acare.clinic.agent.policy.AgentPolicyResponse cachedPolicy;
    
    public PolicyManager(@org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.network.AgentApiService api, @org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.storage.AgentPreferences preferences) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object syncPolicy(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.acare.clinic.agent.policy.AgentPolicyResponse> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.acare.clinic.agent.policy.AgentPolicyResponse getCachedPolicyOrDefault() {
        return null;
    }
    
    private final com.acare.clinic.agent.policy.AgentPolicyResponse defaultPolicy() {
        return null;
    }
}