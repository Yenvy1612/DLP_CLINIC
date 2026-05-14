package com.acare.clinic.data.network

import com.acare.clinic.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ── Auth ──────────────────────────────────────────────────────────────────

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<Any>>

    @POST("api/auth/refresh")
    suspend fun refreshToken(): Response<AuthResponse>

    @GET("api/auth/me")
    suspend fun getMe(): Response<UserProfile>

    @PUT("api/auth/me")
    suspend fun updateMe(@Body request: UpdateProfileRequest): Response<UserProfile>

    @PUT("api/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse<Any>>

    @POST("api/auth/logout")
    suspend fun logout(): Response<ApiResponse<Any>>

    // ── Specialties ───────────────────────────────────────────────────────────

    @GET("api/specialties")
    suspend fun getSpecialties(): Response<ApiResponse<List<Specialty>>>

    // ── Services ──────────────────────────────────────────────────────────────

    @GET("api/services")
    suspend fun getServices(): Response<ApiResponse<List<ClinicService>>>

    @GET("api/services/search")
    suspend fun searchServices(@Query("keyword") keyword: String): Response<ApiResponse<List<ClinicService>>>

    // ── Doctors ───────────────────────────────────────────────────────────────

    @GET("api/users/doctor")
    suspend fun getDoctors(): Response<ApiResponse<List<DoctorUser>>>

    @GET("api/appointments/doctors-by-service")
    suspend fun getDoctorsByService(@Query("serviceId") serviceId: Long): Response<ApiResponse<List<DoctorUser>>>

    // ── Appointments ──────────────────────────────────────────────────────────

    @POST("api/appointments/book")
    suspend fun bookAppointment(@Body request: BookAppointmentRequest): Response<ApiResponse<Appointment>>

    @GET("api/appointments/pending/patient/{patientId}")
    suspend fun getPendingAppointments(@Path("patientId") patientId: Long): Response<List<Appointment>>

    @GET("api/appointments/not-pending/patient/{patientId}")
    suspend fun getCompletedAppointments(@Path("patientId") patientId: Long): Response<List<Appointment>>

    @PATCH("api/appointments/cancel/{id}")
    suspend fun cancelAppointment(@Path("id") id: Long): Response<Void>

    @GET("api/appointments/availability")
    suspend fun checkAvailability(
        @Query("doctorId") doctorId: Long,
        @Query("date") date: String
    ): Response<ApiResponse<List<String>>>

    // ── Medical Records ───────────────────────────────────────────────────────

    @GET("api/medical-records/patient/{patientId}")
    suspend fun getMedicalRecords(@Path("patientId") patientId: Long): Response<List<MedicalRecord>>

    // ── Activities ────────────────────────────────────────────────────────────

    @GET("api/activities/recent/user/{userId}")
    suspend fun getUserActivities(@Path("userId") userId: Long): Response<List<Activity>>
}
