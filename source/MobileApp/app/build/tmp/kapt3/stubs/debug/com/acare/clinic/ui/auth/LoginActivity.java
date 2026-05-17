package com.acare.clinic.ui.auth;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u000b\u001a\u00020\fH\u0002J\u001a\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u000eH\u0002J\u0012\u0010\u0012\u001a\u00020\f2\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0014J\u0010\u0010\u0015\u001a\u00020\f2\u0006\u0010\u0016\u001a\u00020\u000eH\u0002J\u0010\u0010\u0017\u001a\u00020\f2\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0010\u0010\u001a\u001a\u00020\f2\u0006\u0010\u001b\u001a\u00020\u000eH\u0002R\u001b\u0010\u0003\u001a\u00020\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u000e\u0010\t\u001a\u00020\nX\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/acare/clinic/ui/auth/LoginActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "api", "Lcom/acare/clinic/data/network/ApiService;", "getApi", "()Lcom/acare/clinic/data/network/ApiService;", "api$delegate", "Lkotlin/Lazy;", "binding", "Lcom/acare/clinic/databinding/ActivityLoginBinding;", "doLogin", "", "extractLoginError", "", "statusCode", "", "rawErrorBody", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "registerAgentAfterLogin", "username", "setLoading", "loading", "", "showError", "msg", "app_debug"})
public final class LoginActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.acare.clinic.databinding.ActivityLoginBinding binding;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy api$delegate = null;
    
    public LoginActivity() {
        super();
    }
    
    private final com.acare.clinic.data.network.ApiService getApi() {
        return null;
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void doLogin() {
    }
    
    /**
     * Đăng ký agent device với backend sau khi login thành công.
     * Chạy non-blocking để không ảnh hưởng luồng login.
     */
    private final void registerAgentAfterLogin(java.lang.String username) {
    }
    
    private final void setLoading(boolean loading) {
    }
    
    private final void showError(java.lang.String msg) {
    }
    
    private final java.lang.String extractLoginError(int statusCode, java.lang.String rawErrorBody) {
        return null;
    }
}