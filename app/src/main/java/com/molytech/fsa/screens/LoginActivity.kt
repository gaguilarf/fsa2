package com.molytech.fsa.screens

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.R
import com.molytech.fsa.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false  // Íconos claros en barra de estado
            isAppearanceLightNavigationBars = false  // Íconos claros en barra de navegación
        }

        FirebaseApp.initializeApp(this)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val btnLogin = binding.loginButton
        val btnRegister = binding.txtClickableRegister
        val btnForgotPassword = binding.txtClickablePassword
        val edtCorreo = binding.editTextCorreo
        val edtPassword = binding.editTextPassword

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            val userRole = sharedPreferences.getString("usuTip", null) // Obtén el rol almacenado

            when (userRole) {
                "0" -> {
                    // Navegar a AdministrarActivity si el rol es "0"
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                "1" -> {
                    // Navegar a MainActivity si el rol es "1"
                    val intent = Intent(this, AdministrarActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    Toast.makeText(this, "Rol desconocido.", Toast.LENGTH_SHORT).show()
                }
            }
        }


        btnLogin.setOnClickListener {
            val email = edtCorreo.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            signInWithEmailAndPassword(email,password)
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        btnForgotPassword.setOnClickListener {
            val intent = Intent(this, RecuperarActivity::class.java)
            startActivity(intent)
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(baseContext, "Por favor, ingresa correo y contraseña.", Toast.LENGTH_SHORT).show()
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login exitoso
                    val user = auth.currentUser
                    if (user != null) {
                        getUserRole(user.email!!)
                    }
                } else {
                    // Si falla, mostrar mensaje de error
                    Toast.makeText(baseContext, "Datos incorrectos.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getUserRole(email: String) {
        val userDocRef = db.collection("Usuarios").document(email)

        userDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val editor = sharedPreferences.edit()

                // Almacenar todos los datos del documento del usuario
                for (field in document.data!!.entries) {
                    editor.putString("usuCor", email)
                    editor.putString(field.key, field.value.toString()) // Guardar cada campo como clave-valor en SharedPreferences
                }

                // Actualiza el estado de login
                editor.putBoolean("isLoggedIn", true)
                editor.apply()

                // Obtener el rol del usuario
                val userRole = document.getString("usuTip")
                when (userRole) {
                    "0" -> {
                        // Navegar a MainActivity si el rol es "0"
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    "1" -> {
                        // Navegar a AdministrarActivity si el rol es "1"
                        val intent = Intent(this, AdministrarActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else -> {
                        // Manejar otros roles o error
                        Toast.makeText(baseContext, "Rol desconocido.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(baseContext, "Usuario no encontrado en la base de datos.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e: Exception ->
            Toast.makeText(baseContext, "Error al obtener el rol: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

}