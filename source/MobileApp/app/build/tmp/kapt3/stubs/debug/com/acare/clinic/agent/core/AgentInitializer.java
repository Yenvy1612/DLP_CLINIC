package com.acare.clinic.agent.core;

/**
 * AgentInitializer — điểm khởi tạo duy nhất cho Agent module.
 *
 * Sử dụng OkHttpClient có sẵn CookieJar từ NetworkClient
 * để gửi request agent với cùng session xác thực của user.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u000b\u001a\u00020\u0006J\u0006\u0010\f\u001a\u00020\bJ\u001e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014J\u0006\u0010\u0015\u001a\u00020\nR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/acare/clinic/agent/core/AgentInitializer;", "", "()V", "TAG", "", "agentManager", "Lcom/acare/clinic/agent/core/AgentManager;", "eventTracker", "Lcom/acare/clinic/agent/audit/AgentEventTracker;", "initialized", "", "getAgentManager", "getEventTracker", "init", "", "context", "Landroid/content/Context;", "config", "Lcom/acare/clinic/agent/core/AgentConfig;", "okHttpClient", "Lokhttp3/OkHttpClient;", "isInitialized", "app_debug"})
public final class AgentInitializer {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "AgentInitializer";
    private static com.acare.clinic.agent.core.AgentManager agentManager;
    private static com.acare.clinic.agent.audit.AgentEventTracker eventTracker;
    private static boolean initialized = false;
    @org.jetbrains.annotations.NotNull()
    public static final com.acare.clinic.agent.core.AgentInitializer INSTANCE = null;
    
    private AgentInitializer() {
        super();
    }
    
    /**
     * Khởi tạo Agent module dùng OkHttpClient có CookieJar (cookie-based auth).
     * Đây là cách ưu tiên vì app dùng HttpOnly cookie.
     */
    public final void init(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.core.AgentConfig config, @org.jetbrains.annotations.NotNull()
    okhttp3.OkHttpClient okHttpClient) {
    }
    
    public final boolean isInitialized() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.acare.clinic.agent.core.AgentManager getAgentManager() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.acare.clinic.agent.audit.AgentEventTracker getEventTracker() {
        return null;
    }
}