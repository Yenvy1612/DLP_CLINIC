package com.acare.clinic.ui.admin;

/**
 * AdminUsersFragment — Quản lý người dùng (Admin).
 * Tương đương Users.jsx trong Frontend web.
 * Gọi: GET /api/users, GET /api/users/search
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0016\u0010\u0017\u001a\u00020\u00142\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00160\u0011H\u0002J\b\u0010\u0019\u001a\u00020\u0014H\u0002J\b\u0010\u001a\u001a\u00020\u0014H\u0002J$\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010 2\b\u0010!\u001a\u0004\u0018\u00010\"H\u0016J\b\u0010#\u001a\u00020\u0014H\u0016J\u001a\u0010$\u001a\u00020\u00142\u0006\u0010%\u001a\u00020\u001c2\b\u0010!\u001a\u0004\u0018\u00010\"H\u0016J\u0010\u0010&\u001a\u00020\u00142\u0006\u0010\'\u001a\u00020(H\u0002J\u0010\u0010)\u001a\u00020\u00142\u0006\u0010*\u001a\u00020+H\u0002J\b\u0010,\u001a\u00020\u0014H\u0002J\u000e\u0010-\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0005\u001a\u00020\u00068BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\u000b\u001a\u00020\u00048BX\u0082\u0004\u00a2\u0006\u0006\u001a\u0004\b\f\u0010\rR\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006."}, d2 = {"Lcom/acare/clinic/ui/admin/AdminUsersFragment;", "Landroidx/fragment/app/Fragment;", "()V", "_binding", "Lcom/acare/clinic/databinding/FragmentAdminUsersBinding;", "api", "Lcom/acare/clinic/data/network/ApiService;", "getApi", "()Lcom/acare/clinic/data/network/ApiService;", "api$delegate", "Lkotlin/Lazy;", "binding", "getBinding", "()Lcom/acare/clinic/databinding/FragmentAdminUsersBinding;", "searchJob", "Lkotlinx/coroutines/Job;", "specialties", "", "Lcom/acare/clinic/data/model/Specialty;", "confirmDeleteUser", "", "user", "Lcom/acare/clinic/data/model/UserProfile;", "displayUsers", "users", "loadSpecialties", "loadUsers", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onDestroyView", "onViewCreated", "view", "searchUsers", "keyword", "", "setLoading", "loading", "", "showAddUserDialog", "showEditUserDialog", "app_debug"})
public final class AdminUsersFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.Nullable()
    private com.acare.clinic.databinding.FragmentAdminUsersBinding _binding;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy api$delegate = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job searchJob;
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.acare.clinic.data.model.Specialty> specialties;
    
    public AdminUsersFragment() {
        super();
    }
    
    private final com.acare.clinic.databinding.FragmentAdminUsersBinding getBinding() {
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
    
    private final void loadSpecialties() {
    }
    
    private final void showAddUserDialog() {
    }
    
    private final void loadUsers() {
    }
    
    private final void searchUsers(java.lang.String keyword) {
    }
    
    private final void displayUsers(java.util.List<com.acare.clinic.data.model.UserProfile> users) {
    }
    
    public final void showEditUserDialog(@org.jetbrains.annotations.NotNull()
    com.acare.clinic.data.model.UserProfile user) {
    }
    
    private final void confirmDeleteUser(com.acare.clinic.data.model.UserProfile user) {
    }
    
    private final void setLoading(boolean loading) {
    }
    
    @java.lang.Override()
    public void onDestroyView() {
    }
}