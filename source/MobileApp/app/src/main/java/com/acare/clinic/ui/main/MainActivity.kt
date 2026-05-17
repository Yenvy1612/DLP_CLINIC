package com.acare.clinic.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

    /** Debounce: ngăn chuyển tab quá nhanh (< 300ms) */
    private var lastNavTime = 0L
    private val NAV_DEBOUNCE_MS = 300L

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
            }
        }

        // Setup với debounce navigation
        binding.bottomNavigation.setupWithNavController(navController)

        // Thêm debounce listener chống crash khi chuyển tab nhanh
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val now = System.currentTimeMillis()
            if (now - lastNavTime < NAV_DEBOUNCE_MS) {
                return@setOnItemSelectedListener false
            }
            lastNavTime = now

            // Nếu đang ở tab hiện tại → không làm gì (tránh recreate)
            if (navController.currentDestination?.id == item.itemId) {
                return@setOnItemSelectedListener true
            }

            // Navigation an toàn: bắt exception nếu fragment chưa sẵn sàng
            try {
                navController.navigate(item.itemId, null,
                    androidx.navigation.NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setRestoreState(true)
                        .setPopUpTo(
                            navController.graph.startDestinationId,
                            inclusive = false,
                            saveState = true
                        )
                        .build()
                )
                true
            } catch (e: Exception) {
                // Bỏ qua — tránh crash khi destination chưa sẵn sàng
                false
            }
        }

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
