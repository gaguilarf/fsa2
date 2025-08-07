package com.molytech.fsa.ui.passwordreset

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseApp
import com.molytech.fsa.data.di.PasswordResetDependencyProvider
import com.molytech.fsa.databinding.ActivityRecuperarBinding
import com.molytech.fsa.domain.entities.PasswordResetResult
import kotlinx.coroutines.launch

class RecuperarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRecuperarBinding

    private val viewModel: PasswordResetViewModel by viewModels {
        PasswordResetDependencyProvider.providePasswordResetViewModelFactory(this)
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
        binding = ActivityRecuperarBinding.inflate(layoutInflater)
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

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnRecuperar.setOnClickListener {
            val email = binding.editTextCorreo.text.toString()
            viewModel.resetPassword(email)
        }

        binding.txtClickableBack.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.resetState.collect { resetResult ->
                when (resetResult) {
                    is PasswordResetResult.Success -> {
                        Toast.makeText(this@RecuperarActivity, "Correo enviado", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is PasswordResetResult.Error -> {
                        Toast.makeText(this@RecuperarActivity, resetResult.message, Toast.LENGTH_SHORT).show()
                    }
                    is PasswordResetResult.Loading -> {
                        // Puedes mostrar un loading indicator aquí si quieres
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.validationError.collect { error ->
                error?.let {
                    Toast.makeText(this@RecuperarActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearValidationError()
                }
            }
        }
    }

}