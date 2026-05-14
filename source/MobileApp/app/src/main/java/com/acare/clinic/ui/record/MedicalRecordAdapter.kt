package com.acare.clinic.ui.record

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.acare.clinic.data.model.MedicalRecord
import com.acare.clinic.databinding.ItemMedicalRecordBinding

class MedicalRecordAdapter(private val items: List<MedicalRecord>) :
    RecyclerView.Adapter<MedicalRecordAdapter.VH>() {

    inner class VH(val binding: ItemMedicalRecordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemMedicalRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val rec = items[position]
        with(holder.binding) {
            tvRecordCode.text = "#${rec.recordCode}"
            tvDoctor.text = rec.doctorName ?: "Bác sĩ không xác định"
            tvDiagnosis.text = rec.diagnosis?.take(100) ?: "Chưa có chẩn đoán"
            tvDate.text = formatDate(rec.createdAt)
            tvComplaint.text = rec.chiefComplaint?.take(80) ?: ""
        }
    }

    private fun formatDate(raw: String): String {
        return try {
            val date = raw.split("T")[0].split("-")
            "${date[2]}/${date[1]}/${date[0]}"
        } catch (e: Exception) {
            raw
        }
    }
}
