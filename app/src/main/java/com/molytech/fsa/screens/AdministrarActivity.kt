package com.molytech.fsa.screens

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.molytech.fsa.R
import com.molytech.fsa.databinding.ActivityAdministrarBinding
import com.molytech.fsa.historial.AdminHistorialActivity
import com.molytech.fsa.inventario.inventarioActivity

class AdministrarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdministrarBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var btnRestablecer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val turquesaColor = ContextCompat.getColor(this, R.color.turquesa)
        window.statusBarColor = turquesaColor
        window.navigationBarColor = turquesaColor

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false  // Íconos claros en barra de estado
            isAppearanceLightNavigationBars = false  // Íconos claros en barra de navegación
        }

        FirebaseApp.initializeApp(this)
        binding = ActivityAdministrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Recuperar datos almacenados en SharedPreferences
        val nombre = sharedPreferences.getString("usuNom", "No disponible")
        val correo = sharedPreferences.getString("usuCor", "No disponible")
        val telefono = sharedPreferences.getString("usuTel", "No disponible")

        // Asignar valores a los TextView
        binding.edtNombre.text = nombre
        binding.edtCorreo.text = correo
        binding.edtTelefono.text = telefono

        val btnEditarPerfil = binding.btnEditarInformacion
        val btnSolicitudes = binding.btnSolicitudes
        val btnInventario = binding.btnInventario
        val btnCerrarSesion = binding.btnCerrarSesion
        btnRestablecer = binding.edtContrasea

        btnRestablecer.setOnClickListener {
            val intent = Intent(this, RecuperarActivity::class.java)
            startActivity(intent)
        }

        btnSolicitudes.setOnClickListener {
            val intent = Intent(this, AdminHistorialActivity::class.java)
            startActivity(intent)
        }

        btnEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPerfilActivity::class.java)
            startActivity(intent)
        }

        btnInventario.setOnClickListener {
            val intent = Intent(this, inventarioActivity::class.java)
            startActivity(intent)
        }

        btnCerrarSesion.setOnClickListener {
            val editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
