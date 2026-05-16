package com.acare.clinic.agent.device

import android.content.Context
import android.provider.Settings
import java.security.MessageDigest

class DeviceIdProvider(
    private val context: Context
) {
    fun getDeviceId(): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown"

        return "ANDROID-" + sha256(androidId).take(16)
    }

    private fun sha256(value: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray())

        return bytes.joinToString("") { "%02x".format(it) }
    }
}