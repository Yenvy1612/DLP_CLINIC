package com.acare.clinic.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.acare.clinic.agent.core.AgentInitializer
import com.acare.clinic.databinding.ActivitySplashBinding
import com.acare.clinic.ui.auth.LoginActivity
import com.acare.clinic.ui.main.MainActivity
import com.acare.clinic.utils.SessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            delay(1800L) // Hiển thị splash 1.8 giây
            navigateNext()
        }
    }

    private fun navigateNext() {
        val isLoggedIn = SessionManager.isLoggedIn()

        if (isLoggedIn) {
            // Re-register agent khi app restart với session có sẵn
            reRegisterAgent()
        }

        val intent = if (isLoggedIn) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun reRegisterAgent() {
        lifecycleScope.launch {
            try {
                if (!AgentInitializer.isInitialized()) return@launch

                val manager = AgentInitializer.getAgentManager()
                manager.register(SessionManager.getUserEmail())
                Log.i("SplashActivity", "Agent re-registered on app restart")
            } catch (e: Exception) {
                Log.e("SplashActivity", "Agent re-registration failed: ${e.message}")
            }
        }
    }
}

