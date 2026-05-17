package com.acare.clinic.data.model

import com.google.gson.annotations.SerializedName

// ── Generic wrapper ───────────────────────────────────────────────────────────

data class ApiResponse<T>(
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: T?
)

/** Paginated response (giống web FE historyByPatientId) */
data class PageResponse<T>(
    val content: List<T>,
    val totalPages: Int,
    val totalElements: Long,
    val number: Int,  // current page
    val size: Int
)

// ── Auth ──────────────────────────────────────────────────────────────────────

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    @SerializedName("fullName") val fullName: String,
    val email: String,
    val phone: String,
    val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String,
    val role: String = "PATIENT",
    val gender: String = "OTHER",
    val birthDate: String? = null,
    val address: String = "",
    val idNumber: String? = null,
    val specialtyId: Long? = null,
    val clinicLocation: String? = null,
    val workingDays: String? = null,
    val shiftStart: String? = null,
    val shiftEnd: String? = null
)

data class AuthResponse(
    val id: Long,
    val username: String,
    val roles: List<String>,
    @SerializedName("originalRole") val role: String,
    @SerializedName("tokenType") val tokenType: String,
    @SerializedName("expiresInSeconds") val expiresInSeconds: Long
)

data class UserProfile(
    val id: Long,
    val fullName: String?,
    val email: String?,
    val phone: String?,
    val role: String?,
    val gender: String?,
    val birthDate: String?,
    val address: String?,
    val idNumber: String?,
    val enabled: Boolean = true
)

data class AppointmentActionRequest(
    val status: String,
    val notes: String? = null
)

// Security Models
data class SecurityEventResponse(
    val id: Long,
    val userId: Long?,
    val eventType: String,
    val severity: String,
    val ipAddress: String?,
    val requestUri: String?,
    val httpMethod: String?,
    val description: String?,
    val riskScore: Int,
    val actionTaken: String?,
    val occurredAt: String
)

data class DlpLog(
    val id: Long,
    val deviceId: String?,
    val sourceType: String?,
    val platform: String?,
    val eventType: String?,
    val action: String?,
    val violationType: String?,
    val severity: String?,
    val details: String?,
    val contentSnippet: String?,
    val userId: Long?,
    val username: String?,
    val timestamp: String?
)

data class UpdateProfileRequest(
    val fullName: String?,
    val phone: String?,
    val gender: String?,
    val birthDate: String?,
    val address: String?
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)

// ── Doctor Profile ────────────────────────────────────────────────────────────

data class DoctorProfile(
    val id: Long? = null,
    val userId: Long? = null,
    val specialty: String?,
    val department: String?,
    val biography: String?,
    val clinicLocation: String?,
    val workingDays: String?,
    @SerializedName("yearsExperience") val yearsExperience: Int?
)

// ── Specialty / Service ───────────────────────────────────────────────────────

data class Specialty(
    val id: Long,
    val code: String,
    val name: String,
    val active: Boolean
)

data class ClinicService(
    val id: Long,
    val name: String,
    val price: Double,
    val description: String?,
    val department: String?,
    val active: Boolean = true,
    @SerializedName("specialtyId") val specialtyId: Long?
)

// ── Doctor ────────────────────────────────────────────────────────────────────

data class DoctorUser(
    val id: Long,
    val fullName: String?,
    val email: String?,
    val phone: String?,
    val specialty: String?,
    val department: String?,
    val biography: String?,
    val clinicLocation: String?,
    val workingDays: String?,
    @SerializedName("yearsExperience") val yearsExperience: Int?
)

// ── Appointment slot ──────────────────────────────────────────────────────────

data class TimeSlot(
    val time: String,
    val available: Boolean
)

// ── Appointment ───────────────────────────────────────────────────────────────

data class Appointment(
    val id: Long,
    @SerializedName("appointmentCode") val code: String?,
    @SerializedName("patientId") val patientId: Long,
    @SerializedName("patientName") val patientName: String?,
    @SerializedName("doctorId") val doctorId: Long,
    @SerializedName("serviceId") val serviceId: Long?,
    @SerializedName("doctorName") val doctorName: String?,
    @SerializedName("serviceName") val serviceName: String?,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String?,
    val status: String,
    val reason: String?,
    val note: String?,
    val paymentMethod: String?
)

/** Dùng để đặt lịch - POST /api/appointments */
data class BookAppointmentRequest(
    @SerializedName("patientId") val patientId: Long,
    @SerializedName("doctorId") val doctorId: Long,
    @SerializedName("serviceId") val serviceId: Long,
    @SerializedName("startTime") val startTime: String,
    val reason: String?,
    val note: String? = null
)

/** Dùng để cập nhật lịch - PUT /api/appointments/{id} */
data class AppointmentUpdateRequest(
    val patientId: Long,
    val doctorId: Long,
    val serviceId: Long,
    val startTime: String,
    val endTime: String? = null,
    val status: String? = null,
    val reason: String? = null,
    val note: String? = null
)

// ── Medical Record ────────────────────────────────────────────────────────────

data class MedicalRecord(
    val id: Long,
    @SerializedName("recordCode") val recordCode: String,
    @SerializedName("appointmentId") val appointmentId: Long? = null,
    @SerializedName("patientId") val patientId: Long? = null,
    @SerializedName("doctorId") val doctorId: Long? = null,
    @SerializedName("doctorName") val doctorName: String?,
    @SerializedName("chiefComplaint") val chiefComplaint: String?,
    val diagnosis: String?,
    @SerializedName("treatmentPlan") val treatmentPlan: String?,
    @SerializedName("clinicalNotes") val clinicalNotes: String? = null,
    @SerializedName("followUpDate") val followUpDate: String? = null,
    @SerializedName("patientFullName") val patientFullName: String? = null,
    @SerializedName("patientEmail") val patientEmail: String? = null,
    @SerializedName("patientPhone") val patientPhone: String? = null,
    @SerializedName("patientIdNumber") val patientIdNumber: String? = null,
    @SerializedName("bloodType") val bloodType: String? = null,
    @SerializedName("insuranceNumber") val insuranceNumber: String? = null,
    @SerializedName("allergies") val allergies: String? = null,
    @SerializedName("chronicConditions") val chronicConditions: String? = null,
    @SerializedName("emergencyContactName") val emergencyContactName: String? = null,
    @SerializedName("emergencyContactPhone") val emergencyContactPhone: String? = null,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

data class CreateMedicalRecordRequest(
    @SerializedName("appointmentId") val appointmentId: Long,
    @SerializedName("patientId") val patientId: Long? = null,
    @SerializedName("doctorId") val doctorId: Long? = null,
    @SerializedName("chiefComplaint") val chiefComplaint: String? = null,
    val diagnosis: String? = null,
    @SerializedName("treatmentPlan") val treatmentPlan: String? = null,
    @SerializedName("clinicalNotes") val clinicalNotes: String? = null,
    @SerializedName("followUpDate") val followUpDate: String? = null
)

// ── Activity (Notification) ───────────────────────────────────────────────────

data class Activity(
    val id: Long,
    val type: String,
    val message: String,
    val time: String
)

// ── Doctor Dashboard Statistics ───────────────────────────────────────────────

data class DoctorDashboard(
    @SerializedName("totalPatients") val totalPatients: Int = 0,
    @SerializedName("totalAppointments") val totalAppointments: Int = 0,
    @SerializedName("pendingAppointments") val pendingAppointments: Int = 0,
    @SerializedName("completedThisMonth") val completedThisMonth: Int = 0,
    @SerializedName("todayAppointments") val todayAppointments: Int = 0
)

// ── Doctor Statistics (New API) ─────────────────────────────────────────────

data class DoctorStatisticsDashboard(
    val periodType: String?,
    val fromDate: String?,
    val toDate: String?,
    val uniquePatientCount: Long = 0,
    val doneAppointmentCount: Long = 0,
    val dailyVisits: List<DoctorDailyVisitPoint>? = null,
    val patientRows: List<DoctorPatientSummaryRow>? = null
)

data class DoctorDailyVisitPoint(
    val date: String?,
    val doneAppointments: Long = 0
)

data class DoctorPatientSummaryRow(
    val patientId: Long,
    val patientName: String?,
    val doneAppointments: Long = 0,
    val services: List<String> = emptyList()
)

data class DoctorPatientAppointmentPage(
    val patientId: Long,
    val patientName: String?,
    val periodType: String?,
    val fromDate: String?,
    val toDate: String?,
    val page: Int,
    val size: Int,
    val totalPages: Int,
    val totalElements: Long,
    val items: List<DoctorPatientAppointmentItem> = emptyList()
)

data class DoctorPatientAppointmentItem(
    val appointmentId: Long,
    val appointmentCode: String?,
    val startTime: String?,
    val endTime: String?,
    val status: String?,
    val serviceName: String?,
    val reason: String?,
    val note: String?
)

// ── Admin Dashboard Summary ───────────────────────────────────────────────────

data class AdminSummary(
    val userCount: Long = 0,
    val topDoctorName: String? = null,
    val topDoctorDoneCount: Long = 0,
    val topServiceName: String? = null,
    val topServiceDoneCount: Long = 0,
    val monthRevenue: Double = 0.0,
    val month: Int = 0,
    val year: Int = 0
)

// ── Admin User Update ─────────────────────────────────────────────────────────

data class UpdateUserRequest(
    val fullName: String?,
    val email: String?,
    val phone: String?,
    val role: String?,
    val gender: String?,
    val birthDate: String?,
    val address: String?,
    val idNumber: String?,
    val enabled: Boolean? = null
)

// ── Admin Service CRUD ────────────────────────────────────────────────────────

data class CreateServiceRequest(
    val name: String,
    val price: Double,
    val description: String? = null,
    val department: String? = null,
    @SerializedName("specialtyId") val specialtyId: Long? = null,
    val active: Boolean = true
)

data class UpdateServiceRequest(
    val name: String?,
    val price: Double?,
    val description: String?,
    val department: String?,
    @SerializedName("specialtyId") val specialtyId: Long?,
    val active: Boolean? = null
)

// ── Security Dashboard ────────────────────────────────────────────────────────

data class SecurityDashboardResponse(
    @SerializedName("totalEvents24h") val totalEvents24h: Long = 0,
    @SerializedName("criticalEvents24h") val criticalEvents24h: Long = 0,
    @SerializedName("highEvents24h") val highEvents24h: Long = 0,
    @SerializedName("revokedSessions24h") val revokedSessions24h: Long = 0,
    @SerializedName("topEventTypes") val topEventTypes: Map<String, Long>? = null,
    @SerializedName("recentCriticalEvents") val recentCriticalEvents: List<SecurityEventResponse>? = null
)

// ── Agent Status (Admin view) ─────────────────────────────────────────────────

data class AgentStatusItem(
    val installed: Boolean = false,
    val trusted: Boolean = false,
    val deviceId: String?,
    val platform: String?,
    val status: String?,
    val message: String?
)
