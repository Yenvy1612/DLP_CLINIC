package com.acare.clinic.ui.doctor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.EditText
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.FragmentDoctorScheduleBinding
import com.acare.clinic.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

/**
 * DoctorScheduleFragment — Quản lý lịch khám của bác sĩ.
 * Tương đương Schedule.jsx trong Frontend web.
 *
 * Tab 0: Đang chờ (PENDING)
 * Tab 1: Tất cả lịch hẹn
 */
class DoctorScheduleFragment : Fragment() {

    private var _binding: FragmentDoctorScheduleBinding? = null
    private val binding get() = _binding!!
    private val api by lazy { NetworkClient.create(ApiService::class.java) }

    private val cancelReasonOptions = listOf(
        "Bác sĩ có lịch bận đột xuất",
        "Phòng khám không khả dụng",
        "Cần dời lịch do chuyên môn",
        "Lý do khác"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabs()
        loadPending()
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> loadPending()
                    1 -> loadAll()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadPending() {
        val doctorId = SessionManager.getUserId()
        setLoading(true)
        lifecycleScope.launch {
            try {
                val res = api.getPendingAppointmentsByDoctor(doctorId)
                val list = res.body() ?: emptyList()
                if (list.isEmpty()) showEmpty() else {
                    binding.emptyState.visibility = View.GONE
                    binding.rvSchedule.visibility = View.VISIBLE
                    binding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvSchedule.adapter = DoctorAppointmentAdapter(
                        list,
                        onDone = { apt -> markDone(apt.id) },
                        onCancel = { apt -> confirmCancel(apt.id) }
                    )
                }
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Lỗi tải dữ liệu", Snackbar.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun loadAll() {
        val doctorId = SessionManager.getUserId()
        setLoading(true)
        lifecycleScope.launch {
            try {
                val res = api.getAppointmentsByDoctor(doctorId)
                val list = res.body() ?: emptyList()
                if (list.isEmpty()) showEmpty() else {
                    binding.emptyState.visibility = View.GONE
                    binding.rvSchedule.visibility = View.VISIBLE
                    binding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvSchedule.adapter = DoctorAppointmentAdapter(
                        list,
                        onDone = { apt -> markDone(apt.id) },
                        onCancel = { apt -> confirmCancel(apt.id) }
                    )
                }
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Lỗi tải dữ liệu", Snackbar.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun markDone(appointmentId: Long) {
        lifecycleScope.launch {
            try {
                api.markAppointmentDone(appointmentId)
                Snackbar.make(binding.root, "Đã hoàn thành ✓", Snackbar.LENGTH_SHORT).show()
                reloadCurrentTab()
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Lỗi cập nhật", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmCancel(appointmentId: Long) {
        var selectedIndex = 0
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Chọn lý do hủy")
            .setSingleChoiceItems(cancelReasonOptions.toTypedArray(), 0) { _, which ->
                selectedIndex = which
            }
            .setNegativeButton("Đóng") { d, _ -> d.dismiss() }
            .setPositiveButton("Xác nhận") { _, _ ->
                if (cancelReasonOptions[selectedIndex] == "Lý do khác") {
                    showCustomCancelReasonDialog(appointmentId)
                } else {
                    submitCancel(appointmentId, cancelReasonOptions[selectedIndex])
                }
            }
            .show()
    }

    private fun showCustomCancelReasonDialog(appointmentId: Long) {
        val input = EditText(requireContext()).apply {
            hint = "Nhập lý do hủy"
            maxLines = 3
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Lý do khác")
            .setView(input)
            .setNegativeButton("Đóng") { d, _ -> d.dismiss() }
            .setPositiveButton("Xác nhận") { _, _ ->
                val reason = input.text?.toString()?.trim().orEmpty()
                if (reason.isBlank()) {
                    Snackbar.make(binding.root, "Vui lòng nhập lý do hủy", Snackbar.LENGTH_SHORT).show()
                } else {
                    submitCancel(appointmentId, reason)
                }
            }
            .show()
    }

    private fun submitCancel(appointmentId: Long, reason: String) {
        lifecycleScope.launch {
            try {
                val res = api.cancelAppointment(
                    appointmentId,
                    cancelledBy = "DOCTOR",
                    cancelReason = reason
                )
                val body = res.body()
                if (res.isSuccessful && (body == null || body.success)) {
                    Snackbar.make(binding.root, "Đã hủy lịch hẹn", Snackbar.LENGTH_SHORT).show()
                    reloadCurrentTab()
                } else {
                    Snackbar.make(binding.root, body?.message ?: "Hủy thất bại", Snackbar.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Hủy thất bại", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun reloadCurrentTab() {
        when (binding.tabLayout.selectedTabPosition) {
            1 -> loadAll()
            else -> loadPending()
        }
    }

    private fun showEmpty() {
        binding.emptyState.visibility = View.VISIBLE
        binding.rvSchedule.visibility = View.GONE
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
