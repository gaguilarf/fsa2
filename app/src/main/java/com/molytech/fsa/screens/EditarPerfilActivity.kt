package com.molytech.fsa.screens

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.R
import com.molytech.fsa.databinding.ActivityEditarPerfilBinding

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var sharedPreferences: SharedPreferences

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
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        loadUserData()
        val btnGuardar = binding.btnGuardar
        val btnBack = binding.txtClickableBack

        btnBack.setOnClickListener {
            finish()
        }

        btnGuardar.setOnClickListener {
            saveUserData()
        }
    }

    private fun loadUserData() {
        val nombre = sharedPreferences.getString("usuNom", "")
        val telefono = sharedPreferences.getString("usuTel", "")
        val marca = sharedPreferences.getString("usuMar", "")
        val año = sharedPreferences.getString("usuAño", "")

        // Populate the EditTexts with data from SharedPreferences
        binding.editTextNombre.setText(nombre)
        binding.editTextTelefono.setText(telefono)
        binding.editTextMarca.setText(marca)
        binding.editTextAno.setText(año)
    }

    private fun saveUserData() {
        // Get data from EditTexts
        val email = sharedPreferences.getString("usuCor", "") ?: ""
        val rol = sharedPreferences.getString("usuTip", "") ?: ""
        val nombre = binding.editTextNombre.text.toString()
        val telefono = binding.editTextTelefono.text.toString()
        val marca = binding.editTextMarca.text.toString()
        val año = binding.editTextAno.text.toString()

        // Save data to SharedPreferences
        with(sharedPreferences.edit()) {
            putString("usuNom", nombre)
            putString("usuTel", telefono)
            putString("usuMar", marca)
            putString("usuAño", año)
            apply() // or commit() if you want synchronous saving
        }

        // Update data in Firestore
        val userId = auth.currentUser?.uid // Assuming you have a user ID
        userId?.let {
            val userData = hashMapOf(
                "usuNom" to nombre,
                "usuTel" to telefono,
                "usuMar" to marca,
                "usuAño" to año,
                "usuTip" to rol
            )

            firestore.collection("Usuarios")
                .document(email)
                .set(userData)
                .addOnSuccessListener {
                    Toast.makeText(baseContext, "Perfil actualizado.", Toast.LENGTH_SHORT).show()
                    when (rol) {
                        "0" -> {
                            // Navegar a MainActivity si el rol es "0"
                            val intent = Intent(this, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            finish()
                        }

                        "1" -> {
                            // Navegar a AdministrarActivity si el rol es "1"
                            val intent = Intent(this, AdministrarActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            }
                            startActivity(intent)
                            finish()
                        }

                        else -> {
                            // Manejar otros roles o error
                            Toast.makeText(baseContext, "Rol desconocido.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(baseContext, "Ocurrio un error.", Toast.LENGTH_SHORT).show()
                }
        }
    }

}