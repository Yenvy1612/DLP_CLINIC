package com.acare.clinic.ui.booking

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.acare.clinic.agent.core.AgentInitializer
import com.acare.clinic.R
import com.acare.clinic.data.model.*
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.utils.SessionManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class BookingActivity : AppCompatActivity() {

    private val api by lazy { NetworkClient.create(ApiService::class.java) }

    private lateinit var spinnerService: Spinner
    private lateinit var sectionDoctors: LinearLayout
    private lateinit var layoutDoctors: LinearLayout
    private lateinit var progressLoading: ProgressBar
    private lateinit var tvNoDoctors: TextView

    private var services = listOf<ClinicService>()
    private var doctors = listOf<DoctorUser>()
    private var selectedServiceId: Long = -1L

    // Per-doctor state
    private val doctorSlots = mutableMapOf<Long, List<TimeSlot>>()
    private val doctorDates = mutableMapOf<Long, String>()

    // Current selection
    private var selectedDoctorId: Long = -1L
    private var selectedTime: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        spinnerService = findViewById(R.id.spinnerService)
        sectionDoctors = findViewById(R.id.sectionDoctors)
        layoutDoctors = findViewById(R.id.layoutDoctors)
        progressLoading = findViewById(R.id.progressLoading)
        tvNoDoctors = findViewById(R.id.tvNoDoctors)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        loadServices()
    }

    // ── SERVICES ──────────────────────────────────────────────────────────────

    private fun loadServices() {
        progressLoading.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val res = api.getServices()
                services = (res.body() ?: emptyList()).filter { it.active }
                setupServiceSpinner()
            } catch (e: Exception) {
                showError("Không tải được dịch vụ")
            } finally {
                progressLoading.visibility = View.GONE
            }
        }
    }

    private fun setupServiceSpinner() {
        val labels = mutableListOf("-- Vui lòng chọn dịch vụ --")
        labels.addAll(services.map { "${it.name} - ${formatPrice(it.price)} VND" })

        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, labels) {
            override fun isEnabled(position: Int) = position != 0
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getDropDownView(position, convertView, parent)
                (v as? TextView)?.setTextColor(
                    if (position == 0) ContextCompat.getColor(context, R.color.text_hint)
                    else ContextCompat.getColor(context, R.color.text_primary)
                )
                return v
            }
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerService.adapter = adapter

        spinnerService.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    selectedServiceId = -1L
                    sectionDoctors.visibility = View.GONE
                } else {
                    selectedServiceId = services[position - 1].id
                    loadDoctors(selectedServiceId)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // ── DOCTORS ───────────────────────────────────────────────────────────────

    private fun loadDoctors(serviceId: Long) {
        sectionDoctors.visibility = View.VISIBLE
        layoutDoctors.removeAllViews()
        tvNoDoctors.visibility = View.GONE
        progressLoading.visibility = View.VISIBLE
        selectedDoctorId = -1L
        selectedTime = ""

        lifecycleScope.launch {
            try {
                val res = api.getDoctorsByService(serviceId)
                doctors = res.body() ?: emptyList()

                if (doctors.isEmpty()) {
                    tvNoDoctors.visibility = View.VISIBLE
                } else {
                    // Init default date (nearest weekday)
                    val defaultDate = getNextWeekday()
                    doctors.forEach { doc -> doctorDates[doc.id] = defaultDate }

                    // Build doctor cards
                    doctors.forEach { doc -> buildDoctorCard(doc) }

                    // Load slots for each doctor
                    doctors.forEach { doc ->
                        loadSlotsForDoctor(doc.id, defaultDate)
                    }
                }
            } catch (e: Exception) {
                showError("Không tải được bác sĩ")
            } finally {
                progressLoading.visibility = View.GONE
            }
        }
    }

    private fun buildDoctorCard(doc: DoctorUser) {
        val card = layoutInflater.inflate(R.layout.item_doctor_card, layoutDoctors, false) as MaterialCardView
        card.tag = "doctor_${doc.id}"

        card.findViewById<TextView>(R.id.tvDoctorName).text = doc.fullName
        card.findViewById<TextView>(R.id.tvSpecialty).text = doc.specialty ?: "Bác sĩ Chuyên khoa"
        card.findViewById<TextView>(R.id.tvBio).text = doc.biography ?: "Bác sĩ giàu kinh nghiệm, tận tâm với nghề."
        card.findViewById<TextView>(R.id.tvClinicLocation).text = doc.clinicLocation ?: "Đang cập nhật"

        val spinnerDate = card.findViewById<Spinner>(R.id.spinnerDate)
        val layoutSlots = card.findViewById<FlexboxLayout>(R.id.layoutSlots)
        val tvSlotsEmpty = card.findViewById<TextView>(R.id.tvSlotsEmpty)
        val progressSlots = card.findViewById<ProgressBar>(R.id.progressSlots)
        val sectionConfirm = card.findViewById<LinearLayout>(R.id.sectionConfirm)
        val tvSelectedInfo = card.findViewById<TextView>(R.id.tvSelectedInfo)
        val etReason = card.findViewById<EditText>(R.id.etReason)
        val btnConfirm = card.findViewById<MaterialButton>(R.id.btnConfirm)

        // Date spinner
        val dateOptions = getWeekdayOptions(doc)
        val dateLabels = dateOptions.map { it.first }
        val dateSpinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dateLabels)
        dateSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDate.adapter = dateSpinnerAdapter

        spinnerDate.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val newDate = dateOptions[position].second
                doctorDates[doc.id] = newDate
                if (selectedDoctorId == doc.id) {
                    selectedTime = ""
                    sectionConfirm.visibility = View.GONE
                }
                loadSlotsForDoctor(doc.id, newDate)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        sectionConfirm.visibility = View.GONE

        // Confirm button
        btnConfirm.setOnClickListener {
            submitBooking(doc, etReason.text.toString(), sectionConfirm)
        }

        // Store references for slot update
        card.setTag(R.id.layoutSlots, layoutSlots)
        card.setTag(R.id.tvSlotsEmpty, tvSlotsEmpty)
        card.setTag(R.id.progressSlots, progressSlots)
        card.setTag(R.id.sectionConfirm, sectionConfirm)
        card.setTag(R.id.tvSelectedInfo, tvSelectedInfo)

        layoutDoctors.addView(card)
    }

    private fun loadSlotsForDoctor(doctorId: Long, date: String) {
        val card = layoutDoctors.findViewWithTag<MaterialCardView>("doctor_${doctorId}") ?: return
        val layoutSlots = card.getTag(R.id.layoutSlots) as? FlexboxLayout ?: return
        val tvSlotsEmpty = card.getTag(R.id.tvSlotsEmpty) as? TextView ?: return
        val progressSlots = card.getTag(R.id.progressSlots) as? ProgressBar ?: return

        progressSlots.visibility = View.VISIBLE
        layoutSlots.removeAllViews()
        tvSlotsEmpty.visibility = View.GONE

        lifecycleScope.launch {
            try {
                val res = api.getDoctorAvailability(doctorId, selectedServiceId, date)
                val slots = res.body() ?: emptyList()
                doctorSlots[doctorId] = slots
                renderSlots(card, doctorId, slots)
            } catch (e: Exception) {
                tvSlotsEmpty.text = "Không tải được lịch trống"
                tvSlotsEmpty.visibility = View.VISIBLE
            } finally {
                progressSlots.visibility = View.GONE
            }
        }
    }

    private fun renderSlots(card: MaterialCardView, doctorId: Long, slots: List<TimeSlot>) {
        val layoutSlots = card.getTag(R.id.layoutSlots) as? FlexboxLayout ?: return
        val tvSlotsEmpty = card.getTag(R.id.tvSlotsEmpty) as? TextView ?: return
        val sectionConfirm = card.getTag(R.id.sectionConfirm) as? LinearLayout ?: return
        val tvSelectedInfo = card.getTag(R.id.tvSelectedInfo) as? TextView ?: return

        layoutSlots.removeAllViews()

        if (slots.isEmpty()) {
            tvSlotsEmpty.text = "Bác sĩ đã kín lịch vào ngày này. Vui lòng chọn ngày khác."
            tvSlotsEmpty.visibility = View.VISIBLE
            return
        }
        tvSlotsEmpty.visibility = View.GONE

        slots.forEach { slot ->
            val btn = layoutInflater.inflate(R.layout.item_time_slot, layoutSlots, false) as MaterialButton
            btn.text = slot.time.take(5)
            btn.isEnabled = slot.available

            val isSelected = selectedDoctorId == doctorId && selectedTime == slot.time

            applySlotStyle(btn, isSelected, slot.available)

            if (slot.available) {
                btn.setOnClickListener {
                    selectedDoctorId = doctorId
                    selectedTime = slot.time

                    // Refresh all cards' slot styles
                    refreshAllSlotStyles()

                    // Show confirm section
                    val date = doctorDates[doctorId] ?: ""
                    val dateDisplay = date.split("-").reversed().joinToString("/")
                    tvSelectedInfo.text = "Giờ chọn: ${slot.time.take(5)}  •  $dateDisplay"
                    sectionConfirm.visibility = View.VISIBLE

                    // Hide confirm of other cards
                    for (i in 0 until layoutDoctors.childCount) {
                        val c = layoutDoctors.getChildAt(i) as? MaterialCardView ?: continue
                        val tag = c.tag as? String ?: continue
                        if (tag != "doctor_${doctorId}") {
                            (c.getTag(R.id.sectionConfirm) as? LinearLayout)?.visibility = View.GONE
                        }
                    }
                }
            }
            layoutSlots.addView(btn)
        }
    }

    private fun applySlotStyle(btn: MaterialButton, isSelected: Boolean, available: Boolean) {
        when {
            !available -> {
                btn.setBackgroundColor(ContextCompat.getColor(this, R.color.divider))
                btn.setTextColor(ContextCompat.getColor(this, R.color.text_hint))
            }
            isSelected -> {
                btn.setBackgroundColor(ContextCompat.getColor(this, R.color.brand_navy))
                btn.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
            else -> {
                btn.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
                btn.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
                btn.strokeColor = android.content.res.ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.divider)
                )
            }
        }
    }

    private fun refreshAllSlotStyles() {
        for (i in 0 until layoutDoctors.childCount) {
            val card = layoutDoctors.getChildAt(i) as? MaterialCardView ?: continue
            val tag = card.tag as? String ?: continue
            val docId = tag.removePrefix("doctor_").toLongOrNull() ?: continue
            val slots = doctorSlots[docId] ?: continue
            val layoutSlots = card.getTag(R.id.layoutSlots) as? FlexboxLayout ?: continue

            for (j in 0 until layoutSlots.childCount) {
                val btn = layoutSlots.getChildAt(j) as? MaterialButton ?: continue
                val slotTime = slots.getOrNull(j)?.time ?: continue
                val slot = slots.getOrNull(j) ?: continue
                val isSelected = selectedDoctorId == docId && selectedTime == slotTime
                applySlotStyle(btn, isSelected, slot.available)
            }
        }
    }

    // ── SUBMIT ────────────────────────────────────────────────────────────────

    private fun submitBooking(doc: DoctorUser, reason: String, sectionConfirm: LinearLayout) {
        val patientId = SessionManager.getUserId()
        if (patientId < 0) { showError("Vui lòng đăng nhập để đặt lịch"); return }
        if (selectedServiceId < 0 || selectedDoctorId < 0 || selectedTime.isEmpty()) {
            showError("Vui lòng chọn đầy đủ thông tin"); return
        }

        val date = doctorDates[doc.id] ?: run { showError("Vui lòng chọn ngày"); return }
        val startTime = "${date}T${selectedTime}"

        lifecycleScope.launch {
            try {
                val normalizedReason = reason.ifBlank { null }

                if (AgentInitializer.isInitialized()) {
                    val tracker = AgentInitializer.getEventTracker()
                    val accepted = tracker.trackFormSubmit(
                        userId = patientId,
                        formName = "BOOK_APPOINTMENT",
                        formText = listOfNotNull(
                            normalizedReason,
                            "doctor=${doc.fullName}",
                            "serviceId=$selectedServiceId",
                            "startTime=$startTime"
                        ).joinToString(" | ")
                    )
                    AgentInitializer.getAgentManager().syncPendingEvents()
                    if (!accepted) {
                        MaterialAlertDialogBuilder(this@BookingActivity)
                            .setTitle("DLP đã chặn yêu cầu")
                            .setMessage("Nội dung có dấu hiệu nhạy cảm/vi phạm chính sách. Vui lòng chỉnh sửa trước khi gửi.")
                            .setPositiveButton("Đã hiểu", null)
                            .show()
                        return@launch
                    }
                }

                val response = api.bookAppointment(
                    BookAppointmentRequest(
                        patientId = patientId,
                        doctorId = doc.id,
                        serviceId = selectedServiceId,
                        startTime = startTime,
                        reason = normalizedReason,
                        note = normalizedReason
                    )
                )
                val body = response.body()
                if (!response.isSuccessful || body?.success != true) {
                    MaterialAlertDialogBuilder(this@BookingActivity)
                        .setTitle("Đặt lịch thất bại")
                        .setMessage(body?.message ?: "Không thể tạo lịch hẹn. Vui lòng kiểm tra lại thông tin.")
                        .setPositiveButton("Đã hiểu", null)
                        .show()
                    return@launch
                }
                MaterialAlertDialogBuilder(this@BookingActivity)
                    .setTitle("✅ Đặt lịch thành công")
                    .setMessage("Lịch hẹn của bạn đã được xác nhận.\nThanh toán tại quầy khi đến khám.")
                    .setPositiveButton("Xem lịch hẹn") { _, _ ->
                        setResult(RESULT_OK)
                        finish()
                    }
                    .setCancelable(false)
                    .show()

                runCatching {
                    if (AgentInitializer.isInitialized()) {
                        AgentInitializer.getAgentManager().syncPendingEvents()
                    }
                }
            } catch (e: Exception) {
                MaterialAlertDialogBuilder(this@BookingActivity)
                    .setTitle("Đặt lịch thất bại")
                    .setMessage(e.message ?: "Vui lòng thử lại")
                    .setPositiveButton("Đã hiểu", null)
                    .show()
            }
        }
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    /** Trả về list (label, isoDate) ngày làm việc tiếp theo - giống web FE weekdayOptions */
    private fun getWeekdayOptions(doc: DoctorUser): List<Pair<String, String>> {
        val allowedDays = parseWorkingDays(doc.workingDays)
        val result = mutableListOf<Pair<String, String>>()
        val cursor = LocalDate.now()
        val today = cursor

        var d = cursor
        var attempts = 0
        while (result.size < 10 && attempts < 60) {
            attempts++
            val dow = d.dayOfWeek.name // "MONDAY","TUESDAY"...
            if (dow in allowedDays) {
                val isToday = d == today
                val label = if (isToday) "Hôm nay - ${d.format(DateTimeFormatter.ofPattern("dd/MM"))}"
                else "Thứ ${dowVietnamese(d.dayOfWeek.value)} - ${d.format(DateTimeFormatter.ofPattern("dd/MM"))}"
                result.add(Pair(label, d.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            }
            d = d.plusDays(1)
        }
        return result
    }

    private fun parseWorkingDays(raw: String?): Set<String> {
        val all = setOf("MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY")
        if (raw.isNullOrBlank()) return all
        val parsed = raw.split(",").map { it.trim().uppercase(Locale.ROOT) }.filter { it in all }.toSet()
        return if (parsed.isEmpty()) all else parsed
    }

    private fun dowVietnamese(dayOfWeek: Int) = when (dayOfWeek) {
        1 -> "2"; 2 -> "3"; 3 -> "4"; 4 -> "5"; 5 -> "6"; 6 -> "7"; 7 -> "CN"
        else -> dayOfWeek.toString()
    }

    private fun getNextWeekday(): String {
        var d = LocalDate.now()
        while (d.dayOfWeek.value >= 6) d = d.plusDays(1)
        return d.format(DateTimeFormatter.ISO_LOCAL_DATE)
    }

    private fun formatPrice(price: Double): String {
        return String.format(Locale("vi","VN"), "%,.0f", price)
    }

    private fun showError(msg: String) {
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show()
    }
}

// FlexboxLayout alias — sẽ dùng FlexboxLayoutManager hoặc WrapLayout
typealias FlexboxLayout = com.google.android.flexbox.FlexboxLayout
