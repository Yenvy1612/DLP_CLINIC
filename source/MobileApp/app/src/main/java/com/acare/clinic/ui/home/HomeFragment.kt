package com.acare.clinic.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.acare.clinic.R
import com.acare.clinic.data.model.Appointment
import com.acare.clinic.data.network.ApiService
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.FragmentHomeBinding
import com.acare.clinic.ui.booking.BookingActivity
import com.acare.clinic.utils.SessionManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val api by lazy { NetworkClient.create(ApiService::class.java) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupQuickActions()
        loadUpcomingAppointments()

        binding.tvViewAll.setOnClickListener {
            findNavController().navigate(R.id.appointmentFragment)
        }
        binding.btnBookNow.setOnClickListener {
            startActivity(Intent(requireContext(), BookingActivity::class.java))
        }
        binding.btnPatientProfile.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }
    }

    private fun setupHeader() {
        val hour = LocalDateTime.now().hour
        val greeting = when {
            hour < 12 -> "Chào buổi sáng"
            hour < 18 -> "Chào buổi chiều"
            else -> "Chào buổi tối"
        }
        binding.tvGreeting.text = greeting
        val name = SessionManager.getUserName()
        binding.tvUserName.text = name
        binding.tvAvatarInitial.text = name.firstOrNull()?.uppercaseChar()?.toString() ?: "A"
    }

    private fun setupQuickActions() {
        binding.actionBook.setOnClickListener {
            startActivity(Intent(requireContext(), BookingActivity::class.java))
        }
        binding.actionRecords.setOnClickListener {
            findNavController().navigate(R.id.recordFragment)
        }
        binding.actionDoctors.setOnClickListener {
            // TODO: navigate to doctors list
        }
        binding.actionServices.setOnClickListener {
            findNavController().navigate(R.id.servicesFragment)
        }
    }

    private fun loadUpcomingAppointments() {
        val userId = SessionManager.getUserId()
        if (userId < 0) return

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val res = api.getPendingAppointments(userId)
                if (res.isSuccessful) {
                    val appointments = enrichAppointments(res.body() ?: emptyList())
                    displayAppointments(appointments.take(3))
                }
            } catch (_: Exception) {
                Snackbar.make(binding.root, "Không thể tải lịch hẹn", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun enrichAppointments(input: List<Appointment>): List<Appointment> {
        if (input.isEmpty()) return input

        return coroutineScope {
            val doctorIds = input.mapNotNull { it.doctorId }.distinct()
            val serviceIds = input.mapNotNull { it.serviceId }.distinct()

            val doctorMap = doctorIds.map { doctorId ->
                async {
                    doctorId to runCatching { api.getUserById(doctorId).body()?.fullName }.getOrNull()
                }
            }.awaitAll().toMap()

            val serviceMap = serviceIds.map { serviceId ->
                async {
                    serviceId to runCatching { api.getServiceById(serviceId).body()?.name }.getOrNull()
                }
            }.awaitAll().toMap()

            input.map { apt ->
                apt.copy(
                    doctorName = apt.doctorName ?: doctorMap[apt.doctorId],
                    serviceName = apt.serviceName ?: serviceMap[apt.serviceId]
                )
            }
        }
    }

    private fun displayAppointments(appointments: List<Appointment>) {
        if (appointments.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvUpcomingAppointments.visibility = View.GONE
            return
        }
        binding.emptyState.visibility = View.GONE
        binding.rvUpcomingAppointments.visibility = View.VISIBLE
        binding.rvUpcomingAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUpcomingAppointments.adapter = AppointmentCardAdapter(appointments)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
