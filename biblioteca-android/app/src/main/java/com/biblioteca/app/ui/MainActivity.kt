package com.biblioteca.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.biblioteca.app.R
import com.biblioteca.app.databinding.ActivityMainBinding
import com.biblioteca.app.ui.login.LoginActivity
import com.biblioteca.app.utils.SessionManager
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        setSupportActionBar(binding.appBarMain.toolbar)

        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_libros, R.id.nav_prestamos, R.id.nav_usuarios),
            binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        // Info usuario en el header del drawer
        val headerView = binding.navView.getHeaderView(0)
        val tvUsername = headerView.findViewById<android.widget.TextView>(R.id.tvUsername)
        val tvRol = headerView.findViewById<android.widget.TextView>(R.id.tvRol)
        tvUsername.text = session.getUsername()
        tvRol.text = session.getRol()

        // Ocultar menu usuarios si no es ADMIN
        val menuUsuarios = binding.navView.menu.findItem(R.id.nav_usuarios)
        menuUsuarios.isVisible = session.getRol() == "ROLE_ADMIN"

        // Logout
        val menuLogout = binding.navView.menu.findItem(R.id.nav_logout)
        menuLogout.setOnMenuItemClickListener {
            session.cerrarSesion()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            true
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
