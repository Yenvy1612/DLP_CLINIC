package com.acare.clinic.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acare.clinic.R
import com.acare.clinic.data.model.ClinicService
import com.acare.clinic.data.model.CreateServiceRequest
import com.acare.clinic.data.model.UpdateServiceRequest
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.FragmentAdminServicesBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

/**
 * AdminServicesFragment — Quản lý dịch vụ (Admin).
 * Tương đương Services.jsx trong Frontend web.
 * Gọi: GET /api/services, POST /api/services, PUT /api/services/{id}, DELETE /api/services/{id}
 */
class AdminServicesFragment : Fragment() {

    private var _binding: FragmentAdminServicesBinding? = null
    private val binding get() = _binding!!
    private val api by lazy { NetworkClient.create(ApiService::class.java) }
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadServices()

        binding.etSearch.doAfterTextChanged { text ->
            searchJob?.cancel()
            searchJob = viewLifecycleOwner.lifecycleScope.launch {
                delay(500)
                val query = text.toString().trim()
                if (query.isEmpty()) loadServices() else searchServices(query)
            }
        }

        binding.btnRefresh.setOnClickListener {
            binding.etSearch.text?.clear()
            loadServices()
        }

        // FAB to add new service (if exists in layout)
        try {
            val fab = binding.root.findViewById<View>(R.id.fabAddService)
            fab?.setOnClickListener { showAddServiceDialog() }
        } catch (_: Exception) {}
    }

    private fun showAddServiceDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_service, null)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Thêm dịch vụ mới")
            .setView(dialogView)
            .setNegativeButton("Hủy") { d, _ -> d.dismiss() }
            .setPositiveButton("Thêm") { _, _ ->
                val name = dialogView.findViewById<TextInputEditText>(R.id.etServiceName).text.toString().trim()
                val priceStr = dialogView.findViewById<TextInputEditText>(R.id.etServicePrice).text.toString().trim()
                val department = dialogView.findViewById<TextInputEditText>(R.id.etDepartment).text.toString().trim()
                val description = dialogView.findViewById<TextInputEditText>(R.id.etDescription).text.toString().trim()

                if (name.isEmpty() || priceStr.isEmpty()) {
                    Snackbar.make(binding.root, "Vui lòng nhập tên và giá dịch vụ", Snackbar.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val price = priceStr.toDoubleOrNull() ?: 0.0

                val request = CreateServiceRequest(
                    name = name,
                    price = price,
                    description = description.ifEmpty { null },
                    department = department.ifEmpty { null }
                )

                setLoading(true)
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val res = api.createService(request)
                        if (_binding != null) {
                            if (res.isSuccessful) {
                                Snackbar.make(binding.root, "Thêm dịch vụ thành công", Snackbar.LENGTH_SHORT).show()
                                loadServices()
                            } else {
                                Snackbar.make(binding.root, "Lỗi khi thêm dịch vụ", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        if (_binding != null) {
                            Snackbar.make(binding.root, "Lỗi: ${e.message}", Snackbar.LENGTH_LONG).show()
                        }
                    } finally {
                        if (_binding != null) setLoading(false)
                    }
                }
            }
            .show()
    }

    fun showEditServiceDialog(service: ClinicService) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_service, null)

        dialogView.findViewById<TextInputEditText>(R.id.etServiceName).setText(service.name)
        dialogView.findViewById<TextInputEditText>(R.id.etServicePrice).setText(service.price.toString())
        dialogView.findViewById<TextInputEditText>(R.id.etDepartment).setText(service.department ?: "")
        dialogView.findViewById<TextInputEditText>(R.id.etDescription).setText(service.description ?: "")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Sửa dịch vụ")
            .setView(dialogView)
            .setNegativeButton("Hủy") { d, _ -> d.dismiss() }
            .setNeutralButton("Xóa") { _, _ -> confirmDeleteService(service) }
            .setPositiveButton("Cập nhật") { _, _ ->
                val name = dialogView.findViewById<TextInputEditText>(R.id.etServiceName).text.toString().trim()
                val priceStr = dialogView.findViewById<TextInputEditText>(R.id.etServicePrice).text.toString().trim()
                val department = dialogView.findViewById<TextInputEditText>(R.id.etDepartment).text.toString().trim()
                val description = dialogView.findViewById<TextInputEditText>(R.id.etDescription).text.toString().trim()

                val request = UpdateServiceRequest(
                    name = name.ifEmpty { null },
                    price = priceStr.toDoubleOrNull(),
                    description = description.ifEmpty { null },
                    department = department.ifEmpty { null },
                    specialtyId = service.specialtyId,
                    active = service.active
                )

                setLoading(true)
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val res = api.updateService(service.id, request)
                        if (_binding != null) {
                            if (res.isSuccessful) {
                                Snackbar.make(binding.root, "Cập nhật dịch vụ thành công", Snackbar.LENGTH_SHORT).show()
                                loadServices()
                            } else {
                                Snackbar.make(binding.root, "Lỗi khi cập nhật", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        if (_binding != null) Snackbar.make(binding.root, "Lỗi: ${e.message}", Snackbar.LENGTH_LONG).show()
                    } finally {
                        if (_binding != null) setLoading(false)
                    }
                }
            }
            .show()
    }

    private fun confirmDeleteService(service: ClinicService) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Xóa dịch vụ")
            .setMessage("Bạn có chắc chắn muốn xóa dịch vụ \"${service.name}\"?")
            .setNegativeButton("Hủy") { d, _ -> d.dismiss() }
            .setPositiveButton("Xóa") { _, _ ->
                setLoading(true)
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val res = api.deleteService(service.id)
                        if (_binding != null) {
                            if (res.isSuccessful) {
                                Snackbar.make(binding.root, "Đã xóa dịch vụ", Snackbar.LENGTH_SHORT).show()
                                loadServices()
                            } else {
                                Snackbar.make(binding.root, "Lỗi khi xóa", Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        if (_binding != null) Snackbar.make(binding.root, "Lỗi: ${e.message}", Snackbar.LENGTH_LONG).show()
                    } finally {
                        if (_binding != null) setLoading(false)
                    }
                }
            }
            .show()
    }

    private fun loadServices() {
        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.getServices()
                val list = res.body() ?: emptyList()
                if (_binding != null) displayServices(list)
            } catch (e: Exception) {
                if (_binding != null) Snackbar.make(binding.root, "Không thể tải dịch vụ", Snackbar.LENGTH_SHORT).show()
            } finally {
                if (_binding != null) setLoading(false)
            }
        }
    }

    private fun searchServices(keyword: String) {
        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.searchServices(keyword)
                val list = res.body() ?: emptyList()
                if (_binding != null) displayServices(list)
            } catch (e: Exception) {
                if (_binding != null) Snackbar.make(binding.root, "Lỗi tìm kiếm", Snackbar.LENGTH_SHORT).show()
            } finally {
                if (_binding != null) setLoading(false)
            }
        }
    }

    private fun displayServices(services: List<ClinicService>) {
        binding.tvServiceCount.text = "${services.size} dịch vụ"
        if (services.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvServices.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvServices.visibility = View.VISIBLE
            binding.rvServices.layoutManager = LinearLayoutManager(requireContext())
            binding.rvServices.adapter = ServiceCardAdapter(services, this)
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/** Adapter nội bộ cho danh sách dịch vụ */
class ServiceCardAdapter(
    private val items: List<ClinicService>,
    private val fragment: AdminServicesFragment
) : RecyclerView.Adapter<ServiceCardAdapter.VH>() {

    private val fmt = NumberFormat.getNumberInstance(Locale("vi", "VN"))

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvServiceName)
        val tvPrice: TextView = view.findViewById(R.id.tvServicePrice)
        val tvDept: TextView = view.findViewById(R.id.tvDepartment)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_card, parent, false)
        return VH(v)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val svc = items[position]
        holder.tvName.text = svc.name
        holder.tvPrice.text = "${fmt.format(svc.price)} đ"
        holder.tvDept.text = svc.department ?: "—"
        holder.tvStatus.text = if (svc.active) "Hoạt động" else "Tạm dừng"
        holder.tvStatus.setTextColor(
            holder.itemView.context.getColor(
                if (svc.active) R.color.success else R.color.error
            )
        )

        // Long press to edit/delete
        holder.itemView.setOnLongClickListener {
            fragment.showEditServiceDialog(svc)
            true
        }

        // Single tap also opens edit
        holder.itemView.setOnClickListener {
            fragment.showEditServiceDialog(svc)
        }
    }
}
