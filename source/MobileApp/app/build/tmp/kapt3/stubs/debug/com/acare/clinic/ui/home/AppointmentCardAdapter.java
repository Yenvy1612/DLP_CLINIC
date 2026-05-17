package com.acare.clinic.ui.home;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0001:\u0001\u0016B+\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u0012\u0016\b\u0002\u0010\u0006\u001a\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\b\u0018\u00010\u0007\u00a2\u0006\u0002\u0010\tJ\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0002J\b\u0010\r\u001a\u00020\u000eH\u0016J\u001c\u0010\u000f\u001a\u00020\b2\n\u0010\u0010\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0011\u001a\u00020\u000eH\u0016J\u001c\u0010\u0012\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u000eH\u0016R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0006\u001a\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\b\u0018\u00010\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/acare/clinic/ui/home/AppointmentCardAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/acare/clinic/ui/home/AppointmentCardAdapter$VH;", "items", "", "Lcom/acare/clinic/data/model/Appointment;", "onCancel", "Lkotlin/Function1;", "", "(Ljava/util/List;Lkotlin/jvm/functions/Function1;)V", "formatDateTime", "", "raw", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "VH", "app_debug"})
public final class AppointmentCardAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.acare.clinic.ui.home.AppointmentCardAdapter.VH> {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.acare.clinic.data.model.Appointment> items = null;
    @org.jetbrains.annotations.Nullable()
    private final kotlin.jvm.functions.Function1<com.acare.clinic.data.model.Appointment, kotlin.Unit> onCancel = null;
    
    public AppointmentCardAdapter(@org.jetbrains.annotations.NotNull()
    java.util.List<com.acare.clinic.data.model.Appointment> items, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super com.acare.clinic.data.model.Appointment, kotlin.Unit> onCancel) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.acare.clinic.ui.home.AppointmentCardAdapter.VH onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.acare.clinic.ui.home.AppointmentCardAdapter.VH holder, int position) {
    }
    
    private final java.lang.String formatDateTime(java.lang.String raw) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/acare/clinic/ui/home/AppointmentCardAdapter$VH;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "binding", "Lcom/acare/clinic/databinding/ItemAppointmentCardBinding;", "(Lcom/acare/clinic/ui/home/AppointmentCardAdapter;Lcom/acare/clinic/databinding/ItemAppointmentCardBinding;)V", "getBinding", "()Lcom/acare/clinic/databinding/ItemAppointmentCardBinding;", "app_debug"})
    public final class VH extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final com.acare.clinic.databinding.ItemAppointmentCardBinding binding = null;
        
        public VH(@org.jetbrains.annotations.NotNull()
        com.acare.clinic.databinding.ItemAppointmentCardBinding binding) {
            super(null);
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.acare.clinic.databinding.ItemAppointmentCardBinding getBinding() {
            return null;
        }
    }
}