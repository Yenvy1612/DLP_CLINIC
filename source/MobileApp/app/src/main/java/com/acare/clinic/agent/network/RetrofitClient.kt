package com.acare.clinic.agent.network


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.acare.clinic.agent.auth.TokenProvider

/**
 * RetrofitClient cho Agent module.
 *
 * Hỗ trợ 2 chế độ:
 *  - Cookie-based (dùng OkHttpClient có sẵn CookieJar từ NetworkClient)
 *  - Bearer token (fallback nếu cần)
 */
object RetrofitClient {

    /**
     * Tạo AgentApiService dùng OkHttpClient có sẵn CookieJar.
     * Đây là cách ưu tiên vì app xác thực bằng HttpOnly cookie.
     */
    fun create(baseUrl: String, okHttpClient: OkHttpClient): AgentApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(AgentApiService::class.java)
    }

    /**
     * Tạo AgentApiService với Bearer token auth (legacy/fallback).
     */
    fun create(
        baseUrl: String,
        tokenProvider: TokenProvider
    ): AgentApiService {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val token = tokenProvider.getAccessToken()
                val requestBuilder = chain.request().newBuilder()

                if (!token.isNullOrBlank()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                chain.proceed(requestBuilder.build())
            }
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(AgentApiService::class.java)
    }
}