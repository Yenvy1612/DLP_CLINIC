package com.acare.clinic.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acare.clinic.R
import com.acare.clinic.data.model.Appointment
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.FragmentAdminAppointmentsBinding
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * AdminAppointmentsFragment — Quản lý lịch hẹn (Admin).
 * Tương đương Appointments.jsx trong Frontend web.
 * Gọi: GET /api/appointments, GET /api/appointments?status=...
 */
class AdminAppointmentsFragment : Fragment() {

    private var _binding: FragmentAdminAppointmentsBinding? = null
    private val binding get() = _binding!!
    private val api by lazy { NetworkClient.create(ApiService::class.java) }
    private var searchJob: Job? = null
    private var allAppointments: List<Appointment> = emptyList()
    private var currentStatusFilter: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminAppointmentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvAppointments.layoutManager = LinearLayoutManager(requireContext())

        // Status filter chips
        binding.chipGroupStatus.setOnCheckedStateChangeListener { _, checkedIds ->
            currentStatusFilter = when {
                checkedIds.contains(R.id.chipPending) -> "PENDING"
                checkedIds.contains(R.id.chipDone) -> "DONE"
                checkedIds.contains(R.id.chipCancelled) -> "CANCELLED"
                else -> null
            }
            applyFilters()
        }

        // Search with debounce
        binding.etSearch.doAfterTextChanged { text ->
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(500)
                applyFilters()
            }
        }

        // Refresh
        binding.btnRefresh.setOnClickListener {
            binding.etSearch.text?.clear()
            currentStatusFilter = null
            binding.chipAll.isChecked = true
            loadAppointments()
        }

        binding.swipeRefresh.setOnRefreshListener { loadAppointments() }

        loadAppointments()
    }

    private fun loadAppointments() {
        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.getAllAppointments()
                allAppointments = res.body() ?: emptyList()
                if (_binding != null) applyFilters()
            } catch (e: Exception) {
                if (_binding != null) {
                    Snackbar.make(binding.root, "Không thể tải danh sách lịch hẹn", Snackbar.LENGTH_SHORT).show()
                }
            } finally {
                if (_binding != null) {
                    setLoading(false)
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun applyFilters() {
        var filtered = allAppointments

        // Status filter
        if (currentStatusFilter != null) {
            filtered = filtered.filter { it.status.equals(currentStatusFilter, ignoreCase = true) }
        }

        // Text search
        val query = binding.etSearch.text.toString().trim().lowercase()
        if (query.isNotEmpty()) {
            filtered = filtered.filter { apt ->
                (apt.doctorName?.lowercase()?.contains(query) == true) ||
                (apt.patientName?.lowercase()?.contains(query) == true) ||
                (apt.serviceName?.lowercase()?.contains(query) == true) ||
                (apt.code?.lowercase()?.contains(query) == true)
            }
        }

        displayAppointments(filtered)
    }

    private fun displayAppointments(appointments: List<Appointment>) {
        binding.tvAppointmentCount.text = "${appointments.size} lịch hẹn"
        if (appointments.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvAppointments.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvAppointments.visibility = View.VISIBLE
            binding.rvAppointments.adapter = AdminAppointmentAdapter(appointments)
        }
    }

    private fun setLoading(loading: Boolean) {
        if (!binding.swipeRefresh.isRefreshing) {
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/** Adapter for admin appointment list */
class AdminAppointmentAdapter(
    private val items: List<Appointment>
) : RecyclerView.Adapter<AdminAppointmentAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvCode: TextView = view.findViewById(R.id.tvAppointmentCode)
        val chipStatus: Chip = view.findViewById(R.id.chipStatus)
        val tvDoctorName: TextView = view.findViewById(R.id.tvDoctorName)
        val tvPatientName: TextView = view.findViewById(R.id.tvPatientName)
        val tvServiceName: TextView = view.findViewById(R.id.tvServiceName)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_appointment, parent, false)
        return VH(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val apt = items[position]
        holder.tvCode.text = apt.code ?: "#${apt.id}"
        holder.tvDoctorName.text = "BS: ${apt.doctorName ?: "Chưa phân công"}"
        holder.tvPatientName.text = "BN: ${apt.patientName ?: "N/A"}"
        holder.tvServiceName.text = "DV: ${apt.serviceName ?: "N/A"}"

        // Format time
        val timeStr = try {
            val start = apt.startTime.replace("T", " ").take(16)
            start
        } catch (_: Exception) { apt.startTime }
        holder.tvTime.text = "⏰ $timeStr"

        // Status chip
        when (apt.status.uppercase()) {
            "PENDING" -> {
                holder.chipStatus.text = "Chờ xác nhận"
                holder.chipStatus.setChipBackgroundColorResource(R.color.warning)
            }
            "DONE" -> {
                holder.chipStatus.text = "Hoàn thành"
                holder.chipStatus.setChipBackgroundColorResource(R.color.success)
            }
            "CANCELLED" -> {
                holder.chipStatus.text = "Đã hủy"
                holder.chipStatus.setChipBackgroundColorResource(R.color.error)
            }
            else -> {
                holder.chipStatus.text = apt.status
                holder.chipStatus.setChipBackgroundColorResource(R.color.text_hint)
            }
        }
    }
}
