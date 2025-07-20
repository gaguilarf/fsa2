package com.molytech.fsa.ui.screens

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseApp
import com.molytech.fsa.data.di.RegisterDependencyProvider
import com.molytech.fsa.databinding.ActivityRegistroBinding
import com.molytech.fsa.domain.entities.RegisterResult
import com.molytech.fsa.ui.register.RegisterViewModel
import kotlinx.coroutines.launch

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding

    private val viewModel: RegisterViewModel by viewModels {
        RegisterDependencyProvider.provideRegisterViewModelFactory(this)
    }

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
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.txtClickableBack.setOnClickListener {
            finish()
        }

        binding.btnRegistrar.setOnClickListener {
            val nombre = binding.editTextNombre.text.toString()
            val correo = binding.editTextCorreo.text.toString()
            val contraseña = binding.editTextContrasea.text.toString()
            val telefono = binding.editTextTelefono.text.toString()
            val marcaAutomovil = binding.editTextMarca.text.toString()
            val anoAutomovil = binding.editTextAno.text.toString()

            viewModel.registerUser(
                name = nombre,
                email = correo,
                password = contraseña,
                phone = telefono,
                carBrand = marcaAutomovil,
                carYear = anoAutomovil
            )
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.registerState.collect { registerResult ->
                when (registerResult) {
                    is RegisterResult.Success -> {
                        Toast.makeText(this@RegistroActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is RegisterResult.Error -> {
                        Toast.makeText(this@RegistroActivity, registerResult.message, Toast.LENGTH_SHORT).show()
                    }
                    is RegisterResult.Loading -> {
                        // Puedes mostrar un loading indicator aquí si quieres
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.validationError.collect { error ->
                error?.let {
                    Toast.makeText(this@RegistroActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearValidationError()
                }
            }
        }
    }
}