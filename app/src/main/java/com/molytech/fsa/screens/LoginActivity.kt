package com.molytech.fsa.screens

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.R
import com.molytech.fsa.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var googleSignInClient: GoogleSignInClient

    // Launcher para el resultado de Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.d("GoogleSignIn", "firebaseAuthWithGoogle:" + account.id)
            firebaseAuthWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w("GoogleSignIn", "Google sign in failed", e)
            Toast.makeText(this, "Error en el inicio de sesión con Google", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        FirebaseApp.initializeApp(this)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Configurar Google Sign-In
        configureGoogleSignIn()

        val btnLogin = binding.loginButton
        val btnRegister = binding.txtClickableRegister
        val btnForgotPassword = binding.txtClickablePassword
        val btnGoogle = binding.btnGoogle // Tu ImageButton para Google
        val edtCorreo = binding.editTextCorreo
        val edtPassword = binding.editTextPassword

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            val userRole = sharedPreferences.getString("usuTip", null)

            when (userRole) {
                "0" -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                "1" -> {
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
            signInWithEmailAndPassword(email, password)
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        btnForgotPassword.setOnClickListener {
            val intent = Intent(this, RecuperarActivity::class.java)
            startActivity(intent)
        }

        // Configurar el botón de Google Sign-In
        btnGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("GoogleSignIn", "signInWithCredential:success")
                    val user = auth.currentUser
                    if (user != null) {
                        // Verificar si el usuario ya existe en Firestore
                        checkUserInFirestore(user.email!!, user.displayName ?: "Usuario")
                    }
                } else {
                    Log.w("GoogleSignIn", "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Error en la autenticación con Google.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserInFirestore(email: String, displayName: String) {
        val userDocRef = db.collection("Usuarios").document(email)

        userDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                // El usuario ya existe, proceder con el login
                saveUserDataAndNavigate(document.data!!, email)
            } else {
                // El usuario no existe, crear nuevo documento
                createNewGoogleUser(email, displayName)
            }
        }.addOnFailureListener { e ->
            Log.w("Firestore", "Error al verificar usuario", e)
            Toast.makeText(baseContext, "Error al verificar usuario: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNewGoogleUser(email: String, displayName: String) {
        val userData = hashMapOf(
            "usuNom" to displayName,
            "usuCor" to email,
            "usuTip" to "0", // Rol por defecto (puedes cambiarlo según tu lógica)
            "usuAño" to "",
            "usuTel" to ""
        )

        db.collection("Usuarios").document(email)
            .set(userData)
            .addOnSuccessListener {
                Log.d("Firestore", "Usuario creado exitosamente")
                saveUserDataAndNavigate(userData, email)
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error al crear usuario", e)
                Toast.makeText(baseContext, "Error al crear usuario: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserDataAndNavigate(userData: Map<String, Any>, email: String) {
        val editor = sharedPreferences.edit()

        // Almacenar todos los datos del usuario
        editor.putString("usuCor", email)
        for (field in userData.entries) {
            editor.putString(field.key, field.value.toString())
        }

        editor.putBoolean("isLoggedIn", true)
        editor.apply()

        // Navegar según el rol
        val userRole = userData["usuTip"].toString()
        when (userRole) {
            "0" -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            "1" -> {
                val intent = Intent(this, AdministrarActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> {
                Toast.makeText(baseContext, "Rol desconocido.", Toast.LENGTH_SHORT).show()
            }
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
                    val user = auth.currentUser
                    if (user != null) {
                        getUserRole(user.email!!)
                    }
                } else {
                    Toast.makeText(baseContext, "Datos incorrectos.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getUserRole(email: String) {
        val userDocRef = db.collection("Usuarios").document(email)

        userDocRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val editor = sharedPreferences.edit()

                for (field in document.data!!.entries) {
                    editor.putString("usuCor", email)
                    editor.putString(field.key, field.value.toString())
                }

                editor.putBoolean("isLoggedIn", true)
                editor.apply()

                val userRole = document.getString("usuTip")
                when (userRole) {
                    "0" -> {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    "1" -> {
                        val intent = Intent(this, AdministrarActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else -> {
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