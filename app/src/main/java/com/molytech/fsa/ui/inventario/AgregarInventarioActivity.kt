package com.molytech.fsa.ui.inventario

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.molytech.fsa.R
import com.molytech.fsa.data.di.InventoryDependencyProvider
import com.molytech.fsa.databinding.ActivityAgregarInventarioBinding
import com.molytech.fsa.domain.entities.ImageUploadResult
import com.molytech.fsa.domain.entities.SaveProductResult
import kotlinx.coroutines.launch

class AgregarInventarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarInventarioBinding
    private var imagenSeleccionadaUri: Uri? = null
    private var imagenCargadaUri: Uri? = null
    private var isEditMode = false

    private val viewModel: AgregarInventarioViewModel by viewModels {
        InventoryDependencyProvider.provideAgregarInventarioViewModelFactory(this)
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

        binding = ActivityAgregarInventarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        loadProductData()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.txtClickableBack.setOnClickListener {
            finish()
        }

        val seleccionarImagenLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val uri = result.data!!.data
                imagenSeleccionadaUri = uri
                binding.imgVista.setImageURI(uri)
                uri?.let { viewModel.uploadImage(it) }
            }
        }

        binding.btnSubirImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            seleccionarImagenLauncher.launch(intent)
        }

        binding.btnGuardar.setOnClickListener {
            saveProduct()
        }
    }

    private fun loadProductData() {
        val nombre = intent.getStringExtra("nombre")
        val descripcion = intent.getStringExtra("descripcion")
        val precio = intent.getStringExtra("precio")
        val urlImagen = intent.getStringExtra("imagen")
        val estado = intent.getStringExtra("estado")

        if (!nombre.isNullOrEmpty()) {
            isEditMode = true
            binding.editTextNombreProducto.setText(nombre)
            binding.editTextDescripcion.setText(descripcion)
            binding.editTextPrecio.setText(precio)
            binding.swtEstado.isChecked = estado == "A"

            if (!urlImagen.isNullOrEmpty()) {
                Glide.with(this)
                    .load(urlImagen)
                    .placeholder(R.drawable.subir)
                    .error(R.drawable.ic_error)
                    .into(binding.imgVista)
                imagenCargadaUri = urlImagen.toUri()
            }
        } else {
            binding.imgVista.setImageResource(R.drawable.subir)
        }
    }

    private fun saveProduct() {
        val nombreProducto = binding.editTextNombreProducto.text.toString()
        val descripcionProducto = binding.editTextDescripcion.text.toString()
        val precioProducto = binding.editTextPrecio.text.toString()
        val estadoProducto = binding.swtEstado.isChecked
        val currentImageUrl = intent.getStringExtra("imagen") ?: ""
        val hasNewImage = imagenSeleccionadaUri != null && imagenSeleccionadaUri != imagenCargadaUri

        viewModel.saveProduct(
            name = nombreProducto,
            description = descripcionProducto,
            price = precioProducto,
            isActive = estadoProducto,
            currentImageUrl = currentImageUrl,
            hasNewImage = hasNewImage
        )
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.saveProductState.collect { saveResult ->
                when (saveResult) {
                    is SaveProductResult.Success -> {
                        Toast.makeText(this@AgregarInventarioActivity, "Producto guardado con éxito", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    }
                    is SaveProductResult.Error -> {
                        Toast.makeText(this@AgregarInventarioActivity, saveResult.message, Toast.LENGTH_SHORT).show()
                    }
                    is SaveProductResult.Loading -> {
                        // Puedes mostrar un loading indicator aquí si quieres
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.imageUploadState.collect { uploadResult ->
                when (uploadResult) {
                    is ImageUploadResult.Success -> {
                        // La imagen se subió correctamente, el ViewModel ya maneja la URL
                    }
                    is ImageUploadResult.Error -> {
                        Toast.makeText(this@AgregarInventarioActivity, uploadResult.message, Toast.LENGTH_SHORT).show()
                    }
                    is ImageUploadResult.Loading -> {
                        // Puedes mostrar un loading indicator para la subida de imagen
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.validationError.collect { error ->
                error?.let {
                    Toast.makeText(this@AgregarInventarioActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.clearValidationError()
                }
            }
        }
    }
}
