package com.acare.clinic.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.acare.clinic.data.model.UserProfile

/**
 * SessionManager — lưu trữ thông tin user đã đăng nhập.
 * Dùng EncryptedSharedPreferences để bảo mật.
 * Cookie được OkHttp CookieJar tự quản lý.
 */
object SessionManager {

    private const val PREF_NAME = "acare_session"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        prefs = EncryptedSharedPreferences.create(
            context,
            PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveSession(userId: Long, name: String, email: String, role: String) {
        prefs.edit().apply {
            putLong(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_ROLE, role)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserId(): Long = prefs.getLong(KEY_USER_ID, -1L)

    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""

    fun getUserEmail(): String = prefs.getString(KEY_USER_EMAIL, "") ?: ""

    fun getRole(): String = prefs.getString(KEY_USER_ROLE, "PATIENT") ?: "PATIENT"

    fun isPatient(): Boolean = getRole() == "PATIENT"

    fun isDoctor(): Boolean = getRole() == "DOCTOR"

    fun isAdmin(): Boolean = getRole() == "ADMIN"

    fun clear() {
        prefs.edit().clear().apply()
    }
}
