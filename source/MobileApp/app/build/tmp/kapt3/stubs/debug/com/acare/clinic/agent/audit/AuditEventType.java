package com.acare.clinic.agent.audit;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/acare/clinic/agent/audit/AuditEventType;", "", "()V", "COPY_PATIENT_DATA", "", "EXPORT_ALLOWED", "EXPORT_BLOCKED", "FORM_DLP_MATCHED", "LOGIN_FAILED", "LOGIN_SUCCESS", "VIEW_PATIENT_DETAIL", "VIEW_PATIENT_LIST", "app_debug"})
public final class AuditEventType {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String LOGIN_SUCCESS = "LOGIN_SUCCESS";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String LOGIN_FAILED = "LOGIN_FAILED";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String VIEW_PATIENT_LIST = "VIEW_PATIENT_LIST";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String VIEW_PATIENT_DETAIL = "VIEW_PATIENT_DETAIL";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String FORM_DLP_MATCHED = "FORM_DLP_MATCHED";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String COPY_PATIENT_DATA = "COPY_PATIENT_DATA";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String EXPORT_BLOCKED = "EXPORT_BLOCKED";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String EXPORT_ALLOWED = "EXPORT_ALLOWED";
    @org.jetbrains.annotations.NotNull()
    public static final com.acare.clinic.agent.audit.AuditEventType INSTANCE = null;
    
    private AuditEventType() {
        super();
    }
}