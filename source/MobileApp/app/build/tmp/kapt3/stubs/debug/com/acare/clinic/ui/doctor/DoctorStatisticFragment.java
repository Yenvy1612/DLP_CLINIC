package com.acare.clinic.ui.doctor;

/**
 * DoctorStatisticFragment — Thống kê của bác sĩ.
 * Tương đương Statistic.jsx trong Frontend web.
 * Gọi: GET /api/doctor/statistics/dashboard
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0012H\u0002J\u0012\u0010\u0014\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0012H\u0002J\u001c\u0010\u0015\u001a\u00020\u00122\b\u0010\u0016\u001a\u0004\u0018\u00010\u00122\b\u0010\u0017\u001a\u0004\u0018\u00010\u0012H\u0002J\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bH\u0002J\u000e\u0010\u001c\u001a\u00020\u0019H\u0082@\u00a2\u0006\u0002\u0010\u001dJ\b\u0010\u001e\u001a\u00020\u0019H\u0002J$\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010$2\b\u0010%\u001a\u0004\u0018\u00010&H\u0016J\b\u0010\'\u001a\u00020\u0019H\u0016J\u001a\u0010(\u001a\u00020\u00192\u0006\u0010)\u001a\u00020 2\b\u0010%\u001a\u0004\u0018\u00010&H\u0016J\u0016\u0010*\u001a\u00020\u00192\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u001b0,H\u0002J\u0010\u0010-\u001a\u00020\u00192\u0006\u0010.\u001a\u00020/H\u0002J\b\u00100\u001a\u00020\u0019H\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0005\u001a\u00020\u00068BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\u000b\u001a\u00020\u00048BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\rR\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u00061"}, d2 = {"Lcom/acare/clinic/ui/doctor/DoctorStatisticFragment;", "Landroidx/fragment/app/Fragment;", "()V", "_binding", "Lcom/acare/clinic/databinding/FragmentDoctorStatisticBinding;", "api", "Lcom/acare/clinic/data/network/ApiService;", "getApi", "()Lcom/acare/clinic/data/network/ApiService;", "api$delegate", "Lkotlin/Lazy;", "binding", "getBinding", "()Lcom/acare/clinic/databinding/FragmentDoctorStatisticBinding;", "currentMonth", "", "currentYear", "formatDate", "", "raw", "formatDateTime", "formatRange", "fromDate", "toDate", "loadPatientHistory", "", "row", "Lcom/acare/clinic/data/model/DoctorPatientSummaryRow;", "loadPendingCount", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "loadStatistics", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onDestroyView", "onViewCreated", "view", "renderPatientRows", "rows", "", "setLoading", "loading", "", "showOfflineData", "app_debug"})
public final class DoctorStatisticFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.Nullable()
    private com.acare.clinic.databinding.FragmentDoctorStatisticBinding _binding;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy api$delegate = null;
    private int currentMonth;
    private int currentYear;
    
    public DoctorStatisticFragment() {
        super();
    }
    
    private final com.acare.clinic.databinding.FragmentDoctorStatisticBinding getBinding() {
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
    
    private final void loadStatistics() {
    }
    
    private final java.lang.Object loadPendingCount(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final void renderPatientRows(java.util.List<com.acare.clinic.data.model.DoctorPatientSummaryRow> rows) {
    }
    
    private final void loadPatientHistory(com.acare.clinic.data.model.DoctorPatientSummaryRow row) {
    }
    
    private final java.lang.String formatRange(java.lang.String fromDate, java.lang.String toDate) {
        return null;
    }
    
    private final java.lang.String formatDate(java.lang.String raw) {
        return null;
    }
    
    private final java.lang.String formatDateTime(java.lang.String raw) {
        return null;
    }
    
    /**
     * Hiển thị số 0 khi không có kết nối
     */
    private final void showOfflineData() {
    }
    
    private final void setLoading(boolean loading) {
    }
    
    @java.lang.Override()
    public void onDestroyView() {
    }
}