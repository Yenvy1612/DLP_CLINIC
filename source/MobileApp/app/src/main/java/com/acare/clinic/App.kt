package com.acare.clinic

import android.app.Application
import android.util.Log
import com.acare.clinic.agent.core.AgentConfig
import com.acare.clinic.agent.core.AgentInitializer
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.utils.SessionManager

/**
 * Application class — khởi tạo NetworkClient, SessionManager và Agent SDK.
 *
 * Agent SDK dùng chung OkHttpClient (CookieJar) với NetworkClient
 * để gửi request với cùng session xác thực.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        NetworkClient.init(this)
        SessionManager.init(this)

        // ===================================================
        // Khởi tạo Agent SDK — dùng CookieJar từ NetworkClient
        // ===================================================
        initAgent()
    }

    private fun initAgent() {
        try {
            val config = AgentConfig(
                backendBaseUrl = BuildConfig.BASE_URL,
                appVersion = BuildConfig.VERSION_NAME,
                agentVersion = "1.0.0",
                platform = "ANDROID"
            )

            AgentInitializer.init(
                context = this,
                config = config,
                okHttpClient = NetworkClient.getOkHttpClient()
            )

            Log.i("App", "Agent SDK initialized")
        } catch (e: Exception) {
            Log.e("App", "Failed to init Agent SDK: ${e.message}", e)
        }
    }
}
