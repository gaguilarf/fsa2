package com.molytech.fsa.screens

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.R
import com.molytech.fsa.databinding.ActivityRegistroBinding

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

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
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val btnBack = binding.txtClickableBack

        btnBack.setOnClickListener {
            finish()
        }

        val btnRegistro = binding.btnRegistro
        btnRegistro.setOnClickListener {
            val nombre = binding.editTextNombre.text.toString()
            val correo = binding.editTextCorreo.text.toString()
            val contraseña = binding.editTextContrasea.text.toString()
            val telefono = binding.editTextTelefono.text.toString()
            val marcaAutomovil = binding.editTextMarca.text.toString()
            val anoAutomovil = binding.editTextAno.text.toString()

            if (nombre.isNotEmpty() && correo.isNotEmpty() && contraseña.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(correo, contraseña)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = correo

                            val userMap = hashMapOf(
                                "usuNom" to nombre,
                                "usuTel" to telefono,
                                "usuMar" to marcaAutomovil,
                                "usuAño" to anoAutomovil,
                                "usuTip" to "0"
                            )

                            db.collection("Registrados").document(userId).set(emptyMap<String, Any>())
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Documento creado exitosamente con ID: $userId")
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Firestore", "Error al crear documento", e)
                                }

                            db.collection("Usuarios").document(userId)
                                .set(userMap)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                    finish() // Finaliza la actividad si el registro es exitoso
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error al registrar en Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this, "Error en autenticación: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}