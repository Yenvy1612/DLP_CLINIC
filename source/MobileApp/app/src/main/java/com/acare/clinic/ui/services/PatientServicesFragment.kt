package com.acare.clinic.ui.services

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acare.clinic.R
import com.acare.clinic.data.model.ClinicService
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.FragmentPatientServicesBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class PatientServicesFragment : Fragment() {

    private var _binding: FragmentPatientServicesBinding? = null
    private val binding get() = _binding!!
    private val api by lazy { NetworkClient.create(ApiService::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientServicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvServices.layoutManager = LinearLayoutManager(requireContext())
        loadServices()
    }

    private fun loadServices() {
        binding.progressBar.visibility = View.VISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.getServices()
                val services = (res.body() ?: emptyList()).filter { it.active }
                binding.tvCount.text = "${services.size} dịch vụ"
                binding.tvEmpty.visibility = if (services.isEmpty()) View.VISIBLE else View.GONE
                binding.rvServices.adapter = PatientServiceAdapter(services)
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Không tải được danh sách dịch vụ", Snackbar.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

private class PatientServiceAdapter(
    private val items: List<ClinicService>
) : RecyclerView.Adapter<PatientServiceAdapter.VH>() {
    private val fmt = NumberFormat.getNumberInstance(Locale("vi", "VN"))

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvServiceName)
        val tvDepartment: TextView = view.findViewById(R.id.tvDepartment)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val tvPrice: TextView = view.findViewById(R.id.tvServicePrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service_card, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvName.text = item.name
        holder.tvDepartment.text = item.department ?: "Tổng quát"
        holder.tvStatus.text = if (item.active) "Hoạt động" else "Tạm dừng"
        holder.tvPrice.text = "${fmt.format(item.price)} đ"
    }
}
