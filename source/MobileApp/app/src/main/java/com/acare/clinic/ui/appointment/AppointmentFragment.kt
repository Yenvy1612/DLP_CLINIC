package com.acare.clinic.ui.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.acare.clinic.data.model.Appointment
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.FragmentAppointmentBinding
import com.acare.clinic.ui.home.AppointmentCardAdapter
import com.acare.clinic.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AppointmentFragment : Fragment() {

    private var _binding: FragmentAppointmentBinding? = null
    private val binding get() = _binding!!
    private val api by lazy { NetworkClient.create(ApiService::class.java) }

    private var pendingJob: Job? = null
    private var historyJob: Job? = null

    private val cancelReasonOptions = listOf(
        "Lịch cá nhân bận đột xuất",
        "Sức khỏe tạm thời chưa đi khám được",
        "Muốn đổi sang ngày khác",
        "Lý do khác"
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAppointmentBinding.inflate(inflater, container, false)
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
                    1 -> loadHistory()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadPending() {
        val userId = SessionManager.getUserId()
        if (userId < 0 || _binding == null) return

        pendingJob?.cancel()
        historyJob?.cancel()
        setLoading(true)

        pendingJob = viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.getPendingAppointments(userId)
                val list = enrichAppointments(res.body() ?: emptyList())
                if (_binding == null || !isAdded) return@launch

                if (list.isEmpty()) {
                    showEmpty()
                } else {
                    binding.emptyState.visibility = View.GONE
                    binding.rvAppointments.visibility = View.VISIBLE
                    binding.rvAppointments.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvAppointments.adapter = AppointmentCardAdapter(list) { apt ->
                        confirmCancel(apt.id)
                    }
                }
            } catch (_: Exception) {
                if (_binding != null && isAdded) {
                    Snackbar.make(binding.root, "Lỗi tải dữ liệu", Snackbar.LENGTH_SHORT).show()
                }
            } finally {
                if (_binding != null && isAdded) {
                    setLoading(false)
                }
            }
        }
    }

    private fun loadHistory() {
        val userId = SessionManager.getUserId()
        if (userId < 0 || _binding == null) return

        historyJob?.cancel()
        pendingJob?.cancel()
        setLoading(true)

        historyJob = viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.getAppointmentHistory(userId)
                val list = enrichAppointments(res.body()?.content ?: emptyList())
                if (_binding == null || !isAdded) return@launch

                if (list.isEmpty()) {
                    showEmpty()
                } else {
                    binding.emptyState.visibility = View.GONE
                    binding.rvAppointments.visibility = View.VISIBLE
                    binding.rvAppointments.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvAppointments.adapter = AppointmentCardAdapter(list)
                }
            } catch (_: Exception) {
                if (_binding != null && isAdded) {
                    Snackbar.make(binding.root, "Lỗi tải dữ liệu", Snackbar.LENGTH_SHORT).show()
                }
            } finally {
                if (_binding != null && isAdded) {
                    setLoading(false)
                }
            }
        }
    }

    private fun confirmCancel(appointmentId: Long) {
        if (!isAdded || _binding == null) return

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
        if (!isAdded || _binding == null) return

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
                    if (_binding != null && isAdded) {
                        Snackbar.make(binding.root, "Vui lòng nhập lý do hủy", Snackbar.LENGTH_SHORT).show()
                    }
                } else {
                    submitCancel(appointmentId, reason)
                }
            }
            .show()
    }

    private fun submitCancel(appointmentId: Long, reason: String) {
        if (_binding == null) return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.cancelAppointment(appointmentId, cancelReason = reason)
                val body = res.body()
                if (_binding == null || !isAdded) return@launch

                if (res.isSuccessful && (body == null || body.success)) {
                    Snackbar.make(binding.root, "Đã hủy lịch hẹn", Snackbar.LENGTH_SHORT).show()
                    loadPending()
                } else {
                    Snackbar.make(binding.root, body?.message ?: "Hủy thất bại", Snackbar.LENGTH_SHORT).show()
                }
            } catch (_: Exception) {
                if (_binding != null && isAdded) {
                    Snackbar.make(binding.root, "Hủy thất bại", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showEmpty() {
        if (_binding == null) return
        binding.emptyState.visibility = View.VISIBLE
        binding.rvAppointments.visibility = View.GONE
    }

    private suspend fun enrichAppointments(input: List<Appointment>): List<Appointment> {
        if (input.isEmpty()) return input

        return coroutineScope {
            val doctorIds = input.mapNotNull { it.doctorId }.distinct()
            val serviceIds = input.mapNotNull { it.serviceId }.distinct()

            val doctorMap = doctorIds.map { doctorId ->
                async {
                    doctorId to runCatching { api.getUserById(doctorId).body()?.fullName }.getOrNull()
                }
            }.awaitAll().toMap()

            val serviceMap = serviceIds.map { serviceId ->
                async {
                    serviceId to runCatching { api.getServiceById(serviceId).body()?.name }.getOrNull()
                }
            }.awaitAll().toMap()

            input.map { apt ->
                apt.copy(
                    doctorName = apt.doctorName ?: doctorMap[apt.doctorId],
                    serviceName = apt.serviceName ?: serviceMap[apt.serviceId]
                )
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        if (_binding == null) return
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        pendingJob?.cancel()
        historyJob?.cancel()
        super.onDestroyView()
        _binding = null
    }
}
