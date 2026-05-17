package com.acare.clinic.ui.doctor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.FragmentDoctorStatisticBinding
import kotlinx.coroutines.launch

/**
 * DoctorStatisticFragment — Thống kê của bác sĩ.
 * Tương đương Statistic.jsx trong Frontend web.
 * Gọi: GET /api/doctor/statistics/dashboard
 */
class DoctorStatisticFragment : Fragment() {

    private var _binding: FragmentDoctorStatisticBinding? = null
    private val binding get() = _binding!!
    private val api by lazy { NetworkClient.create(ApiService::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoctorStatisticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadStatistics()
        binding.btnRefresh.setOnClickListener { loadStatistics() }
    }

    private fun loadStatistics() {
        setLoading(true)
        lifecycleScope.launch {
            try {
                val res = api.getDoctorDashboard()
                if (res.isSuccessful) {
                    val d = res.body() ?: return@launch
                    binding.tvTotalPatients.text = d.totalPatients.toString()
                    binding.tvTotalAppointments.text = d.totalAppointments.toString()
                    binding.tvPendingAppointments.text = d.pendingAppointments.toString()
                    binding.tvCompletedMonth.text = d.completedThisMonth.toString()
                    binding.tvTodayAppointments.text = d.todayAppointments.toString()
                } else {
                    showOfflineData()
                }
            } catch (e: Exception) {
                showOfflineData()
            } finally {
                setLoading(false)
            }
        }
    }

    /** Hiển thị số 0 khi không có kết nối */
    private fun showOfflineData() {
        binding.tvTotalPatients.text = "—"
        binding.tvTotalAppointments.text = "—"
        binding.tvPendingAppointments.text = "—"
        binding.tvCompletedMonth.text = "—"
        binding.tvTodayAppointments.text = "—"
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.contentLayout.visibility = if (loading) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
