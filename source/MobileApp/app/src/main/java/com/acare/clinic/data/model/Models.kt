package com.acare.clinic.data.model

import com.google.gson.annotations.SerializedName

// ── Generic wrapper ───────────────────────────────────────────────────────────

data class ApiResponse<T>(
    val status: Int,
    val success: Boolean,
    val message: String,
    val data: T?
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
    val gender: String = "OTHER",
    val address: String = ""
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
    val fullName: String,
    val email: String,
    val phone: String?,
    val role: String,
    val gender: String?,
    val birthDate: String?,
    val address: String?
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
    val department: String,
    @SerializedName("specialtyId") val specialtyId: Long?
)

// ── Doctor ────────────────────────────────────────────────────────────────────

data class DoctorUser(
    val id: Long,
    val fullName: String,
    val email: String,
    val specialty: String?,
    val department: String?,
    @SerializedName("yearsExperience") val yearsExperience: Int?
)

// ── Appointment ───────────────────────────────────────────────────────────────

data class Appointment(
    val id: Long,
    @SerializedName("appointmentCode") val code: String,
    @SerializedName("patientId") val patientId: Long,
    @SerializedName("doctorId") val doctorId: Long,
    @SerializedName("doctorName") val doctorName: String?,
    @SerializedName("serviceName") val serviceName: String?,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("endTime") val endTime: String,
    val status: String,
    val reason: String?,
    val note: String?
)

data class BookAppointmentRequest(
    @SerializedName("patientId") val patientId: Long,
    @SerializedName("doctorId") val doctorId: Long,
    @SerializedName("serviceId") val serviceId: Long,
    @SerializedName("startTime") val startTime: String,
    val reason: String?
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

// ── Activity ──────────────────────────────────────────────────────────────────

data class Activity(
    val id: Long,
    val type: String,
    val message: String,
    val time: String
)
