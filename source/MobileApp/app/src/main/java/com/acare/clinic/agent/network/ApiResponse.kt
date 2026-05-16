package com.acare.clinic.agent.network

data class ApiResponse<T>(
    val code: Int? = null,
    val message: String? = null,
    val result: T? = null
)