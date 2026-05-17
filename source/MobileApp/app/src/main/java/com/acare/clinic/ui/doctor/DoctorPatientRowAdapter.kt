package com.acare.clinic.ui.doctor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.acare.clinic.R
import com.acare.clinic.data.model.DoctorPatientSummaryRow
import com.google.android.material.button.MaterialButton

class DoctorPatientRowAdapter(
    private val items: List<DoctorPatientSummaryRow>,
    private val onViewHistory: (DoctorPatientSummaryRow) -> Unit
) : RecyclerView.Adapter<DoctorPatientRowAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvPatientName: TextView = view.findViewById(R.id.tvPatientName)
        val tvDoneCount: TextView = view.findViewById(R.id.tvDoneCount)
        val tvServices: TextView = view.findViewById(R.id.tvServices)
        val btnViewHistory: MaterialButton = view.findViewById(R.id.btnViewHistory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor_patient_row, parent, false)
        return VH(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val row = items[position]
        holder.tvPatientName.text = row.patientName ?: "Bệnh nhân #${row.patientId}"
        holder.tvDoneCount.text = "${row.doneAppointments} lượt khám"

        val services = row.services.takeIf { it.isNotEmpty() }?.joinToString(
            separator = ", "
        ) ?: "Chưa có dịch vụ"
        holder.tvServices.text = "Dịch vụ: $services"

        holder.btnViewHistory.setOnClickListener { onViewHistory(row) }
    }
}
