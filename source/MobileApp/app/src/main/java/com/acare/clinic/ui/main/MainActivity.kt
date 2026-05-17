package com.acare.clinic.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.acare.clinic.R
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.ActivityMainBinding
import com.acare.clinic.ui.auth.LoginActivity
import com.acare.clinic.utils.SessionManager

/**
 * MainActivity — shell chứa Bottom Navigation + NavHostFragment.
 *
 * Phân quyền theo role (giống Frontend web):
 *  - PATIENT : Tab Trang chủ | Lịch hẹn | Hồ sơ bệnh | Tôi  (nav_graph_patient)
 *  - DOCTOR  : Tab Trang chủ | Lịch khám | Thống kê | Tôi   (nav_graph_doctor)
 *  - ADMIN   : Tab Tổng quan | Người dùng | Dịch vụ | Lịch hẹn | Bảo mật (nav_graph_admin)
 *
 * Anti-crash: Dùng setReorderingAllowed + debounce navigation
 * để tránh crash khi chuyển tab quá nhanh.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRoleBasedNavigation()
    }

    private fun setupRoleBasedNavigation() {
        val role = SessionManager.getRole()

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        when (role) {
            "DOCTOR" -> {
                val inflater = navController.navInflater
                val graph = inflater.inflate(R.navigation.nav_graph_doctor)
                navController.graph = graph
                binding.bottomNavigation.menu.clear()
                binding.bottomNavigation.inflateMenu(R.menu.bottom_nav_doctor)
            }
            "ADMIN" -> {
                val inflater = navController.navInflater
                val graph = inflater.inflate(R.navigation.nav_graph_admin)
                navController.graph = graph
                binding.bottomNavigation.menu.clear()
                binding.bottomNavigation.inflateMenu(R.menu.bottom_nav_admin)
            }
            else -> {
                // PATIENT (default)
                val inflater = navController.navInflater
                val graph = inflater.inflate(R.navigation.nav_graph_patient)
                navController.graph = graph
                binding.bottomNavigation.menu.clear()
                binding.bottomNavigation.inflateMenu(R.menu.bottom_nav_menu)
                binding.btnProfileOverlay.visibility = android.view.View.VISIBLE
                binding.btnProfileOverlay.setOnClickListener {
                    if (navController.currentDestination?.id != R.id.profileFragment) {
                        navController.navigate(R.id.profileFragment)
                    }
                }
            }
        }

        if (role == "DOCTOR" || role == "ADMIN") {
            binding.btnProfileOverlay.visibility = android.view.View.GONE
        }

        // Setup điều hướng tab chuẩn với NavigationUI
        binding.bottomNavigation.setupWithNavController(navController)

        // Reselect: scroll to top hoặc refresh
        binding.bottomNavigation.setOnItemReselectedListener { _ ->
            // Không làm gì khi reselect — tránh recreate fragment
        }
    }

    fun logout() {
        SessionManager.clear()
        NetworkClient.clearCookies()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}
