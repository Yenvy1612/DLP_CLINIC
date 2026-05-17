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
    val paymentMethod: String = "COUNTER"
)

// ── Medical Record ────────────────────────────────────────────────────────────

data class MedicalRecord(
    val id: Long,
    @SerializedName("recordCode") val recordCode: String,
    @SerializedName("doctorName") val doctorName: String?,
    @SerializedName("chiefComplaint") val chiefComplaint: String?,
    val diagnosis: String?,
    @SerializedName("treatmentPlan") val treatmentPlan: String?,
    @SerializedName("createdAt") val createdAt: String
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
