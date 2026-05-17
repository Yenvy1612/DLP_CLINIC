package com.acare.clinic.ui.doctor;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\t\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\f\u0012\b\u0012\u00060\u0002R\u00020\u00000\u0001:\u0001\u001eBq\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u0012\u0014\b\u0002\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007\u0012\u0016\b\u0002\u0010\n\u001a\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\f\u0018\u00010\u000b\u0012\u0016\b\u0002\u0010\r\u001a\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\f\u0018\u00010\u000b\u0012\u0016\b\u0002\u0010\u000e\u001a\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\f\u0018\u00010\u000b\u00a2\u0006\u0002\u0010\u000fJ\u0010\u0010\u0010\u001a\u00020\t2\u0006\u0010\u0011\u001a\u00020\tH\u0002J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\u0010\u0010\u0014\u001a\u00020\t2\u0006\u0010\u0015\u001a\u00020\tH\u0002J\u001c\u0010\u0016\u001a\u00020\f2\n\u0010\u0017\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u0018\u001a\u00020\u0013H\u0016J\u001c\u0010\u0019\u001a\u00060\u0002R\u00020\u00002\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0013H\u0016J\u0010\u0010\u001d\u001a\u00020\u00132\u0006\u0010\u0015\u001a\u00020\tH\u0002R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\r\u001a\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\f\u0018\u00010\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\n\u001a\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\f\u0018\u00010\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u000e\u001a\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\f\u0018\u00010\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001f"}, d2 = {"Lcom/acare/clinic/ui/doctor/DoctorAppointmentAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/acare/clinic/ui/doctor/DoctorAppointmentAdapter$VH;", "items", "", "Lcom/acare/clinic/data/model/Appointment;", "medicalRecordSummaryByAppointmentId", "", "", "", "onDone", "Lkotlin/Function1;", "", "onCancel", "onRecord", "(Ljava/util/List;Ljava/util/Map;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V", "formatTime", "raw", "getItemCount", "", "mapStatus", "status", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "statusColor", "VH", "app_debug"})
public final class DoctorAppointmentAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.acare.clinic.ui.doctor.DoctorAppointmentAdapter.VH> {
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<com.acare.clinic.data.model.Appointment> items = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.Long, java.lang.String> medicalRecordSummaryByAppointmentId = null;
    @org.jetbrains.annotations.Nullable()
    private final kotlin.jvm.functions.Function1<com.acare.clinic.data.model.Appointment, kotlin.Unit> onDone = null;
    @org.jetbrains.annotations.Nullable()
    private final kotlin.jvm.functions.Function1<com.acare.clinic.data.model.Appointment, kotlin.Unit> onCancel = null;
    @org.jetbrains.annotations.Nullable()
    private final kotlin.jvm.functions.Function1<com.acare.clinic.data.model.Appointment, kotlin.Unit> onRecord = null;
    
    public DoctorAppointmentAdapter(@org.jetbrains.annotations.NotNull()
    java.util.List<com.acare.clinic.data.model.Appointment> items, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.Long, java.lang.String> medicalRecordSummaryByAppointmentId, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super com.acare.clinic.data.model.Appointment, kotlin.Unit> onDone, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super com.acare.clinic.data.model.Appointment, kotlin.Unit> onCancel, @org.jetbrains.annotations.Nullable()
    kotlin.jvm.functions.Function1<? super com.acare.clinic.data.model.Appointment, kotlin.Unit> onRecord) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.acare.clinic.ui.doctor.DoctorAppointmentAdapter.VH onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.acare.clinic.ui.doctor.DoctorAppointmentAdapter.VH holder, int position) {
    }
    
    private final java.lang.String formatTime(java.lang.String raw) {
        return null;
    }
    
    private final java.lang.String mapStatus(java.lang.String status) {
        return null;
    }
    
    private final int statusColor(java.lang.String status) {
        return 0;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\bR\u0011\u0010\u000b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\bR\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0011\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0015\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0014R\u0011\u0010\u0017\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0014R\u0011\u0010\u0019\u001a\u00020\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0014\u00a8\u0006\u001b"}, d2 = {"Lcom/acare/clinic/ui/doctor/DoctorAppointmentAdapter$VH;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "view", "Landroid/view/View;", "(Lcom/acare/clinic/ui/doctor/DoctorAppointmentAdapter;Landroid/view/View;)V", "btnCancel", "Lcom/google/android/material/button/MaterialButton;", "getBtnCancel", "()Lcom/google/android/material/button/MaterialButton;", "btnDone", "getBtnDone", "btnRecord", "getBtnRecord", "chipStatus", "Lcom/google/android/material/chip/Chip;", "getChipStatus", "()Lcom/google/android/material/chip/Chip;", "tvMedicalRecord", "Landroid/widget/TextView;", "getTvMedicalRecord", "()Landroid/widget/TextView;", "tvPatientName", "getTvPatientName", "tvService", "getTvService", "tvTime", "getTvTime", "app_debug"})
    public final class VH extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView tvPatientName = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView tvService = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView tvMedicalRecord = null;
        @org.jetbrains.annotations.NotNull()
        private final android.widget.TextView tvTime = null;
        @org.jetbrains.annotations.NotNull()
        private final com.google.android.material.chip.Chip chipStatus = null;
        @org.jetbrains.annotations.NotNull()
        private final com.google.android.material.button.MaterialButton btnDone = null;
        @org.jetbrains.annotations.NotNull()
        private final com.google.android.material.button.MaterialButton btnCancel = null;
        @org.jetbrains.annotations.NotNull()
        private final com.google.android.material.button.MaterialButton btnRecord = null;
        
        public VH(@org.jetbrains.annotations.NotNull()
        android.view.View view) {
            super(null);
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getTvPatientName() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getTvService() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getTvMedicalRecord() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.widget.TextView getTvTime() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.google.android.material.chip.Chip getChipStatus() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.google.android.material.button.MaterialButton getBtnDone() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.google.android.material.button.MaterialButton getBtnCancel() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.google.android.material.button.MaterialButton getBtnRecord() {
            return null;
        }
    }
}