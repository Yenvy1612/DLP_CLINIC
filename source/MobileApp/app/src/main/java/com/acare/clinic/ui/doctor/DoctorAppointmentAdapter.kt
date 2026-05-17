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

/**
 * DoctorAppointmentAdapter — hiển thị danh sách lịch hẹn của bác sĩ.
 * Hỗ trợ action: đánh dấu hoàn thành.
 */
class DoctorAppointmentAdapter(
    private val items: List<Appointment>,
    private val onDone: ((Appointment) -> Unit)? = null
) : RecyclerView.Adapter<DoctorAppointmentAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvPatientName: TextView = view.findViewById(R.id.tvPatientName)
        val tvService: TextView = view.findViewById(R.id.tvService)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val chipStatus: Chip = view.findViewById(R.id.chipStatus)
        val btnDone: MaterialButton = view.findViewById(R.id.btnDone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doctor_appointment, parent, false)
        return VH(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val apt = items[position]
        holder.tvPatientName.text = apt.patientName ?: "Bệnh nhân #${apt.patientId}"
        holder.tvService.text = apt.serviceName ?: "—"
        holder.tvTime.text = formatTime(apt.startTime)
        holder.chipStatus.text = mapStatus(apt.status)
        holder.chipStatus.setChipBackgroundColorResource(statusColor(apt.status))

        // Chỉ cho phép Done khi đang PENDING hoặc CONFIRMED
        if (apt.status in listOf("PENDING", "CONFIRMED")) {
            holder.btnDone.visibility = View.VISIBLE
            holder.btnDone.setOnClickListener { onDone?.invoke(apt) }
        } else {
            holder.btnDone.visibility = View.GONE
        }
    }

    private fun formatTime(raw: String): String {
        return try {
            raw.replace("T", " ").substring(0, 16)
        } catch (_: Exception) { raw }
    }

    private fun mapStatus(status: String) = when (status) {
        "PENDING"   -> "Chờ xác nhận"
        "CONFIRMED" -> "Đã xác nhận"
        "DONE"      -> "Hoàn thành"
        "CANCELLED" -> "Đã hủy"
        else        -> status
    }

    private fun statusColor(status: String) = when (status) {
        "PENDING"   -> R.color.warning
        "CONFIRMED" -> R.color.primary
        "DONE"      -> R.color.success
        "CANCELLED" -> R.color.error
        else        -> R.color.text_secondary
    }
}
