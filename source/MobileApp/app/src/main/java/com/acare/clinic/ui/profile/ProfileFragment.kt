package com.acare.clinic.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.acare.clinic.data.model.ChangePasswordRequest
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.FragmentProfileBinding
import com.acare.clinic.ui.auth.LoginActivity
import com.acare.clinic.ui.main.MainActivity
import com.acare.clinic.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val api by lazy { NetworkClient.create(ApiService::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfile()
        binding.btnChangePassword.setOnClickListener { showChangePasswordDialog() }
        binding.btnLogout.setOnClickListener { confirmLogout() }
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            try {
                val res = api.getMe()
                val profile = res.body()
                if (profile != null) {
                    binding.tvName.text = profile.fullName
                    binding.tvEmail.text = profile.email
                    binding.tvPhone.text = profile.phone ?: "Chưa cập nhật"
                    binding.tvRole.text = when (profile.role) {
                        "PATIENT" -> "Bệnh nhân"
                        "DOCTOR" -> "Bác sĩ"
                        "ADMIN" -> "Quản trị viên"
                        else -> profile.role
                    }
                    binding.tvAddress.text = profile.address ?: "Chưa cập nhật"
                    // Avatar initials
                    binding.tvAvatarInitial.text = profile.fullName.take(1).uppercase()
                }
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Không thể tải thông tin", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showChangePasswordDialog() {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(com.acare.clinic.R.layout.dialog_change_password, null)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Đổi mật khẩu")
            .setView(dialogView)
            .setNegativeButton("Hủy") { d, _ -> d.dismiss() }
            .setPositiveButton("Xác nhận") { _, _ ->
                val current = dialogView.findViewById<TextInputEditText>(com.acare.clinic.R.id.etCurrentPassword)?.text.toString()
                val newPass = dialogView.findViewById<TextInputEditText>(com.acare.clinic.R.id.etNewPassword)?.text.toString()
                val confirm = dialogView.findViewById<TextInputEditText>(com.acare.clinic.R.id.etConfirmNewPassword)?.text.toString()
                if (newPass == confirm && newPass.length >= 6) {
                    changePassword(current, newPass, confirm)
                } else {
                    Snackbar.make(binding.root, "Mật khẩu không hợp lệ", Snackbar.LENGTH_SHORT).show()
                }
            }
            .show()
    }

    private fun changePassword(current: String, newPass: String, confirm: String) {
        lifecycleScope.launch {
            try {
                val res = api.changePassword(ChangePasswordRequest(current, newPass, confirm))
                if (res.isSuccessful) {
                    Snackbar.make(binding.root, "Đổi mật khẩu thành công", Snackbar.LENGTH_SHORT).show()
                } else {
                    Snackbar.make(binding.root, "Mật khẩu hiện tại không đúng", Snackbar.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Lỗi kết nối", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmLogout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc muốn đăng xuất không?")
            .setNegativeButton("Hủy") { d, _ -> d.dismiss() }
            .setPositiveButton("Đăng xuất") { _, _ ->
                lifecycleScope.launch {
                    try { api.logout() } catch (_: Exception) {}
                    (activity as? MainActivity)?.logout()
                }
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
