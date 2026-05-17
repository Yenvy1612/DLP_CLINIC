package com.acare.clinic.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acare.clinic.R
import com.acare.clinic.data.model.DlpLog
import com.acare.clinic.data.model.SecurityDashboardResponse
import com.acare.clinic.data.model.SecurityEventResponse
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.FragmentAdminSecurityBinding
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AdminSecurityFragment : Fragment() {

    private var _binding: FragmentAdminSecurityBinding? = null
    private val binding get() = _binding!!
    private val api by lazy { NetworkClient.create(ApiService::class.java) }
    
    private var isDlpMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminSecurityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.rvLogs.layoutManager = LinearLayoutManager(requireContext())
        
        binding.chipGroupType.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.contains(R.id.chipDlp)) {
                isDlpMode = true
                binding.dashboardCard.visibility = View.GONE
                loadDlpLogs()
            } else {
                isDlpMode = false
                loadSecurityDashboard()
                loadSecurityEvents()
            }
        }
        
        binding.swipeRefresh.setOnRefreshListener {
            if (isDlpMode) loadDlpLogs() else {
                loadSecurityDashboard()
                loadSecurityEvents()
            }
        }
        
        binding.btnRefresh.setOnClickListener {
            if (isDlpMode) loadDlpLogs() else {
                loadSecurityDashboard()
                loadSecurityEvents()
            }
        }
        
        loadSecurityDashboard()
        loadSecurityEvents()
    }
    
    private fun loadSecurityDashboard() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.getSecurityDashboard()
                if (_binding != null && res.isSuccessful) {
                    val dashboard = res.body()?.data
                    if (dashboard != null) {
                        displayDashboard(dashboard)
                    }
                }
            } catch (_: Exception) {
                // Dashboard is optional, don't show error
            }
        }
    }
    
    private fun displayDashboard(d: SecurityDashboardResponse) {
        binding.dashboardCard.visibility = View.VISIBLE
        binding.tvTotalEvents.text = d.totalEvents24h.toString()
        binding.tvCriticalEvents.text = d.criticalEvents24h.toString()
        binding.tvHighEvents.text = d.highEvents24h.toString()
        binding.tvRevokedSessions.text = d.revokedSessions24h.toString()
        
        // Top event types
        val topTypes = d.topEventTypes
        if (!topTypes.isNullOrEmpty()) {
            val topTypesText = topTypes.entries.joinToString("\n") { (type, count) ->
                "Ã¢â‚¬Â¢ $type: $count"
            }
            binding.tvTopEventTypes.text = topTypesText
            binding.tvTopEventTypes.visibility = View.VISIBLE
            binding.tvTopEventTypesLabel.visibility = View.VISIBLE
        } else {
            binding.tvTopEventTypes.visibility = View.GONE
            binding.tvTopEventTypesLabel.visibility = View.GONE
        }
    }
    
    private fun loadSecurityEvents() {
        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.getSecurityEvents(page = 0, size = 50)
                if (_binding != null) {
                    val events = res.body()?.data?.content ?: emptyList()
                    if (events.isEmpty()) {
                        showEmpty(true)
                    } else {
                        showEmpty(false)
                        binding.rvLogs.adapter = SecurityEventAdapter(events)
                    }
                }
            } catch (e: Exception) {
                if (_binding != null) {
                    Snackbar.make(binding.root, "KhÃƒÂ´ng thÃ¡Â»Æ’ tÃ¡ÂºÂ£i nhÃ¡ÂºÂ­t kÃƒÂ½ bÃ¡ÂºÂ£o mÃ¡ÂºÂ­t", Snackbar.LENGTH_SHORT).show()
                }
            } finally {
                if (_binding != null) {
                    setLoading(false)
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }
    }
    
    private fun loadDlpLogs() {
        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.getDlpLogs(page = 0, size = 50)
                if (_binding != null) {
                    val logs = res.body()?.data?.content ?: emptyList()
                    if (logs.isEmpty()) {
                        showEmpty(true)
                    } else {
                        showEmpty(false)
                        binding.rvLogs.adapter = DlpLogAdapter(logs)
                    }
                }
            } catch (e: Exception) {
                if (_binding != null) {
                    Snackbar.make(binding.root, "KhÃƒÂ´ng thÃ¡Â»Æ’ tÃ¡ÂºÂ£i nhÃ¡ÂºÂ­t kÃƒÂ½ DLP", Snackbar.LENGTH_SHORT).show()
                }
            } finally {
                if (_binding != null) {
                    setLoading(false)
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }
    }
    
    private fun showEmpty(isEmpty: Boolean) {
        binding.emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvLogs.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun setLoading(loading: Boolean) {
        if (!binding.swipeRefresh.isRefreshing) {
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// Adapters
class SecurityEventAdapter(private val items: List<SecurityEventResponse>) : RecyclerView.Adapter<SecurityEventAdapter.VH>() {
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvEventType: TextView = view.findViewById(R.id.tvEventType)
        val chipSeverity: Chip = view.findViewById(R.id.chipSeverity)
        val tvIpAddress: TextView = view.findViewById(R.id.tvIpAddress)
        val tvUri: TextView = view.findViewById(R.id.tvUri)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val tvAction: TextView = view.findViewById(R.id.tvAction)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_security_event, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvEventType.text = item.eventType
        holder.chipSeverity.text = item.severity
        holder.chipSeverity.setChipBackgroundColorResource(
            when (item.severity.uppercase()) {
                "CRITICAL" -> R.color.error
                "HIGH" -> R.color.warning
                "MEDIUM" -> R.color.primary
                else -> R.color.success
            }
        )
        holder.tvIpAddress.text = "IP: ${item.ipAddress ?: "N/A"}"
        holder.tvUri.text = "${item.httpMethod ?: "N/A"} ${item.requestUri ?: "N/A"}"
        holder.tvDescription.text = item.description ?: "KhÃƒÂ´ng cÃƒÂ³ chi tiÃ¡ÂºÂ¿t"
        holder.tvAction.text = "HÃƒÂ nh Ã„â€˜Ã¡Â»â„¢ng: ${item.actionTaken ?: "N/A"}"
        holder.tvTime.text = item.occurredAt
    }
}

class DlpLogAdapter(private val items: List<DlpLog>) : RecyclerView.Adapter<DlpLogAdapter.VH>() {
    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvActionType: TextView = view.findViewById(R.id.tvActionType)
        val chipSeverity: Chip = view.findViewById(R.id.chipSeverity)
        val tvMachine: TextView = view.findViewById(R.id.tvMachine)
        val tvFile: TextView = view.findViewById(R.id.tvFile)
        val tvDetails: TextView = view.findViewById(R.id.tvDetails)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_dlp_log, parent, false))
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvActionType.text = friendlyEvent(item.eventType, item.action)
        holder.chipSeverity.text = item.severity ?: "INFO"
        holder.chipSeverity.setChipBackgroundColorResource(
            when (item.severity?.uppercase()) {
                "CRITICAL" -> R.color.error
                "HIGH" -> R.color.warning
                "MEDIUM" -> R.color.primary
                else -> R.color.success
            }
        )

        val source = friendlySource(item.sourceType, item.platform)
        holder.tvMachine.text = "Thiet bi: ${item.deviceId ?: "N/A"} | Nen tang: ${item.platform ?: "N/A"}"
        holder.tvFile.text = "Vi pham: ${friendlyViolation(item.violationType)} | Hanh dong: ${friendlyAction(item.action)}"

        val snippet = item.contentSnippet?.takeIf { it.isNotBlank() }
        holder.tvDetails.text = buildPreciseReason(item, snippet)

        val userLabel = item.username ?: item.userId?.toString() ?: "N/A"
        holder.tvStatus.text = "Nguon: $source | User: $userLabel"
        holder.tvTime.text = item.timestamp ?: "N/A"
    }

    private fun friendlySource(sourceType: String?, platform: String?): String {
        val s = sourceType?.uppercase()
        return when {
            s == "PHONE" -> "Dien thoai"
            s == "ANDROID_AGENT" -> "Agent mobile"
            s == "WEB" -> "Web"
            platform?.uppercase() == "ANDROID" -> "Dien thoai"
            else -> sourceType ?: "N/A"
        }
    }

    private fun friendlyViolation(violationType: String?): String {
        val v = violationType?.uppercase() ?: return "N/A"
        return when {
            v.contains("CCCD") -> "CCCD/ID nhay cam"
            v.contains("PHONE") -> "So dien thoai"
            v.contains("EMAIL") -> "Email"
            v.contains("SENSITIVE") || v.contains("HIV") || v.contains("KEYWORD") -> "Tu khoa nhay cam"
            else -> violationType
        }
    }

    private fun friendlyAction(action: String?): String {
        return when (action?.uppercase()) {
            "INPUT_SCAN" -> "Quet dau vao"
            "OUTPUT_SCAN" -> "Quet dau ra"
            "SUBMIT_FORM" -> "Gui form"
            "COPY" -> "Copy"
            "EXPORT" -> "Export"
            else -> action ?: "N/A"
        }
    }

    private fun friendlyEvent(eventType: String?, action: String?): String {
        return when (eventType?.uppercase()) {
            "FORM_DLP_MATCHED" -> "Vi pham khi dien form"
            "COPY_PATIENT_DATA" -> "Vi pham khi copy"
            "EXPORT_BLOCKED" -> "Chan export"
            "INPUT_SCAN" -> "Quet du lieu dau vao"
            "OUTPUT_SCAN" -> "Quet du lieu dau ra"
            else -> friendlyAction(action)
        }
    }

        private fun buildPreciseReason(item: DlpLog, snippet: String?): String {
        val endpoint = item.details
            ?.substringAfter("tai ", "")
            ?.substringBefore(" -", "")
            ?.trim()
            ?.takeIf { it.isNotBlank() }
        val whereText = endpoint?.let { " tai $it" } ?: ""
        val payload = snippet ?: "khong co"
        val trigger = extractTrigger(item, payload)

        return when (item.action?.uppercase()) {
            "INPUT_SCAN", "SUBMIT_FORM" -> "Dien noi dung vi pham ($trigger)$whereText: $payload"
            "COPY" -> "Copy du lieu vi pham ($trigger)$whereText: $payload"
            "EXPORT" -> "Export du lieu vi pham ($trigger)$whereText: $payload"
            "OUTPUT_SCAN" -> "Tra ve du lieu vi pham ($trigger)$whereText: $payload"
            else -> "Noi dung vi pham ($trigger)$whereText: $payload"
        }
    }

    private fun extractTrigger(item: DlpLog, payload: String): String {
        val violation = item.violationType?.trim().orEmpty()
        if (violation.isNotBlank()) return violation

        val raw = payload.removePrefix("[").removeSuffix("]")
        val first = raw.split(",").firstOrNull()?.trim().orEmpty()
        return if (first.isNotBlank()) first else "UNKNOWN"
    }
}