package com.acare.clinic.data.network;

/**
 * NetworkClient — singleton quản lý OkHttp + Retrofit.
 *
 * CookieJar tự động lưu và gửi cookie (access_token, refresh_token)
 * từ backend, giải quyết vấn đề HttpOnly cookie trên mobile.
 *
 * Lưu cookie trong memory (sẽ mất khi app bị kill).
 * Để persist: thay ConcurrentHashMap bằng EncryptedSharedPreferences.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\n\u001a\u00020\u000bJ\u001f\u0010\f\u001a\u0002H\r\"\u0004\b\u0000\u0010\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u0002H\r0\u000f\u00a2\u0006\u0002\u0010\u0010J\u000e\u0010\u0011\u001a\u00020\u000b2\u0006\u0010\u0012\u001a\u00020\u0013R \u0010\u0003\u001a\u0014\u0012\u0004\u0012\u00020\u0005\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00060\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/acare/clinic/data/network/NetworkClient;", "", "()V", "cookieStore", "Ljava/util/concurrent/ConcurrentHashMap;", "", "", "Lokhttp3/Cookie;", "retrofit", "Lretrofit2/Retrofit;", "clearCookies", "", "create", "T", "service", "Ljava/lang/Class;", "(Ljava/lang/Class;)Ljava/lang/Object;", "init", "context", "Landroid/content/Context;", "app_debug"})
public final class NetworkClient {
    private static retrofit2.Retrofit retrofit;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.concurrent.ConcurrentHashMap<java.lang.String, java.util.List<okhttp3.Cookie>> cookieStore = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.acare.clinic.data.network.NetworkClient INSTANCE = null;
    
    private NetworkClient() {
        super();
    }
    
    public final void init(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    public final <T extends java.lang.Object>T create(@org.jetbrains.annotations.NotNull()
    java.lang.Class<T> service) {
        return null;
    }
    
    /**
     * Xóa toàn bộ cookie (dùng khi logout)
     */
    public final void clearCookies() {
    }
}