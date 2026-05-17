package com.acare.clinic.agent.network

data class ApiResponse<T>(
    val status: Int? = null,
    val success: Boolean? = null,
    val message: String? = null,
    val data: T? = null
)