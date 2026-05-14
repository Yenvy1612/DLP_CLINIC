package com.acare.clinic.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.acare.clinic.R
import com.acare.clinic.data.network.NetworkClient
import com.acare.clinic.databinding.ActivityMainBinding
import com.acare.clinic.ui.auth.LoginActivity
import com.acare.clinic.utils.SessionManager

/**
 * MainActivity — shell chứa Bottom Navigation + NavHostFragment.
 * Các tab: Home | Appointments | Medical Records | Profile
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)
    }

    fun logout() {
        SessionManager.clear()
        NetworkClient.clearCookies()
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}
