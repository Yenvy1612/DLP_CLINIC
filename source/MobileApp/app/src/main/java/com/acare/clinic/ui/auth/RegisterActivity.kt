package com.acare.clinic.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.acare.clinic.data.model.RegisterRequest
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.ActivityRegisterBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val api by lazy { NetworkClient.create(ApiService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnRegister.setOnClickListener { doRegister() }
    }

    private fun doRegister() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirm = binding.etConfirmPassword.text.toString()

        if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin bắt buộc")
            return
        }
        if (password != confirm) {
            showError("Mật khẩu xác nhận không khớp")
            return
        }
        if (password.length < 6) {
            showError("Mật khẩu phải có ít nhất 6 ký tự")
            return
        }

        setLoading(true)
        lifecycleScope.launch {
            try {
                val res = api.register(
                    RegisterRequest(
                        fullName = fullName,
                        email = email,
                        phone = phone,
                        password = password,
                        confirmPassword = confirm
                    )
                )
                if (res.isSuccessful) {
                    Snackbar.make(binding.root, "Đăng ký thành công! Vui lòng đăng nhập.", Snackbar.LENGTH_LONG).show()
                    finish()
                } else {
                    val msg = res.errorBody()?.string() ?: "Đăng ký thất bại"
                    showError(if (msg.contains("EMAIL")) "Email đã được sử dụng" else "Đăng ký thất bại")
                }
            } catch (e: Exception) {
                showError("Lỗi kết nối: ${e.localizedMessage}")
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.btnRegister.isEnabled = !loading
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun showError(msg: String) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_LONG).show()
    }
}
