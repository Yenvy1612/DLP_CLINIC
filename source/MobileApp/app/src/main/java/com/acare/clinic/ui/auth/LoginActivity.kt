package com.acare.clinic.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.acare.clinic.data.model.LoginRequest
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.databinding.ActivityLoginBinding
import com.acare.clinic.ui.main.MainActivity
import com.acare.clinic.utils.SessionManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

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

                    // Lấy thêm profile để lưu tên
                    val meRes = api.getMe()
                    val name = meRes.body()?.fullName ?: auth.username

                    SessionManager.saveSession(
                        userId = auth.id,
                        name = name,
                        email = auth.username,
                        role = auth.role
                    )

                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    showError("Sai tài khoản hoặc mật khẩu")
                }
            } catch (e: Exception) {
                showError("Lỗi kết nối: ${e.localizedMessage}")
            } finally {
                setLoading(false)
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
}
