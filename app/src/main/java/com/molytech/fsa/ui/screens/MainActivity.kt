package com.molytech.fsa.ui.screens

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.molytech.fsa.ui.historial.HistorialServiciosActivity
import com.molytech.fsa.R
import com.molytech.fsa.databinding.ActivityMainBinding
import com.molytech.fsa.ui.screens.LoginActivity
import androidx.core.content.edit
import com.molytech.fsa.ui.screens.RecuperarActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var btnRestablecer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Recuperar datos almacenados en SharedPreferences
        val nombre = sharedPreferences.getString("usuNom", "No disponible")
        val correo = sharedPreferences.getString("usuCor", "No disponible")
        val telefono = sharedPreferences.getString("usuTel", "No disponible")

        // Asignar valores a los TextView
        binding.edtNombre.setText(nombre)
        binding.edtCorreo.setText(correo)
        binding.edtTelefono.setText(telefono)

        val btnHistorialServicios = binding.btnHistorialServicios
        val btnEditarPerfil = binding.btnEditarInformacion
        val btnCerrarSesion = binding.btnCerrarSesion
        btnRestablecer = binding.btnRestablecer

        btnRestablecer.setOnClickListener {
            val intent = Intent(this, RecuperarActivity::class.java)
            startActivity(intent)
        }

        btnHistorialServicios.setOnClickListener {
            val intent = Intent(this, HistorialServiciosActivity::class.java)
            startActivity(intent)
        }

        btnCerrarSesion.setOnClickListener {
            sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            sharedPreferences.edit {
                clear()
            }

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPerfilActivity::class.java)
            startActivity(intent)
        }
    }
}