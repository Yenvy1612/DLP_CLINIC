package com.acare.clinic.ui.doctor;

/**
 * DoctorScheduleFragment — Quản lý lịch khám của bác sĩ.
 * Tương đương Schedule.jsx trong Frontend web.
 *
 * Tab 0: Đang chờ (PENDING)
 * Tab 1: Tất cả lịch hẹn
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\b\u0010\u0015\u001a\u00020\u0012H\u0002J\b\u0010\u0016\u001a\u00020\u0012H\u0002J\u0010\u0010\u0017\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J$\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0016J\b\u0010 \u001a\u00020\u0012H\u0016J\u001a\u0010!\u001a\u00020\u00122\u0006\u0010\"\u001a\u00020\u00192\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0016J\b\u0010#\u001a\u00020\u0012H\u0002J\u0010\u0010$\u001a\u00020\u00122\u0006\u0010%\u001a\u00020&H\u0002J\b\u0010\'\u001a\u00020\u0012H\u0002J\u0010\u0010(\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\b\u0010)\u001a\u00020\u0012H\u0002J\u0018\u0010*\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010+\u001a\u00020\u0010H\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0005\u001a\u00020\u00068BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\u000b\u001a\u00020\u00048BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\rR\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006,"}, d2 = {"Lcom/acare/clinic/ui/doctor/DoctorScheduleFragment;", "Landroidx/fragment/app/Fragment;", "()V", "_binding", "Lcom/acare/clinic/databinding/FragmentDoctorScheduleBinding;", "api", "Lcom/acare/clinic/data/network/ApiService;", "getApi", "()Lcom/acare/clinic/data/network/ApiService;", "api$delegate", "Lkotlin/Lazy;", "binding", "getBinding", "()Lcom/acare/clinic/databinding/FragmentDoctorScheduleBinding;", "cancelReasonOptions", "", "", "confirmCancel", "", "appointmentId", "", "loadAll", "loadPending", "markDone", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onDestroyView", "onViewCreated", "view", "reloadCurrentTab", "setLoading", "loading", "", "setupTabs", "showCustomCancelReasonDialog", "showEmpty", "submitCancel", "reason", "app_debug"})
public final class DoctorScheduleFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.Nullable()
    private com.acare.clinic.databinding.FragmentDoctorScheduleBinding _binding;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy api$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<java.lang.String> cancelReasonOptions = null;
    
    public DoctorScheduleFragment() {
        super();
    }
    
    private final com.acare.clinic.databinding.FragmentDoctorScheduleBinding getBinding() {
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
    
    private final void setupTabs() {
    }
    
    private final void loadPending() {
    }
    
    private final void loadAll() {
    }
    
    private final void markDone(long appointmentId) {
    }
    
    private final void confirmCancel(long appointmentId) {
    }
    
    private final void showCustomCancelReasonDialog(long appointmentId) {
    }
    
    private final void submitCancel(long appointmentId, java.lang.String reason) {
    }
    
    private final void reloadCurrentTab() {
    }
    
    private final void showEmpty() {
    }
    
    private final void setLoading(boolean loading) {
    }
    
    @java.lang.Override()
    public void onDestroyView() {
    }
}