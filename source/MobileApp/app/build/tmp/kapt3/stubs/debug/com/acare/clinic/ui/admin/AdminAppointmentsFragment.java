package com.acare.clinic.ui.admin;

/**
 * AdminAppointmentsFragment — Quản lý lịch hẹn (Admin).
 * Tương đương Appointments.jsx trong Frontend web.
 * Gọi: GET /api/appointments, GET /api/appointments?status=...
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0015\u001a\u00020\u0016H\u0002J\u0016\u0010\u0017\u001a\u00020\u00162\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u0002J\b\u0010\u0019\u001a\u00020\u0016H\u0002J$\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\b\u0010 \u001a\u0004\u0018\u00010!H\u0016J\b\u0010\"\u001a\u00020\u0016H\u0016J\u001a\u0010#\u001a\u00020\u00162\u0006\u0010$\u001a\u00020\u001b2\b\u0010 \u001a\u0004\u0018\u00010!H\u0016J\u0010\u0010%\u001a\u00020\u00162\u0006\u0010&\u001a\u00020\'H\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\b\u001a\u00020\t8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\f\u0010\r\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\u000e\u001a\u00020\u00048BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006("}, d2 = {"Lcom/acare/clinic/ui/admin/AdminAppointmentsFragment;", "Landroidx/fragment/app/Fragment;", "()V", "_binding", "Lcom/acare/clinic/databinding/FragmentAdminAppointmentsBinding;", "allAppointments", "", "Lcom/acare/clinic/data/model/Appointment;", "api", "Lcom/acare/clinic/data/network/ApiService;", "getApi", "()Lcom/acare/clinic/data/network/ApiService;", "api$delegate", "Lkotlin/Lazy;", "binding", "getBinding", "()Lcom/acare/clinic/databinding/FragmentAdminAppointmentsBinding;", "currentStatusFilter", "", "searchJob", "Lkotlinx/coroutines/Job;", "applyFilters", "", "displayAppointments", "appointments", "loadAppointments", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onDestroyView", "onViewCreated", "view", "setLoading", "loading", "", "app_debug"})
public final class AdminAppointmentsFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.Nullable()
    private com.acare.clinic.databinding.FragmentAdminAppointmentsBinding _binding;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy api$delegate = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job searchJob;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.acare.clinic.data.model.Appointment> allAppointments;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String currentStatusFilter;
    
    public AdminAppointmentsFragment() {
        super();
    }
    
    private final com.acare.clinic.databinding.FragmentAdminAppointmentsBinding getBinding() {
        return null;
    }
    
    private final com.acare.clinic.data.network.ApiService getApi() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull()
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable()
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override()
    public void onViewCreated(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void loadAppointments() {
    }
    
    private final void applyFilters() {
    }
    
    private final void displayAppointments(java.util.List<com.acare.clinic.data.model.Appointment> appointments) {
    }
    
    private final void setLoading(boolean loading) {
    }
    
    @java.lang.Override()
    public void onDestroyView() {
    }
}