package com.acare.clinic.ui.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.FragmentAppointmentBinding
import com.acare.clinic.ui.home.AppointmentCardAdapter
import com.acare.clinic.utils.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class AppointmentFragment : Fragment() {

    private var _binding: FragmentAppointmentBinding? = null
    private val binding get() = _binding!!
    private val api by lazy { NetworkClient.create(ApiService::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabs()
        loadPending()
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> loadPending()
                    1 -> loadHistory()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadPending() {
        val userId = SessionManager.getUserId()
        setLoading(true)
        lifecycleScope.launch {
            try {
                val res = api.getPendingAppointments(userId)
                val list = res.body() ?: emptyList()
                if (list.isEmpty()) showEmpty() else {
                    binding.emptyState.visibility = View.GONE
                    binding.rvAppointments.visibility = View.VISIBLE
                    binding.rvAppointments.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvAppointments.adapter = AppointmentCardAdapter(list) { apt ->
                        confirmCancel(apt.id)
                    }
                }
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Lỗi tải dữ liệu", Snackbar.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun loadHistory() {
        val userId = SessionManager.getUserId()
        setLoading(true)
        lifecycleScope.launch {
            try {
                val res = api.getCompletedAppointments(userId)
                val list = res.body() ?: emptyList()
                if (list.isEmpty()) showEmpty() else {
                    binding.emptyState.visibility = View.GONE
                    binding.rvAppointments.visibility = View.VISIBLE
                    binding.rvAppointments.layoutManager = LinearLayoutManager(requireContext())
                    binding.rvAppointments.adapter = AppointmentCardAdapter(list)
                }
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Lỗi tải dữ liệu", Snackbar.LENGTH_SHORT).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun confirmCancel(appointmentId: Long) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hủy lịch hẹn")
            .setMessage("Bạn có chắc muốn hủy lịch hẹn này không?")
            .setNegativeButton("Không") { d, _ -> d.dismiss() }
            .setPositiveButton("Có, hủy") { _, _ ->
                lifecycleScope.launch {
                    try {
                        api.cancelAppointment(appointmentId)
                        Snackbar.make(binding.root, "Đã hủy lịch hẹn", Snackbar.LENGTH_SHORT).show()
                        loadPending()
                    } catch (e: Exception) {
                        Snackbar.make(binding.root, "Hủy thất bại", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    private fun showEmpty() {
        binding.emptyState.visibility = View.VISIBLE
        binding.rvAppointments.visibility = View.GONE
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
