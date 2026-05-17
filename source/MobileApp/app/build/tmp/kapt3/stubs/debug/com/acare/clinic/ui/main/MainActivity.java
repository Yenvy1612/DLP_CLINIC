package com.acare.clinic.ui.main;

/**
 * MainActivity — shell chứa Bottom Navigation + NavHostFragment.
 *
 * Phân quyền theo role (giống Frontend web):
 * - PATIENT : Tab Trang chủ | Lịch hẹn | Hồ sơ bệnh | Tôi  (nav_graph_patient)
 * - DOCTOR  : Tab Trang chủ | Lịch khám | Thống kê | Tôi   (nav_graph_doctor)
 * - ADMIN   : Tab Tổng quan | Người dùng | Dịch vụ | Lịch hẹn | Bảo mật (nav_graph_admin)
 *
 * Anti-crash: Dùng setReorderingAllowed + debounce navigation
 * để tránh crash khi chuyển tab quá nhanh.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0007\u001a\u00020\bJ\u0012\u0010\t\u001a\u00020\b2\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0014J\b\u0010\f\u001a\u00020\bH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lcom/acare/clinic/ui/main/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/acare/clinic/databinding/ActivityMainBinding;", "navController", "Landroidx/navigation/NavController;", "logout", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "setupRoleBasedNavigation", "app_debug"})
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.acare.clinic.databinding.ActivityMainBinding binding;
    private androidx.navigation.NavController navController;
    
    public MainActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupRoleBasedNavigation() {
    }
    
    public final void logout() {
    }
}