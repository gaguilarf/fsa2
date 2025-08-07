package com.molytech.fsa.ui.editprofile

import android.content.Intent
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
import com.molytech.fsa.data.di.EditProfileDependencyProvider
import com.molytech.fsa.databinding.ActivityEditarPerfilBinding
import com.molytech.fsa.domain.entities.UpdateProfileResult
import com.molytech.fsa.ui.screens.AdministrarActivity
import com.molytech.fsa.ui.screens.MainActivity
import kotlinx.coroutines.launch

class EditarPerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditarPerfilBinding

    private val viewModel: EditProfileViewModel by viewModels {
        EditProfileDependencyProvider.provideEditProfileViewModelFactory(this)
    }

    private var userEmail = ""
    private var userRole = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false
        windowInsetsController.isAppearanceLightNavigationBars = false

        FirebaseApp.initializeApp(this)
        binding = ActivityEditarPerfilBinding.inflate(layoutInflater)
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

        // Obtener datos del usuario actual
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        userEmail = sharedPreferences.getString("usuCor", "") ?: ""
        userRole = sharedPreferences.getString("usuTip", "") ?: ""

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.txtClickableBack.setOnClickListener {
            finish()
        }

        binding.btnGuardar.setOnClickListener {
            val nombre = binding.editTextNombre.text.toString()
            val telefono = binding.editTextTelefono.text.toString()
            val marca = binding.editTextMarca.text.toString()
            val año = binding.editTextAno.text.toString()

            viewModel.updateProfile(
                email = userEmail,
                name = nombre,
                phone = telefono,
                carBrand = marca,
                carYear = año,
                role = userRole
            )
        }
    }

    private fun observeViewModel() {
        // Observar el perfil del usuario para cargar datos iniciales
        lifecycleScope.launch {
            viewModel.userProfile.collect { profile ->
                profile?.let {
                    binding.editTextNombre.setText(it.name)
                    binding.editTextTelefono.setText(it.phone)
                    binding.editTextMarca.setText(it.carBrand)
                    binding.editTextAno.setText(it.carYear)
                }
            }
        }

        // Observar el estado de actualización
        lifecycleScope.launch {
            viewModel.updateState.collect { updateResult ->
                when (updateResult) {
                    is UpdateProfileResult.Success -> {
                        Toast.makeText(this@EditarPerfilActivity, "Perfil actualizado.", Toast.LENGTH_SHORT).show()
                        navigateBasedOnRole(updateResult.user.role)
                    }
                    is UpdateProfileResult.Error -> {
                        Toast.makeText(this@EditarPerfilActivity, updateResult.message, Toast.LENGTH_SHORT).show()
                    }
                    is UpdateProfileResult.Loading -> {
                        // Puedes mostrar un loading indicator aquí si quieres
                    }
                }
            }
        }

        // Observar errores de validación
        lifecycleScope.launch {
            viewModel.validationError.collect { error ->
                error?.let {
                    Toast.makeText(this@EditarPerfilActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearValidationError()
                }
            }
        }
    }

    private fun navigateBasedOnRole(role: String) {
        when (role) {
            "0" -> {
                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
            "1" -> {
                val intent = Intent(this, AdministrarActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
            else -> {
                Toast.makeText(this, "Rol desconocido.", Toast.LENGTH_SHORT).show()
            }
        }
    }

}