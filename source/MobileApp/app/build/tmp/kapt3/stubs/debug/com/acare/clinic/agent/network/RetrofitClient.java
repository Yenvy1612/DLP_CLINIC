package com.acare.clinic.agent.network;

/**
 * RetrofitClient cho Agent module.
 *
 * Hỗ trợ 2 chế độ:
 * - Cookie-based (dùng OkHttpClient có sẵn CookieJar từ NetworkClient)
 * - Bearer token (fallback nếu cần)
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bJ\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\n\u00a8\u0006\u000b"}, d2 = {"Lcom/acare/clinic/agent/network/RetrofitClient;", "", "()V", "create", "Lcom/acare/clinic/agent/network/AgentApiService;", "baseUrl", "", "tokenProvider", "Lcom/acare/clinic/agent/auth/TokenProvider;", "okHttpClient", "Lokhttp3/OkHttpClient;", "app_debug"})
public final class RetrofitClient {
    @org.jetbrains.annotations.NotNull()
    public static final com.acare.clinic.agent.network.RetrofitClient INSTANCE = null;
    
    private RetrofitClient() {
        super();
    }
    
    /**
     * Tạo AgentApiService dùng OkHttpClient có sẵn CookieJar.
     * Đây là cách ưu tiên vì app xác thực bằng HttpOnly cookie.
     */
    @org.jetbrains.annotations.NotNull()
    public final com.acare.clinic.agent.network.AgentApiService create(@org.jetbrains.annotations.NotNull()
    java.lang.String baseUrl, @org.jetbrains.annotations.NotNull()
    okhttp3.OkHttpClient okHttpClient) {
        return null;
    }
    
    /**
     * Tạo AgentApiService với Bearer token auth (legacy/fallback).
     */
    @org.jetbrains.annotations.NotNull()
    public final com.acare.clinic.agent.network.AgentApiService create(@org.jetbrains.annotations.NotNull()
    java.lang.String baseUrl, @org.jetbrains.annotations.NotNull()
    com.acare.clinic.agent.auth.TokenProvider tokenProvider) {
        return null;
    }
}