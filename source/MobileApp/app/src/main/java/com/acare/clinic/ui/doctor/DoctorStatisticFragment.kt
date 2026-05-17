package com.acare.clinic.ui.doctor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.data.model.DoctorPatientSummaryRow
import com.acare.clinic.databinding.FragmentDoctorStatisticBinding
import com.acare.clinic.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.LinearLayoutManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * DoctorStatisticFragment — Thống kê của bác sĩ.
 * Tương đương Statistic.jsx trong Frontend web.
 * Gọi: GET /api/doctor/statistics/dashboard
 */
class DoctorStatisticFragment : Fragment() {

    private var _binding: FragmentDoctorStatisticBinding? = null
    private val binding get() = _binding!!
    private val api by lazy { NetworkClient.create(ApiService::class.java) }

    private var currentMonth = LocalDate.now().monthValue
    private var currentYear = LocalDate.now().year

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorStatisticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadStatistics()
        binding.btnRefresh.setOnClickListener { loadStatistics() }
    }

    private fun loadStatistics() {
        setLoading(true)
        lifecycleScope.launch {
            try {
                val today = LocalDate.now()
                currentMonth = today.monthValue
                currentYear = today.year

                val statsRes = api.getDoctorStatisticsDashboard(
                    periodType = "MONTH",
                    month = currentMonth,
                    year = currentYear
                )

                if (statsRes.isSuccessful) {
                    val d = statsRes.body() ?: return@launch

                    val dailyVisits = d.dailyVisits ?: emptyList()
                    val todayCount = dailyVisits
                        .firstOrNull { it.date == today.toString() }
                        ?.doneAppointments ?: 0

                    val daysWithVisits = dailyVisits.count { it.doneAppointments > 0 }

                    binding.tvTodayAppointments.text = todayCount.toString()
                    binding.tvTotalPatients.text = d.uniquePatientCount.toString()
                    binding.tvCompletedMonth.text = daysWithVisits.toString()
                    binding.tvTotalAppointments.text = d.doneAppointmentCount.toString()

                    binding.tvPatientSectionRange.text = formatRange(d.fromDate, d.toDate)
                    renderPatientRows(d.patientRows ?: emptyList())
                } else {
                    showOfflineData()
                }

                loadPendingCount()
            } catch (e: Exception) {
                showOfflineData()
            } finally {
                setLoading(false)
            }
        }
    }

    private suspend fun loadPendingCount() {
        val doctorId = SessionManager.getUserId()
        if (doctorId < 0) {
            binding.tvPendingAppointments.text = "—"
            return
        }

        try {
            val res = api.getAppointments(doctorId = doctorId, pending = true)
            val total = res.body()?.size ?: 0
            binding.tvPendingAppointments.text = total.toString()
        } catch (_: Exception) {
            binding.tvPendingAppointments.text = "—"
        }
    }

    private fun renderPatientRows(rows: List<DoctorPatientSummaryRow>) {
        if (rows.isEmpty()) {
            binding.rvPatientRows.visibility = View.GONE
            binding.tvPatientEmpty.visibility = View.VISIBLE
            return
        }

        binding.tvPatientEmpty.visibility = View.GONE
        binding.rvPatientRows.visibility = View.VISIBLE
        if (binding.rvPatientRows.layoutManager == null) {
            binding.rvPatientRows.layoutManager = LinearLayoutManager(requireContext())
        }
        binding.rvPatientRows.adapter = DoctorPatientRowAdapter(rows) { row ->
            loadPatientHistory(row)
        }
    }

    private fun loadPatientHistory(row: DoctorPatientSummaryRow) {
        lifecycleScope.launch {
            try {
                val res = api.getPatientAppointmentsForDoctor(
                    patientId = row.patientId,
                    periodType = "MONTH",
                    month = currentMonth,
                    year = currentYear,
                    page = 0,
                    size = 10
                )

                if (!res.isSuccessful || res.body() == null) {
                    Snackbar.make(binding.root, "Không tải được lịch sử", Snackbar.LENGTH_SHORT).show()
                    return@launch
                }

                val body = res.body()!!
                val items = body.items
                val message = if (items.isEmpty()) {
                    "Chưa có lịch sử trong kỳ này."
                } else {
                    items.joinToString(separator = "\n") { item ->
                        val timeLabel = formatDateTime(item.startTime)
                        val service = item.serviceName ?: "Dịch vụ"
                        "• $timeLabel — $service"
                    }
                }

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Lịch sử: ${row.patientName ?: "Bệnh nhân"}")
                    .setMessage(message)
                    .setPositiveButton("Đóng", null)
                    .show()
            } catch (_: Exception) {
                Snackbar.make(binding.root, "Không tải được lịch sử", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun formatRange(fromDate: String?, toDate: String?): String {
        if (fromDate.isNullOrBlank() || toDate.isNullOrBlank()) return ""
        return "Kỳ: ${formatDate(fromDate)} - ${formatDate(toDate)}"
    }

    private fun formatDate(raw: String): String {
        return try {
            val parsed = LocalDate.parse(raw)
            parsed.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (_: Exception) {
            raw
        }
    }

    private fun formatDateTime(raw: String?): String {
        if (raw.isNullOrBlank()) return "—"
        return try {
            raw.replace("T", " ").substring(0, 16)
        } catch (_: Exception) {
            raw
        }
    }

    /** Hiển thị số 0 khi không có kết nối */
    private fun showOfflineData() {
        binding.tvTotalPatients.text = "—"
        binding.tvTotalAppointments.text = "—"
        binding.tvPendingAppointments.text = "—"
        binding.tvCompletedMonth.text = "—"
        binding.tvTodayAppointments.text = "—"
        binding.tvPatientSectionRange.text = ""
        binding.rvPatientRows.visibility = View.GONE
        binding.tvPatientEmpty.visibility = View.VISIBLE
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.contentLayout.visibility = if (loading) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
