package com.molytech.fsa.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.molytech.fsa.R
import com.molytech.fsa.data.di.LoginDependencyProvider
import com.molytech.fsa.databinding.ActivityLoginBinding
import com.molytech.fsa.domain.entities.AuthResult
import com.molytech.fsa.ui.passwordreset.RecuperarActivity
import com.molytech.fsa.ui.register.RegistroActivity
import com.molytech.fsa.ui.screens.AdministrarActivity
import com.molytech.fsa.ui.screens.MainActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient

    private val viewModel: LoginViewModel by viewModels {
        LoginDependencyProvider.provideLoginViewModelFactory(this)
    }

    // Launcher para el resultado de Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            Log.d("GoogleSignIn", "firebaseAuthWithGoogle:" + account.id)
            viewModel.loginWithGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Log.w("GoogleSignIn", "Google sign in failed", e)
            Toast.makeText(this, "Error en el inicio de sesión con Google", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false
        windowInsetsController.isAppearanceLightNavigationBars = false

        FirebaseApp.initializeApp(this)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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

        configureGoogleSignIn()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.editTextCorreo.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            viewModel.loginWithEmail(email, password)
        }

        binding.txtClickableRegister.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        binding.txtClickablePassword.setOnClickListener {
            val intent = Intent(this, RecuperarActivity::class.java)
            startActivity(intent)
        }

        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isUserLoggedIn.collect { isLoggedIn ->
                if (isLoggedIn) {
                    lifecycleScope.launch {
                        viewModel.userRole.collect { role ->
                            role?.let { navigateBasedOnRole(it) }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.loginState.collect { authResult ->
                when (authResult) {
                    is AuthResult.Success -> {
                        navigateBasedOnRole(authResult.user.role)
                    }
                    is AuthResult.Error -> {
                        Toast.makeText(this@LoginActivity, authResult.message, Toast.LENGTH_SHORT).show()
                    }
                    is AuthResult.Loading -> {
                        // Puedes mostrar un loading indicator aquí si quieres
                    }
                }
            }
        }
    }

    private fun navigateBasedOnRole(userRole: String) {
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

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }
}