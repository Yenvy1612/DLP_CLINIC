package com.acare.clinic.data.model;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BW\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\u0016\b\u0002\u0010\u0007\u001a\u0010\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u0003\u0018\u00010\b\u0012\u0010\b\u0002\u0010\n\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u000b\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0018\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\u0017\u0010\u001b\u001a\u0010\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u0003\u0018\u00010\bH\u00c6\u0003J\u0011\u0010\u001c\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u000bH\u00c6\u0003J[\u0010\u001d\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\u0016\b\u0002\u0010\u0007\u001a\u0010\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u0003\u0018\u00010\b2\u0010\b\u0002\u0010\n\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u000bH\u00c6\u0001J\u0013\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010!\u001a\u00020\"H\u00d6\u0001J\t\u0010#\u001a\u00020\tH\u00d6\u0001R\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u001e\u0010\n\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u000b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000fR$\u0010\u0007\u001a\u0010\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u0003\u0018\u00010\b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u000f\u00a8\u0006$"}, d2 = {"Lcom/acare/clinic/data/model/SecurityDashboardResponse;", "", "totalEvents24h", "", "criticalEvents24h", "highEvents24h", "revokedSessions24h", "topEventTypes", "", "", "recentCriticalEvents", "", "Lcom/acare/clinic/data/model/SecurityEventResponse;", "(JJJJLjava/util/Map;Ljava/util/List;)V", "getCriticalEvents24h", "()J", "getHighEvents24h", "getRecentCriticalEvents", "()Ljava/util/List;", "getRevokedSessions24h", "getTopEventTypes", "()Ljava/util/Map;", "getTotalEvents24h", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
public final class SecurityDashboardResponse {
    @com.google.gson.annotations.SerializedName(value = "totalEvents24h")
    private final long totalEvents24h = 0L;
    @com.google.gson.annotations.SerializedName(value = "criticalEvents24h")
    private final long criticalEvents24h = 0L;
    @com.google.gson.annotations.SerializedName(value = "highEvents24h")
    private final long highEvents24h = 0L;
    @com.google.gson.annotations.SerializedName(value = "revokedSessions24h")
    private final long revokedSessions24h = 0L;
    @com.google.gson.annotations.SerializedName(value = "topEventTypes")
    @org.jetbrains.annotations.Nullable()
    private final java.util.Map<java.lang.String, java.lang.Long> topEventTypes = null;
    @com.google.gson.annotations.SerializedName(value = "recentCriticalEvents")
    @org.jetbrains.annotations.Nullable()
    private final java.util.List<com.acare.clinic.data.model.SecurityEventResponse> recentCriticalEvents = null;
    
    public SecurityDashboardResponse(long totalEvents24h, long criticalEvents24h, long highEvents24h, long revokedSessions24h, @org.jetbrains.annotations.Nullable()
    java.util.Map<java.lang.String, java.lang.Long> topEventTypes, @org.jetbrains.annotations.Nullable()
    java.util.List<com.acare.clinic.data.model.SecurityEventResponse> recentCriticalEvents) {
        super();
    }
    
    public final long getTotalEvents24h() {
        return 0L;
    }
    
    public final long getCriticalEvents24h() {
        return 0L;
    }
    
    public final long getHighEvents24h() {
        return 0L;
    }
    
    public final long getRevokedSessions24h() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Map<java.lang.String, java.lang.Long> getTopEventTypes() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.acare.clinic.data.model.SecurityEventResponse> getRecentCriticalEvents() {
        return null;
    }
    
    public SecurityDashboardResponse() {
        super();
    }
    
    public final long component1() {
        return 0L;
    }
    
    public final long component2() {
        return 0L;
    }
    
    public final long component3() {
        return 0L;
    }
    
    public final long component4() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.Map<java.lang.String, java.lang.Long> component5() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.util.List<com.acare.clinic.data.model.SecurityEventResponse> component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.acare.clinic.data.model.SecurityDashboardResponse copy(long totalEvents24h, long criticalEvents24h, long highEvents24h, long revokedSessions24h, @org.jetbrains.annotations.Nullable()
    java.util.Map<java.lang.String, java.lang.Long> topEventTypes, @org.jetbrains.annotations.Nullable()
    java.util.List<com.acare.clinic.data.model.SecurityEventResponse> recentCriticalEvents) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}