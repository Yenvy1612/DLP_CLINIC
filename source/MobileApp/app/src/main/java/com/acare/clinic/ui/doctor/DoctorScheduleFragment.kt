package com.acare.clinic.ui.doctor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.EditText
import com.acare.clinic.agent.dlp.MaskingUtil
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.FragmentDoctorScheduleBinding
import com.acare.clinic.agent.core.AgentInitializer
import com.acare.clinic.data.model.Appointment
import com.acare.clinic.utils.PdfExportUtil
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
    private var latestAppointments: List<Appointment> = emptyList()
    private var medicalSummaryByAppointmentId: Map<Long, String> = emptyMap()

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
        binding.btnExportDoctorPdf.setOnClickListener { exportSchedulePdf() }
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
                latestAppointments = list
                medicalSummaryByAppointmentId = loadMedicalSummaries(list)
                if (list.isEmpty()) showEmpty() else {
                    binding.emptyState.visibility = View.GONE
                    binding.rvSchedule.visibility = View.VISIBLE
                    binding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvSchedule.adapter = DoctorAppointmentAdapter(
                        list,
                        medicalRecordSummaryByAppointmentId = medicalSummaryByAppointmentId,
                        onDone = { apt -> markDone(apt.id) },
                        onCancel = { apt -> confirmCancel(apt.id) },
                        onRecord = { apt -> showMedicalRecordDialog(apt) }
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
                latestAppointments = list
                medicalSummaryByAppointmentId = loadMedicalSummaries(list)
                if (list.isEmpty()) showEmpty() else {
                    binding.emptyState.visibility = View.GONE
                    binding.rvSchedule.visibility = View.VISIBLE
                    binding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvSchedule.adapter = DoctorAppointmentAdapter(
                        list,
                        medicalRecordSummaryByAppointmentId = medicalSummaryByAppointmentId,
                        onDone = { apt -> markDone(apt.id) },
                        onCancel = { apt -> confirmCancel(apt.id) },
                        onRecord = { apt -> showMedicalRecordDialog(apt) }
                    )
                }
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Lỗi tải dữ liệu", Snackbar.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun exportSchedulePdf() {
        val doctorId = SessionManager.getUserId()
        if (latestAppointments.isEmpty()) {
            Snackbar.make(binding.root, "Khong co lich de export", Snackbar.LENGTH_SHORT).show()
            return
        }

        val payload = buildString {
            append("doctorId=").append(doctorId).append("\n")
            latestAppointments.forEachIndexed { index, apt ->
                append(index + 1).append(". ")
                    .append(apt.patientName ?: apt.patientId).append(" | ")
                    .append(apt.serviceName ?: "").append(" | ")
                    .append(apt.startTime).append(" | ")
                    .append(apt.status).append("\n")
            }
        }

        lifecycleScope.launch {
            if (AgentInitializer.isInitialized()) {
                val accepted = AgentInitializer.getEventTracker().trackExport(
                    userId = doctorId,
                    patientId = doctorId,
                    exportText = payload
                )
                AgentInitializer.getAgentManager().syncPendingEvents()
                if (!accepted) {
                    Snackbar.make(binding.root, "DLP chan export vi pham chinh sach", Snackbar.LENGTH_LONG).show()
                    return@launch
                }
            }

            val lines = mutableListOf<String>()
            latestAppointments.forEach { apt ->
                val summary = medicalSummaryByAppointmentId[apt.id] ?: "Ho so: Chua co"
                lines += MaskingUtil.mask("BN: ${apt.patientName ?: apt.patientId} | DV: ${apt.serviceName ?: "N/A"} | Gio: ${apt.startTime} | Trang thai: ${apt.status}")
                lines += MaskingUtil.mask("   -> $summary")
                
                try {
                    val rec = api.getMedicalRecordByAppointmentId(apt.id).body()
                    if (rec != null) {
                        val maskedCccd = rec.patientIdNumber?.let { if (it.length > 4) it.take(2) + "***" + it.takeLast(2) else "***" } ?: "N/A"
                        val maskedEmail = rec.patientEmail?.let { if (it.contains("@")) it.substringBefore("@").take(2) + "***@" + it.substringAfter("@") else "***" } ?: "N/A"
                        val maskedPhone = rec.patientPhone?.let { if (it.length > 4) it.take(3) + "***" + it.takeLast(2) else "***" } ?: "N/A"
                        lines += "   * Thong tin benh nhan: CCCD=$maskedCccd, Email=$maskedEmail, SDT=$maskedPhone"
                        lines += MaskingUtil.mask("   * Chan doan: ${rec.diagnosis ?: "N/A"}")
                        lines += MaskingUtil.mask("   * Phac do dieu tri: ${rec.treatmentPlan ?: "N/A"}")
                        lines += MaskingUtil.mask("   * Ghi chu lam sang: ${rec.clinicalNotes ?: "N/A"}")
                        lines += MaskingUtil.mask("   * Ngay tai kham: ${rec.followUpDate ?: "N/A"}")
                    }
                } catch (e: Exception) {}
                lines += ""
            }
            
            val file = PdfExportUtil.exportTextAsPdf(
                requireContext(),
                title = "Lich kham va HSBA - Bac si #$doctorId",
                lines = lines,
                prefix = "doctor_schedule_records"
            )
            Snackbar.make(binding.root, "Da export PDF: ${file.name}", Snackbar.LENGTH_LONG).show()
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

    private suspend fun loadMedicalSummaries(appointments: List<Appointment>): Map<Long, String> {
        val out = mutableMapOf<Long, String>()
        appointments.forEach { apt ->
            val text = runCatching {
                val rec = api.getMedicalRecordByAppointmentId(apt.id).body()
                if (rec == null) {
                    "Ho so: Chua co"
                } else {
                    "Ho so: ${rec.recordCode} | CD: ${rec.diagnosis ?: "N/A"}"
                }
            }.getOrDefault("Ho so: Chua co")
            out[apt.id] = text
        }
        return out
    }

    private fun showMedicalRecordDialog(apt: Appointment) {
        lifecycleScope.launch {
            setLoading(true)
            val existingRecord = try {
                api.getMedicalRecordByAppointmentId(apt.id).body()
            } catch (e: Exception) {
                null
            }
            setLoading(false)

            val dialogView = LayoutInflater.from(requireContext()).inflate(com.acare.clinic.R.layout.dialog_create_medical_record, null)
            val etChiefComplaint = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.acare.clinic.R.id.etChiefComplaint)
            val etDiagnosis = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.acare.clinic.R.id.etDiagnosis)
            val etTreatmentPlan = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.acare.clinic.R.id.etTreatmentPlan)
            val etClinicalNotes = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.acare.clinic.R.id.etClinicalNotes)
            val etFollowUpDate = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(com.acare.clinic.R.id.etFollowUpDate)

            if (existingRecord != null) {
                etChiefComplaint.setText(existingRecord.chiefComplaint)
                etDiagnosis.setText(existingRecord.diagnosis)
                etTreatmentPlan.setText(existingRecord.treatmentPlan)
                etClinicalNotes.setText(existingRecord.clinicalNotes)
                etFollowUpDate.setText(existingRecord.followUpDate)
            }

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(if (existingRecord != null) "Cập nhật Hồ sơ bệnh án" else "Tạo Hồ sơ bệnh án")
                .setView(dialogView)
                .setNegativeButton("Hủy") { d, _ -> d.dismiss() }
                .setPositiveButton("Lưu") { _, _ ->
                    val req = com.acare.clinic.data.model.CreateMedicalRecordRequest(
                        appointmentId = apt.id,
                        patientId = apt.patientId,
                        doctorId = SessionManager.getUserId(),
                        chiefComplaint = etChiefComplaint.text?.toString()?.ifBlank { null },
                        diagnosis = etDiagnosis.text?.toString()?.ifBlank { null },
                        treatmentPlan = etTreatmentPlan.text?.toString()?.ifBlank { null },
                        clinicalNotes = etClinicalNotes.text?.toString()?.ifBlank { null },
                        followUpDate = etFollowUpDate.text?.toString()?.ifBlank { null }
                    )
                    lifecycleScope.launch {
                        setLoading(true)
                        try {
                            val res = if (existingRecord != null) {
                                api.updateMedicalRecord(existingRecord.id, req)
                            } else {
                                api.createMedicalRecord(req)
                            }
                            if (res.isSuccessful) {
                                Snackbar.make(binding.root, "Đã lưu hồ sơ bệnh án", Snackbar.LENGTH_SHORT).show()
                                reloadCurrentTab()
                            } else {
                                Snackbar.make(binding.root, "Lỗi khi lưu hồ sơ", Snackbar.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Snackbar.make(binding.root, "Lỗi kết nối", Snackbar.LENGTH_SHORT).show()
                        } finally {
                            setLoading(false)
                        }
                    }
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
