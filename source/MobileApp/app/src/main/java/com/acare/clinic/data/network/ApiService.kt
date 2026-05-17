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

    @GET("api/auth/me/doctor-profile")
    suspend fun getMyDoctorProfile(): Response<DoctorProfile>

    @PUT("api/auth/me/doctor-profile")
    suspend fun updateMyDoctorProfile(@Body request: DoctorProfile): Response<DoctorProfile>

    @PUT("api/auth/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse<Any>>

    @POST("api/auth/logout")
    suspend fun logout(): Response<ApiResponse<Any>>

    // ── Specialties ───────────────────────────────────────────────────────────

    @GET("api/specialties")
    suspend fun getSpecialties(): Response<List<Specialty>>

    // ── Services ──────────────────────────────────────────────────────────────

    @GET("api/services")
    suspend fun getServices(): Response<List<ClinicService>>

    @GET("api/services/{id}")
    suspend fun getServiceById(@Path("id") id: Long): Response<ClinicService>

    @GET("api/services/search")
    suspend fun searchServices(@Query("keyword") keyword: String): Response<List<ClinicService>>

    // ── Users (Admin) ─────────────────────────────────────────────────────────

    @GET("api/users")
    suspend fun getUsers(): Response<List<UserProfile>>

    @GET("api/users/search")
    suspend fun searchUsers(@Query("keyword") keyword: String): Response<List<UserProfile>>

    @POST("api/users")
    suspend fun createUser(@Body request: RegisterRequest): Response<ApiResponse<Any>>

    @GET("api/users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<UserProfile>

    @GET("api/users/doctor")
    suspend fun getDoctors(): Response<List<DoctorUser>>

    @GET("api/users/{id}/doctor-profile")
    suspend fun getDoctorProfile(@Path("id") id: Long): Response<DoctorProfile>

    // ── Appointments (PATIENT) ────────────────────────────────────────────────

    @GET("api/appointments/doctors-by-service")
    suspend fun getDoctorsByService(@Query("serviceId") serviceId: Long): Response<List<DoctorUser>>

    @GET("api/appointments/doctor-availability")
    suspend fun getDoctorAvailability(
        @Query("doctorId") doctorId: Long,
        @Query("serviceId") serviceId: Long,
        @Query("date") date: String,
        @Query("appointmentId") appointmentId: Long? = null
    ): Response<List<TimeSlot>>

    @POST("api/appointments")
    suspend fun bookAppointment(@Body request: BookAppointmentRequest): Response<Appointment>

    /** GET /appointments/pending/patient/{patientId} */
    @GET("api/appointments/pending/patient/{patientId}")
    suspend fun getPendingAppointmentsByPatient(
        @Path("patientId") patientId: Long
    ): Response<List<Appointment>>

    /** Compat fallback: GET /appointments?patientId=&pending=true... */
    @GET("api/appointments")
    suspend fun getAppointments(
        @Query("doctorId") doctorId: Long? = null,
        @Query("patientId") patientId: Long? = null,
        @Query("pending") pending: Boolean? = null,
        @Query("today") today: Boolean? = null,
        @Query("doneThisMonth") doneThisMonth: Boolean? = null
    ): Response<List<Appointment>>

    /** Legacy helper for existing code */
    @GET("api/appointments")
    suspend fun getPendingAppointments(
        @Query("patientId") patientId: Long,
        @Query("pending") pending: Boolean = true
    ): Response<List<Appointment>>

    /** GET /appointments/history/patient/{id} — có phân trang */
    @GET("api/appointments/history/patient/{id}")
    suspend fun getAppointmentHistory(
        @Path("id") patientId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "startTime,desc"
    ): Response<PageResponse<Appointment>>

    /** DELETE /appointments/{id} — bệnh nhân hủy */
    @DELETE("api/appointments/{id}")
    suspend fun cancelAppointment(
        @Path("id") id: Long,
        @Query("cancelledBy") cancelledBy: String = "PATIENT",
        @Query("cancelReason") cancelReason: String = ""
    ): Response<Void>

    // ── Appointments (DOCTOR) ─────────────────────────────────────────────────

    /** GET /appointments/pending/doctor/{doctorId} */
    @GET("api/appointments/pending/doctor/{doctorId}")
    suspend fun getPendingAppointmentsByDoctor(
        @Path("doctorId") doctorId: Long
    ): Response<List<Appointment>>

    /** GET /appointments/doctor/{doctorId} — có phân trang */
    @GET("api/appointments/doctor/{doctorId}")
    suspend fun getAppointmentsByDoctor(
        @Path("doctorId") doctorId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<Appointment>>

    /** PATCH /appointments/done/{id} — bác sĩ đánh dấu hoàn thành */
    @PATCH("api/appointments/done/{id}")
    suspend fun markAppointmentDone(@Path("id") id: Long): Response<Appointment>

    /** PATCH /appointments/cancel/{id} — bác sĩ hủy */
    @PATCH("api/appointments/cancel/{id}")
    suspend fun cancelAppointmentByDoctor(@Path("id") id: Long): Response<Appointment>

    /** PATCH /appointments/{id}/status */
    @PATCH("api/appointments/{id}/status")
    suspend fun updateAppointmentStatus(
        @Path("id") id: Long,
        @Body request: Map<String, String>
    ): Response<Appointment>

    // ── Doctor Statistics ─────────────────────────────────────────────────────

    /** GET /api/doctor/statistics/dashboard */
    @GET("api/doctor/statistics/dashboard")
    suspend fun getDoctorDashboard(): Response<DoctorDashboard>

    /** GET /api/doctor/statistics/patients/{patientId}/appointments */
    @GET("api/doctor/statistics/patients/{patientId}/appointments")
    suspend fun getPatientAppointmentsForDoctor(
        @Path("patientId") patientId: Long
    ): Response<List<Appointment>>

    // ── Admin Dashboard ───────────────────────────────────────────────────────

    /** GET /api/admin/dashboard/summary */
    @GET("api/admin/dashboard/summary")
    suspend fun getAdminSummary(): Response<AdminSummary>

    // ── Activities ────────────────────────────────────────────────────────────

    @GET("api/activities/recent/user/{userId}")
    suspend fun getUserActivities(@Path("userId") userId: Long): Response<List<Activity>>

    @GET("api/activities/recent/user/{userId}/count")
    suspend fun getUserActivitiesCount(@Path("userId") userId: Long): Response<Int>

    @DELETE("api/activities/recent/user/{userId}/{notificationId}")
    suspend fun deleteUserActivity(
        @Path("userId") userId: Long,
        @Path("notificationId") notificationId: Long
    ): Response<Void>

    // ── Medical Records ───────────────────────────────────────────────────────

    @GET("api/medical-records/patient/{patientId}")
    suspend fun getMedicalRecords(@Path("patientId") patientId: Long): Response<List<MedicalRecord>>

    // ── Security & DLP (Admin) ────────────────────────────────────────────────

    @GET("api/security/events")
    suspend fun getSecurityEvents(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50,
        @Query("severity") severity: String? = null
    ): Response<ApiResponse<PageResponse<SecurityEventResponse>>>

    @GET("api/security/events/user/{userId}")
    suspend fun getSecurityEventsByUser(
        @Path("userId") userId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PageResponse<SecurityEventResponse>>>

    @GET("api/security/dashboard")
    suspend fun getSecurityDashboard(): Response<ApiResponse<SecurityDashboardResponse>>

    @GET("api/dlp-logs")
    suspend fun getDlpLogs(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): Response<ApiResponse<PageResponse<DlpLog>>>

    // ── Admin User CRUD ───────────────────────────────────────────────────────

    @PUT("api/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body request: UpdateUserRequest
    ): Response<UserProfile>

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<ApiResponse<Any>>

    @PUT("api/users/{id}/doctor-profile")
    suspend fun updateDoctorProfileByAdmin(
        @Path("id") id: Long,
        @Body request: DoctorProfile
    ): Response<DoctorProfile>

    // ── Admin Service CRUD ────────────────────────────────────────────────────

    @POST("api/services")
    suspend fun createService(@Body request: CreateServiceRequest): Response<ApiResponse<Any>>

    @PUT("api/services/{id}")
    suspend fun updateService(
        @Path("id") id: Long,
        @Body request: UpdateServiceRequest
    ): Response<ApiResponse<Any>>

    @DELETE("api/services/{id}")
    suspend fun deleteService(@Path("id") id: Long): Response<ApiResponse<Any>>

    // ── Admin Appointment Management ──────────────────────────────────────────

    @GET("api/appointments")
    suspend fun getAllAppointments(): Response<List<Appointment>>

    @GET("api/appointments/filter")
    suspend fun filterAppointments(
        @Query("doctorName") doctorName: String? = null,
        @Query("patientName") patientName: String? = null,
        @Query("appointmentDate") appointmentDate: String? = null,
        @Query("status") status: String? = null
    ): Response<List<Appointment>>

    // ── Admin Activities ──────────────────────────────────────────────────────

    @GET("api/activities/recent")
    suspend fun getRecentActivities(): Response<List<Activity>>

    @GET("api/activities/recent/admin")
    suspend fun getRecentAdminActivities(): Response<List<Activity>>

    @DELETE("api/activities/recent/admin/{notificationId}")
    suspend fun deleteAdminActivity(
        @Path("notificationId") notificationId: Long
    ): Response<ApiResponse<Any>>

    // ── Agent List (Admin) ────────────────────────────────────────────────────

    @GET("api/agents/list")
    suspend fun getAgentList(): Response<ApiResponse<List<AgentStatusItem>>>
}
