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
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.getMe()
                val profile = res.body()
                if (res.isSuccessful && profile != null) {
                    _binding?.let { b ->
                        b.tvName.text = profile.fullName ?: "Chưa cập nhật"
                        b.tvEmail.text = profile.email ?: "Chưa cập nhật"
                        b.tvPhone.text = profile.phone ?: "Chưa cập nhật"
                        b.tvRole.text = when (profile.role) {
                            "PATIENT" -> "Bệnh nhân"
                            "DOCTOR" -> "Bác sĩ"
                            "ADMIN" -> "Quản trị viên"
                            else -> profile.role ?: "Chưa rõ"
                        }
                        b.tvAddress.text = profile.address ?: "Chưa cập nhật"
                        // Avatar initials
                        b.tvAvatarInitial.text = profile.fullName?.take(1)?.uppercase() ?: "?"
                    }
                } else {
                    _binding?.let { b ->
                        Snackbar.make(b.root, "Không thể tải thông tin cá nhân", Snackbar.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                _binding?.let { b ->
                    Snackbar.make(b.root, "Lỗi kết nối", Snackbar.LENGTH_SHORT).show()
                }
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
                if (newPass == confirm && newPass.length >= 8) {
                    changePassword(current, newPass, confirm)
                } else {
                    _binding?.let { b ->
                        Snackbar.make(b.root, "Mật khẩu mới không khớp hoặc chưa đủ 8 ký tự", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    private fun changePassword(current: String, newPass: String, confirm: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.changePassword(ChangePasswordRequest(current, newPass, confirm))
                _binding?.let { b ->
                    if (res.isSuccessful) {
                        Snackbar.make(b.root, "Đổi mật khẩu thành công", Snackbar.LENGTH_SHORT).show()
                    } else {
                        val errorMsg = try {
                            val errorJson = org.json.JSONObject(res.errorBody()?.string() ?: "")
                            errorJson.optString("message", "Mật khẩu hiện tại không đúng")
                        } catch (e: Exception) {
                            "Mật khẩu hiện tại không đúng"
                        }
                        Snackbar.make(b.root, "Lỗi: $errorMsg", Snackbar.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                _binding?.let { b ->
                    Snackbar.make(b.root, "Lỗi kết nối", Snackbar.LENGTH_SHORT).show()
                }
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
