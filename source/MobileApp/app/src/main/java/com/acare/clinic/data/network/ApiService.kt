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
    suspend fun getSpecialties(): Response<List<Specialty>>

    // ── Services (web FE: serviceService.getAll() → GET /services) ────────────

    @GET("api/services")
    suspend fun getServices(): Response<List<ClinicService>>

    @GET("api/services/{id}")
    suspend fun getServiceById(@Path("id") id: Long): Response<ClinicService>

    @GET("api/services/search")
    suspend fun searchServices(@Query("keyword") keyword: String): Response<List<ClinicService>>

    // ── Doctors (web FE: userService.getDoctors()) ────────────────────────────

    @GET("api/users/doctor")
    suspend fun getDoctors(): Response<List<DoctorUser>>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<DoctorUser>

    // ── Appointments (match web FE appointmentService exactly) ────────────────

    /**
     * Web FE: appointmentService.getDoctorsByService(serviceId)
     * → GET /appointments/doctors-by-service?serviceId=
     */
    @GET("api/appointments/doctors-by-service")
    suspend fun getDoctorsByService(@Query("serviceId") serviceId: Long): Response<List<DoctorUser>>

    /**
     * Web FE: appointmentService.getDoctorAvailability(doctorId, serviceId, date)
     * → GET /appointments/doctor-availability?doctorId=&serviceId=&date=
     */
    @GET("api/appointments/doctor-availability")
    suspend fun getDoctorAvailability(
        @Query("doctorId") doctorId: Long,
        @Query("serviceId") serviceId: Long,
        @Query("date") date: String,
        @Query("appointmentId") appointmentId: Long? = null
    ): Response<List<TimeSlot>>

    /**
     * Web FE: appointmentService.bookForPatient(appointment)
     * → POST /appointments (KHÔNG phải /appointments/book)
     */
    @POST("api/appointments")
    suspend fun bookAppointment(@Body request: BookAppointmentRequest): Response<Appointment>

    /**
     * Web FE: appointmentService.pendingByPatientId(id)
     * → GET /appointments?patientId=&pending=true
     */
    @GET("api/appointments")
    suspend fun getPendingAppointments(
        @Query("patientId") patientId: Long,
        @Query("pending") pending: Boolean = true
    ): Response<List<Appointment>>

    /**
     * Web FE: appointmentService.historyByPatientId(id, {page, size, sort})
     * → GET /appointments/history/patient/{id}?page=&size=&sort=
     */
    @GET("api/appointments/history/patient/{id}")
    suspend fun getAppointmentHistory(
        @Path("id") patientId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "startTime,desc"
    ): Response<PageResponse<Appointment>>

    /**
     * Web FE: appointmentService.remove(id, cancelledBy, cancelReason)
     * → DELETE /appointments/{id}?cancelledBy=&cancelReason=
     */
    @DELETE("api/appointments/{id}")
    suspend fun cancelAppointment(
        @Path("id") id: Long,
        @Query("cancelledBy") cancelledBy: String = "PATIENT",
        @Query("cancelReason") cancelReason: String = ""
    ): Response<Void>

    // ── Medical Records ───────────────────────────────────────────────────────

    @GET("api/medical-records/patient/{patientId}")
    suspend fun getMedicalRecords(@Path("patientId") patientId: Long): Response<List<MedicalRecord>>

    // ── Activities ────────────────────────────────────────────────────────────

    @GET("api/activities/recent/user/{userId}")
    suspend fun getUserActivities(@Path("userId") userId: Long): Response<List<Activity>>
}
