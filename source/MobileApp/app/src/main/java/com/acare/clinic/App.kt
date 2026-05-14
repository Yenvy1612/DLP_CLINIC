package com.acare.clinic

import android.app.Application
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.utils.SessionManager

/**
 * Application class — khởi tạo NetworkClient (OkHttp + CookieJar) một lần duy nhất.
 *
 * PLACEHOLDER: Agent SDK sẽ được khởi tạo ở đây khi có.
 * Ví dụ:
 *   AgentSDK.init(this, AgentConfig(backendUrl = BuildConfig.BASE_URL))
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        NetworkClient.init(this)
        SessionManager.init(this)

        // ===================================================
        // PLACEHOLDER: Khởi tạo Agent SDK tại đây
        // AgentSDK.init(this, BuildConfig.BASE_URL)
        // ===================================================
    }
}
