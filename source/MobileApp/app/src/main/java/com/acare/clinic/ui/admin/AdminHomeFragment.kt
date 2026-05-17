package com.acare.clinic.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.FragmentAdminHomeBinding
import com.acare.clinic.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

import androidx.navigation.fragment.findNavController
import com.acare.clinic.R

/**
 * AdminHomeFragment — Dashboard tổng quan cho ADMIN.
 * Tương đương Dashboard.jsx trong Frontend web.
 * Gọi: GET /api/admin/dashboard/summary
 */
class AdminHomeFragment : Fragment() {

    private var _binding: FragmentAdminHomeBinding? = null
    private val binding get() = _binding!!
    private val api by lazy { NetworkClient.create(ApiService::class.java) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvAdminName.text = "Quản trị viên: ${SessionManager.getUserName()}"
        loadSummary()
        binding.btnRefresh.setOnClickListener { loadSummary() }
        
        binding.btnAdminProfile.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }
    }

    private fun loadSummary() {
        setLoading(true)
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.getAdminSummary()
                if (_binding != null) {
                    if (res.isSuccessful) {
                        val s = res.body() ?: return@launch
                        
                        binding.tvTotalUsers.text = s.userCount.toString()
                        
                        val monthYear = "tháng ${s.month}/${s.year}"
                        
                        binding.tvTopDoctorTitle.text = "Bác sĩ nổi bật $monthYear"
                        binding.tvTopDoctorName.text = s.topDoctorName ?: "Chưa có dữ liệu"
                        binding.tvTopDoctorCount.text = if (s.topDoctorDoneCount > 0) "${s.topDoctorDoneCount} lịch đã khám" else ""
                        
                        binding.tvTopServiceTitle.text = "Dịch vụ nổi bật $monthYear"
                        binding.tvTopServiceName.text = s.topServiceName ?: "Chưa có dữ liệu"
                        binding.tvTopServiceCount.text = if (s.topServiceDoneCount > 0) "${s.topServiceDoneCount} lượt sử dụng" else ""
                        
                        binding.tvRevenueTitle.text = "Doanh thu $monthYear"
                        
                        val format = NumberFormat.getNumberInstance(Locale("vi", "VN"))
                        binding.tvMonthRevenue.text = "${format.format(s.monthRevenue)} đ"
                    } else {
                        showDash()
                    }
                }
            } catch (e: Exception) {
                if (_binding != null) showDash()
            } finally {
                if (_binding != null) setLoading(false)
            }
        }
    }

    private fun showDash() {
        binding.tvTotalUsers.text = "—"
        binding.tvTopDoctorName.text = "—"
        binding.tvTopDoctorCount.text = ""
        binding.tvTopServiceName.text = "—"
        binding.tvTopServiceCount.text = ""
        binding.tvMonthRevenue.text = "—"
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.contentScroll.visibility = if (loading) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
