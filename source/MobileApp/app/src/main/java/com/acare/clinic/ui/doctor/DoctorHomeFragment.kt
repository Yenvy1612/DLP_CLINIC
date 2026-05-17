package com.acare.clinic.ui.doctor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.acare.clinic.data.model.Appointment
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.FragmentDoctorHomeBinding
import com.acare.clinic.utils.SessionManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

import androidx.navigation.fragment.findNavController
import com.acare.clinic.R

/**
 * DoctorHomeFragment — Màn hình chính của DOCTOR.
 * Hiển thị: chào hỏi, thống kê nhanh, danh sách lịch hẹn hôm nay đang chờ.
 * Tương đương: Doctor dashboard trong Frontend web.
 */
class DoctorHomeFragment : Fragment() {

    private var _binding: FragmentDoctorHomeBinding? = null
    private val binding get() = _binding!!
    private val api by lazy { NetworkClient.create(ApiService::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupHeader()
        loadDashboard()
        loadPendingAppointments()
        
        binding.btnDoctorProfile.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }
    }

    private fun setupHeader() {
        val hour = LocalDateTime.now().hour
        val greeting = when {
            hour < 12 -> "Chào buổi sáng ☀️"
            hour < 18 -> "Chào buổi chiều 🌤️"
            else      -> "Chào buổi tối 🌙"
        }
        val today = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy", Locale("vi"))
        )
        binding.tvGreeting.text = greeting
        binding.tvDoctorName.text = "BS. ${SessionManager.getUserName()}"
        binding.tvTodayDate.text = today
        binding.tvAvatarInitial.text =
            SessionManager.getUserName().firstOrNull()?.uppercaseChar()?.toString() ?: "D"
    }

    private fun loadDashboard() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.getDoctorDashboard()
                if (res.isSuccessful) {
                    val dashboard = res.body() ?: return@launch
                    binding.tvTodayCount.text = dashboard.todayAppointments.toString()
                    binding.tvPendingCount.text = dashboard.pendingAppointments.toString()
                    binding.tvTotalPatients.text = dashboard.totalPatients.toString()
                    binding.tvCompletedMonth.text = dashboard.completedThisMonth.toString()
                }
            } catch (_: Exception) {
                // Silently ignore — dashboard stats are optional
            }
        }
    }

    private fun loadPendingAppointments() {
        val doctorId = SessionManager.getUserId()
        if (doctorId < 0) return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.getPendingAppointmentsByDoctor(doctorId)
                if (res.isSuccessful) {
                    val list = res.body() ?: emptyList()
                    displayAppointments(list.take(5))
                }
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Không thể tải lịch hẹn", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayAppointments(list: List<Appointment>) {
        if (list.isEmpty()) {
            binding.emptyAppointments.visibility = View.VISIBLE
            binding.rvPendingAppointments.visibility = View.GONE
        } else {
            binding.emptyAppointments.visibility = View.GONE
            binding.rvPendingAppointments.visibility = View.VISIBLE
            binding.rvPendingAppointments.layoutManager =
                LinearLayoutManager(requireContext())
            binding.rvPendingAppointments.adapter =
                DoctorAppointmentAdapter(list) { apt ->
                    handleDone(apt.id)
                }
        }
    }

    private fun handleDone(appointmentId: Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                api.markAppointmentDone(appointmentId)
                Snackbar.make(binding.root, "Đã hoàn thành lịch hẹn ✓", Snackbar.LENGTH_SHORT).show()
                loadPendingAppointments()
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Lỗi cập nhật", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
