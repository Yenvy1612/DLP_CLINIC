package com.acare.clinic.ui.record

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.acare.clinic.data.model.MedicalRecord
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.FragmentMedicalRecordBinding
import com.acare.clinic.utils.SessionManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MedicalRecordFragment : Fragment() {

    private var _binding: FragmentMedicalRecordBinding? = null
    private val binding get() = _binding!!
    private val api by lazy { NetworkClient.create(ApiService::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMedicalRecordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadRecords()
        binding.swipeRefresh.setOnRefreshListener { loadRecords() }
    }

    private fun loadRecords() {
        val userId = SessionManager.getUserId()
        binding.progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val res = api.getMedicalRecords(userId)
                val list = res.body() ?: emptyList()
                if (list.isEmpty()) {
                    binding.emptyState.visibility = View.VISIBLE
                    binding.rvRecords.visibility = View.GONE
                } else {
                    binding.emptyState.visibility = View.GONE
                    binding.rvRecords.visibility = View.VISIBLE
                    binding.rvRecords.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvRecords.adapter = MedicalRecordAdapter(list)
                }
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Lỗi tải hồ sơ bệnh án", Snackbar.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
