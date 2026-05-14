package com.acare.clinic.data.network

import android.content.Context
import com.acare.clinic.BuildConfig
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * NetworkClient — singleton quản lý OkHttp + Retrofit.
 *
 * CookieJar tự động lưu và gửi cookie (access_token, refresh_token)
 * từ backend, giải quyết vấn đề HttpOnly cookie trên mobile.
 *
 * Lưu cookie trong memory (sẽ mất khi app bị kill).
 * Để persist: thay ConcurrentHashMap bằng EncryptedSharedPreferences.
 */
object NetworkClient {

    private lateinit var retrofit: Retrofit
    private val cookieStore = ConcurrentHashMap<String, MutableList<Cookie>>()

    fun init(context: Context) {
        val cookieJar = object : CookieJar {
            override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                val key = url.host
                val existing = cookieStore.getOrPut(key) { mutableListOf() }
                cookies.forEach { newCookie ->
                    existing.removeAll { it.name == newCookie.name }
                    existing.add(newCookie)
                }
            }

            override fun loadForRequest(url: HttpUrl): List<Cookie> {
                return cookieStore[url.host] ?: emptyList()
            }
        }

        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG)
                HttpLoggingInterceptor.Level.BODY
            else
                HttpLoggingInterceptor.Level.NONE
        }

        val okHttp = OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> create(service: Class<T>): T = retrofit.create(service)

    /** Xóa toàn bộ cookie (dùng khi logout) */
    fun clearCookies() = cookieStore.clear()
}
