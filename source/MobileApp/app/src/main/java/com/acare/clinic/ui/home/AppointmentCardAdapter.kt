package com.acare.clinic.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.acare.clinic.R
import com.acare.clinic.data.model.Appointment
import com.acare.clinic.databinding.ItemAppointmentCardBinding

class AppointmentCardAdapter(
    private val items: List<Appointment>,
    private val onCancel: ((Appointment) -> Unit)? = null
) : RecyclerView.Adapter<AppointmentCardAdapter.VH>() {

    inner class VH(val binding: ItemAppointmentCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAppointmentCardBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val apt = items[position]
        with(holder.binding) {
            tvDoctorName.text = apt.doctorName ?: "Bác sĩ chưa xác định"
            tvServiceName.text = apt.serviceName ?: "Dịch vụ chưa xác định"
            tvDateTime.text = formatDateTime(apt.startTime)
            tvCode.text = "#${apt.code}"

            // Status chip
            val (label, colorRes, textColorRes) = when (apt.status) {
                "PENDING" -> Triple("Chờ khám", R.color.warning_light, R.color.warning)
                "DONE" -> Triple("Hoàn thành", R.color.success_light, R.color.success)
                "CANCELLED" -> Triple("Đã hủy", R.color.error_light, R.color.error)
                "NO_SHOW" -> Triple("Không đến", R.color.divider, R.color.text_secondary)
                else -> Triple(apt.status, R.color.divider, R.color.text_secondary)
            }
            chipStatus.text = label
            chipStatus.chipBackgroundColor = android.content.res.ColorStateList.valueOf(
                ContextCompat.getColor(holder.itemView.context, colorRes)
            )
            chipStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, textColorRes))

            // Nút hủy chỉ hiển thị khi PENDING
            btnCancel.visibility = if (apt.status == "PENDING" && onCancel != null)
                android.view.View.VISIBLE else android.view.View.GONE
            btnCancel.setOnClickListener { onCancel?.invoke(apt) }
        }
    }

    private fun formatDateTime(raw: String): String {
        return try {
            // "2026-05-14T09:00:00" → "09:00 - 14/05/2026"
            val parts = raw.split("T")
            val date = parts[0].split("-")
            val time = parts.getOrElse(1) { "" }.take(5)
            "$time  •  ${date[2]}/${date[1]}/${date[0]}"
        } catch (e: Exception) {
            raw
        }
    }
}
