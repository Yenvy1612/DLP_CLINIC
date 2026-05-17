package com.acare.clinic.ui.admin;

/**
 * AdminServicesFragment — Quản lý dịch vụ (Admin).
 * Tương đương Services.jsx trong Frontend web.
 * Gọi: GET /api/services, POST /api/services, PUT /api/services/{id}, DELETE /api/services/{id}
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013H\u0002J\u0016\u0010\u0014\u001a\u00020\u00112\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00130\u0016H\u0002J\b\u0010\u0017\u001a\u00020\u0011H\u0002J$\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0016J\b\u0010 \u001a\u00020\u0011H\u0016J\u001a\u0010!\u001a\u00020\u00112\u0006\u0010\"\u001a\u00020\u00192\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0016J\u0010\u0010#\u001a\u00020\u00112\u0006\u0010$\u001a\u00020%H\u0002J\u0010\u0010&\u001a\u00020\u00112\u0006\u0010\'\u001a\u00020(H\u0002J\b\u0010)\u001a\u00020\u0011H\u0002J\u000e\u0010*\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0013R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0005\u001a\u00020\u00068BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\u000b\u001a\u00020\u00048BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\rR\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006+"}, d2 = {"Lcom/acare/clinic/ui/admin/AdminServicesFragment;", "Landroidx/fragment/app/Fragment;", "()V", "_binding", "Lcom/acare/clinic/databinding/FragmentAdminServicesBinding;", "api", "Lcom/acare/clinic/data/network/ApiService;", "getApi", "()Lcom/acare/clinic/data/network/ApiService;", "api$delegate", "Lkotlin/Lazy;", "binding", "getBinding", "()Lcom/acare/clinic/databinding/FragmentAdminServicesBinding;", "searchJob", "Lkotlinx/coroutines/Job;", "confirmDeleteService", "", "service", "Lcom/acare/clinic/data/model/ClinicService;", "displayServices", "services", "", "loadServices", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onDestroyView", "onViewCreated", "view", "searchServices", "keyword", "", "setLoading", "loading", "", "showAddServiceDialog", "showEditServiceDialog", "app_debug"})
public final class AdminServicesFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.Nullable()
    private com.acare.clinic.databinding.FragmentAdminServicesBinding _binding;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy api$delegate = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job searchJob;
    
    public AdminServicesFragment() {
        super();
    }
    
    private final com.acare.clinic.databinding.FragmentAdminServicesBinding getBinding() {
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
    
    private final void showAddServiceDialog() {
    }
    
    public final void showEditServiceDialog(@org.jetbrains.annotations.NotNull()
    com.acare.clinic.data.model.ClinicService service) {
    }
    
    private final void confirmDeleteService(com.acare.clinic.data.model.ClinicService service) {
    }
    
    private final void loadServices() {
    }
    
    private final void searchServices(java.lang.String keyword) {
    }
    
    private final void displayServices(java.util.List<com.acare.clinic.data.model.ClinicService> services) {
    }
    
    private final void setLoading(boolean loading) {
    }
    
    @java.lang.Override()
    public void onDestroyView() {
    }
}