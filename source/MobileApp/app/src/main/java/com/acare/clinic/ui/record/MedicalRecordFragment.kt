package com.acare.clinic.ui.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.acare.clinic.agent.core.AgentInitializer
import com.acare.clinic.agent.dlp.MaskingUtil
import com.acare.clinic.data.model.MedicalRecord
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.FragmentMedicalRecordBinding
import com.acare.clinic.utils.PdfExportUtil
import com.acare.clinic.utils.SessionManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MedicalRecordFragment : Fragment() {

    private var _binding: FragmentMedicalRecordBinding? = null
    private val binding get() = _binding!!
    private val api by lazy { NetworkClient.create(ApiService::class.java) }
    private var latestRecords: List<MedicalRecord> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMedicalRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnExportPdf.setOnClickListener { exportRecordsPdf() }
        loadRecords()
        binding.swipeRefresh.setOnRefreshListener { loadRecords() }
    }

    private fun loadRecords() {
        val userId = SessionManager.getUserId()
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val res = api.getMedicalRecords(userId)
                val list = res.body() ?: emptyList()
                latestRecords = list
                if (list.isEmpty()) {
                    binding.emptyState.visibility = View.VISIBLE
                    binding.rvRecords.visibility = View.GONE
                } else {
                    binding.emptyState.visibility = View.GONE
                    binding.rvRecords.visibility = View.VISIBLE
                    binding.rvRecords.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvRecords.adapter = MedicalRecordAdapter(list) { rec ->
                        showRecordDetails(rec)
                    }
                }
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Lỗi tải hồ sơ bệnh án", Snackbar.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun showRecordDetails(rec: MedicalRecord) {
        val details = buildString {
            append("Mã hồ sơ: ${rec.recordCode}\n")
            append("Bác sĩ: ${rec.doctorName ?: "N/A"}\n")
            append("Ngày tạo: ${rec.createdAt}\n\n")
            append("Lý do khám:\n${rec.chiefComplaint ?: "N/A"}\n\n")
            append("Chẩn đoán:\n${rec.diagnosis ?: "N/A"}\n\n")
            append("Phác đồ điều trị:\n${rec.treatmentPlan ?: "N/A"}\n\n")
            append("Ghi chú lâm sàng:\n${rec.clinicalNotes ?: "N/A"}\n\n")
            append("Ngày tái khám: ${rec.followUpDate ?: "N/A"}")
        }
        com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
            .setTitle("Chi tiết Hồ sơ bệnh án")
            .setMessage(details)
            .setPositiveButton("Đóng") { d, _ -> d.dismiss() }
            .show()
    }

    private fun exportRecordsPdf() {
        val records = latestRecords
        if (records.isEmpty()) {
            Snackbar.make(binding.root, "Khong co du lieu de export", Snackbar.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val userId = SessionManager.getUserId()
            val profile = runCatching { api.getMe().body() }.getOrNull()

            val rawLines = mutableListOf<String>()
            rawLines += "Thong tin benh nhan"
            rawLines += "Ho ten: ${profile?.fullName ?: SessionManager.getUserName()}"
            rawLines += "Email: ${profile?.email ?: SessionManager.getUserEmail()}"
            rawLines += "So dien thoai: ${profile?.phone ?: "N/A"}"
            rawLines += "CCCD: ${profile?.idNumber ?: "N/A"}"
            rawLines += "Dia chi: ${profile?.address ?: "N/A"}"
            rawLines += "Ngay sinh: ${profile?.birthDate ?: "N/A"}"
            rawLines += ""
            rawLines += "Danh sach ho so benh an (${records.size})"

            records.forEachIndexed { index, record ->
                rawLines += "${index + 1}. Ma ho so: ${record.recordCode}"
                rawLines += "   Bac si: ${record.doctorName ?: "N/A"}"
                rawLines += "   Trieu chung: ${record.chiefComplaint ?: "N/A"}"
                rawLines += "   Chan doan: ${record.diagnosis ?: "N/A"}"
                rawLines += "   Phac do: ${record.treatmentPlan ?: "N/A"}"
                rawLines += "   Ngay tao: ${record.createdAt}"
                rawLines += ""
            }

            val payload = rawLines.joinToString("\n")

            if (AgentInitializer.isInitialized()) {
                val accepted = AgentInitializer.getEventTracker().trackExport(
                    userId = userId,
                    patientId = userId,
                    exportText = MaskingUtil.mask(payload)
                )
                AgentInitializer.getAgentManager().syncPendingEvents()
                if (!accepted) {
                    Snackbar.make(binding.root, "DLP chan export vi pham chinh sach", Snackbar.LENGTH_LONG).show()
                    return@launch
                }
            }

            val file = PdfExportUtil.exportTextAsPdf(
                requireContext(),
                title = "Ho so benh an benh nhan #$userId",
                lines = rawLines,
                prefix = "patient_records"
            )
            Snackbar.make(binding.root, "Da export PDF: ${file.name}", Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
