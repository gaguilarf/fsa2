package com.molytech.fsa.screens

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.R
import com.molytech.fsa.databinding.ActivityRecuperarBinding

class RecuperarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecuperarBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private val listaIds = mutableListOf<String>()

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
        binding = ActivityRecuperarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        obtenerDocumentos()

        val btnRecuperar = binding.btnRecuperar
        val edtCorreo = binding.editTextCorreo
        val btnBack = binding.txtClickableBack

        btnRecuperar.setOnClickListener {
            val email = edtCorreo.text.toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Correo electrónico inválido", Toast.LENGTH_SHORT).show()
            } else {
                verificarCorreo(email)
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun verificarCorreo(email: String) {
        var correoEncontrado = false
        for (id in listaIds) {
            if (id == email) {
                correoEncontrado = true
                break
            }
        }
        if (correoEncontrado) {
            enviarCorreoDeRestablecimiento(email)
        } else {
            Toast.makeText(this, "Correo no registrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun enviarCorreoDeRestablecimiento(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Correo enviado", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error al enviar el correo", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun obtenerDocumentos() {
        firestore.collection("Registrados")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val documentos = task.result
                    if (documentos != null && !documentos.isEmpty) {
                        for (documento in documentos) {
                            listaIds.add(documento.id) // Obtiene el nombre del documento (ID)
                        }
                    } else {
                        Toast.makeText(this, "No se encontraron documentos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Obtener el error detallado
                    val error = task.exception?.message
                    Toast.makeText(this, "Error al obtener documentos: $error", Toast.LENGTH_LONG).show()
                }
            }
    }

}