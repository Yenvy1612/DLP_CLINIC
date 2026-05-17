package com.acare.clinic.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.acare.clinic.agent.core.AgentInitializer
import com.acare.clinic.data.model.LoginRequest
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.ActivityLoginBinding
import com.acare.clinic.ui.main.MainActivity
import com.acare.clinic.utils.SessionManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val api by lazy { NetworkClient.create(ApiService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener { doLogin() }
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun doLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin")
            return
        }

        setLoading(true)

        lifecycleScope.launch {
            try {
                val res = api.login(LoginRequest(email, password))
                if (res.isSuccessful && res.body() != null) {
                    val auth = res.body()!!

                    // Lấy thêm profile để lưu tên hiển thị
                    val meRes = api.getMe()
                    val name = meRes.body()?.fullName ?: auth.username

                    SessionManager.saveSession(
                        userId = auth.id,
                        name = name,
                        email = auth.username,
                        role = auth.role
                    )

                    // ── Đăng ký Agent sau khi login thành công ──
                    registerAgentAfterLogin(auth.username)

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    showError(extractLoginError(res.code(), res.errorBody()?.string()))
                }
            } catch (e: Exception) {
                showError("Lỗi kết nối: ${e.localizedMessage}")
            } finally {
                setLoading(false)
            }
        }
    }

    /**
     * Đăng ký agent device với backend sau khi login thành công.
     * Chạy non-blocking để không ảnh hưởng luồng login.
     */
    private fun registerAgentAfterLogin(username: String) {
        lifecycleScope.launch {
            try {
                if (!AgentInitializer.isInitialized()) {
                    Log.w("LoginActivity", "Agent not initialized, skipping registration")
                    return@launch
                }

                val manager = AgentInitializer.getAgentManager()
                manager.register(username)
                Log.i("LoginActivity", "Agent registered successfully for $username")

                // Track login event qua agent
                val tracker = AgentInitializer.getEventTracker()
                tracker.trackViewPatientDetail(
                    userId = SessionManager.getUserId(),
                    patientId = SessionManager.getUserId()
                )
            } catch (e: Exception) {
                Log.e("LoginActivity", "Agent registration failed: ${e.message}", e)
                // Không block login nếu agent registration thất bại
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.btnLogin.isEnabled = !loading
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnLogin.text = if (loading) "" else "Đăng nhập"
    }

    private fun showError(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
    }

    private fun extractLoginError(statusCode: Int, rawErrorBody: String?): String {
        val backendMessage = try {
            if (rawErrorBody.isNullOrBlank()) null
            else JSONObject(rawErrorBody).optString("message").takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }

        if (!backendMessage.isNullOrBlank()) return backendMessage

        return when (statusCode) {
            400, 401 -> "Sai tài khoản hoặc mật khẩu"
            404 -> "Không tìm thấy API đăng nhập. Kiểm tra đúng địa chỉ backend"
            500 -> "Backend đang lỗi nội bộ (500)"
            else -> "Đăng nhập thất bại (HTTP $statusCode)"
        }
    }
}

