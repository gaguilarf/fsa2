package com.molytech.fsa.ui.screens

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.molytech.fsa.R
import com.molytech.fsa.databinding.ActivityAdministrarBinding
import com.molytech.fsa.ui.historial.AdminHistorialActivity
import com.molytech.fsa.ui.inventario.InventarioActivity
import com.molytech.fsa.ui.login.LoginActivity
import com.molytech.fsa.ui.passwordreset.RecuperarActivity
import androidx.core.content.edit
import com.molytech.fsa.ui.editprofile.EditarPerfilActivity

class AdministrarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdministrarBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var btnRestablecer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false
        windowInsetsController.isAppearanceLightNavigationBars = false

        FirebaseApp.initializeApp(this)
        binding = ActivityAdministrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                insets.left,
                insets.top,
                insets.right,
                insets.bottom
            )
            windowInsets
        }

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

        val btnEditarPerfil = binding.btnEditarInformacion
        val btnSolicitudes = binding.btnSolicitudes
        val btnInventario = binding.btnInventario
        val btnCerrarSesion = binding.btnCerrarSesion
        btnRestablecer = binding.btnRestablecer

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
            val intent = Intent(this, InventarioActivity::class.java)
            startActivity(intent)
        }

        btnCerrarSesion.setOnClickListener {
            sharedPreferences.edit {
                clear()
            }

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
