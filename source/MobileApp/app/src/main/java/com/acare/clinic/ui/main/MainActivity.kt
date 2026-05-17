package com.acare.clinic.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.onNavDestinationSelected
import com.acare.clinic.R
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.ActivityMainBinding
import com.acare.clinic.ui.auth.LoginActivity
import com.acare.clinic.utils.SessionManager

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
                navController.graph = navController.navInflater.inflate(R.navigation.nav_graph_doctor)
                binding.bottomNavigation.menu.clear()
                binding.bottomNavigation.inflateMenu(R.menu.bottom_nav_doctor)
                binding.btnProfileOverlay.visibility = android.view.View.GONE
            }
            "ADMIN" -> {
                navController.graph = navController.navInflater.inflate(R.navigation.nav_graph_admin)
                binding.bottomNavigation.menu.clear()
                binding.bottomNavigation.inflateMenu(R.menu.bottom_nav_admin)
                binding.btnProfileOverlay.visibility = android.view.View.GONE
            }
            else -> {
                navController.graph = navController.navInflater.inflate(R.navigation.nav_graph_patient)
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

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            try {
                val options = navOptions {
                    launchSingleTop = true
                    restoreState = true
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
                navController.navigate(item.itemId, null, options)
                true
            } catch (_: Exception) {
                item.onNavDestinationSelected(navController)
            }
        }

        binding.bottomNavigation.setOnItemReselectedListener { _ -> }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.menu.findItem(destination.id)?.isChecked = true
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