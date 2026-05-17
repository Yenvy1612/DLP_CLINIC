package com.acare.clinic.ui.doctor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.acare.clinic.R
import com.acare.clinic.data.model.Appointment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip

class DoctorAppointmentAdapter(
    private val items: List<Appointment>,
    private val medicalRecordSummaryByAppointmentId: Map<Long, String> = emptyMap(),
    private val onDone: ((Appointment) -> Unit)? = null,
    private val onCancel: ((Appointment) -> Unit)? = null,
    private val onRecord: ((Appointment) -> Unit)? = null
) : RecyclerView.Adapter<DoctorAppointmentAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvPatientName: TextView = view.findViewById(R.id.tvPatientName)
        val tvService: TextView = view.findViewById(R.id.tvService)
        val tvMedicalRecord: TextView = view.findViewById(R.id.tvMedicalRecord)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val chipStatus: Chip = view.findViewById(R.id.chipStatus)
        val btnDone: MaterialButton = view.findViewById(R.id.btnDone)
        val btnCancel: MaterialButton = view.findViewById(R.id.btnCancel)
        val btnRecord: MaterialButton = view.findViewById(R.id.btnRecord)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor_appointment, parent, false)
        return VH(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val apt = items[position]
        holder.tvPatientName.text = apt.patientName ?: "Benh nhan #${apt.patientId}"
        holder.tvService.text = apt.serviceName ?: "N/A"
        holder.tvMedicalRecord.text = medicalRecordSummaryByAppointmentId[apt.id] ?: "Ho so: Chua co"
        holder.tvTime.text = formatTime(apt.startTime)
        holder.chipStatus.text = mapStatus(apt.status)
        holder.chipStatus.setChipBackgroundColorResource(statusColor(apt.status))

        val canAct = apt.status in listOf("PENDING", "CONFIRMED")
        holder.btnDone.visibility = if (canAct) View.VISIBLE else View.GONE
        holder.btnCancel.visibility = if (canAct) View.VISIBLE else View.GONE
        holder.btnRecord.visibility = View.VISIBLE

        holder.btnDone.setOnClickListener { onDone?.invoke(apt) }
        holder.btnCancel.setOnClickListener { onCancel?.invoke(apt) }
        holder.btnRecord.setOnClickListener { onRecord?.invoke(apt) }
    }

    private fun formatTime(raw: String): String {
        return try {
            raw.replace("T", " ").substring(0, 16)
        } catch (_: Exception) {
            raw
        }
    }

    private fun mapStatus(status: String) = when (status) {
        "PENDING" -> "Cho xac nhan"
        "CONFIRMED" -> "Da xac nhan"
        "DONE" -> "Hoan thanh"
        "CANCELLED" -> "Da huy"
        else -> status
    }

    private fun statusColor(status: String) = when (status) {
        "PENDING" -> R.color.warning
        "CONFIRMED" -> R.color.primary
        "DONE" -> R.color.success
        "CANCELLED" -> R.color.error
        else -> R.color.text_secondary
    }
}
