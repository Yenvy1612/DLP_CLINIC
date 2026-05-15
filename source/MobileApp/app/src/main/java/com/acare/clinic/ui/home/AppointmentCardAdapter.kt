package com.acare.clinic.ui.home

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
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
        val ctx = holder.itemView.context

        with(holder.binding) {
            tvCode.text = "#${apt.code}"
            tvDoctorName.text = apt.doctorName ?: "Bác sĩ chưa xác định"
            tvService.text = apt.serviceName ?: "Dịch vụ chưa xác định"
            tvTime.text = formatDateTime(apt.startTime)

            // Status: label + colors (giống web FE statusColors)
            val (label, bgColorRes, textColorRes, barColorRes) = when (apt.status) {
                "PENDING"   -> listOf("Đang chờ",  R.color.status_pending,   R.color.status_pending_text,   R.color.primary)
                "DONE"      -> listOf("Hoàn thành",R.color.status_done,      R.color.status_done_text,      R.color.success)
                "CANCELLED" -> listOf("Đã hủy",    R.color.status_cancelled, R.color.status_cancelled_text, R.color.error)
                "NO_SHOW"   -> listOf("Không đến", R.color.divider,          R.color.text_secondary,        R.color.text_hint)
                else        -> listOf(apt.status,  R.color.divider,          R.color.text_secondary,        R.color.text_hint)
            }

            tvStatus.text = label as String
            tvStatus.setTextColor(ContextCompat.getColor(ctx, textColorRes as Int))
            (tvStatus.background as? GradientDrawable)?.setColor(ContextCompat.getColor(ctx, bgColorRes as Int))

            // Left accent bar color
            statusBar.setBackgroundColor(ContextCompat.getColor(ctx, barColorRes as Int))

            // Cancel button
            btnCancel.visibility = if (apt.status == "PENDING" && onCancel != null) View.VISIBLE else View.GONE
            btnCancel.setOnClickListener { onCancel?.invoke(apt) }
        }
    }

    private fun formatDateTime(raw: String): String {
        return try {
            val parts = raw.split("T")
            val date = parts[0].split("-")
            val time = parts.getOrElse(1) { "" }.take(5)
            "$time  •  ${date[2]}/${date[1]}/${date[0]}"
        } catch (e: Exception) {
            raw
        }
    }
}
