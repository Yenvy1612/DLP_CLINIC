package com.acare.clinic.agent.auth

interface TokenProvider {
    fun getAccessToken(): String?
}