package com.molytech.fsa.ui.historial

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.molytech.fsa.ui.servicios.SolicitarServicioActivity
import com.molytech.fsa.databinding.ActivityHistorialServiciosBinding
import com.molytech.fsa.domain.entities.HistorialItem
import com.molytech.fsa.data.di.HistorialDependencyProvider

class HistorialServiciosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistorialServiciosBinding
    private lateinit var recyclerHistorial: RecyclerView
    private lateinit var historialAdapter: HistorialAdapter
    private lateinit var addProductLauncher: ActivityResultLauncher<Intent>
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var viewModel: HistorialServiciosViewModel
    private var correo: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        binding = ActivityHistorialServiciosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindow()
        setupViewModel()
        setupRecyclerView()
        setupActivityResultLauncher()
        setupObservers()
        setupClickListeners()

        loadHistorial()
    }

    private fun setupWindow() {

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = false
        windowInsetsController.isAppearanceLightNavigationBars = false

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
    }

    private fun setupViewModel() {
        // Setup dependencies using the dependency provider
        val viewModelFactory = HistorialServiciosViewModelFactory(
            HistorialDependencyProvider.getHistorialServiciosUseCase
        )

        viewModel = ViewModelProvider(this, viewModelFactory)[HistorialServiciosViewModel::class.java]

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        correo = sharedPreferences.getString("usuCor", "")
    }

    private fun setupRecyclerView() {
        recyclerHistorial = binding.recyclerHistorial
        recyclerHistorial.layoutManager = LinearLayoutManager(this)

        historialAdapter = HistorialAdapter(mutableListOf()) { selectedItem ->
            navigateToSolicitarServicio(selectedItem)
        }
        recyclerHistorial.adapter = historialAdapter
    }

    private fun setupActivityResultLauncher() {
        addProductLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                loadHistorial()
            }
        }
    }

    private fun setupObservers() {
        viewModel.historialItems.observe(this) { items ->
            historialAdapter.updateItems(items)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            // Mostrar/ocultar indicador de carga si es necesario
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }

    private fun setupClickListeners() {
        binding.txtClickableBack.setOnClickListener {
            finish()
        }

        binding.fabAgregar.setOnClickListener {
            val intent = Intent(this, SolicitarServicioActivity::class.java)
            addProductLauncher.launch(intent)
        }
    }

    private fun loadHistorial() {
        correo?.let { email ->
            if (email.isNotEmpty()) {
                viewModel.loadHistorial(email)
            }
        }
    }

    private fun navigateToSolicitarServicio(selectedItem: HistorialItem) {
        val intent = Intent(this, SolicitarServicioActivity::class.java).apply {
            putExtra("id", selectedItem.id)
            putExtra("hora", selectedItem.hora)
            putExtra("fecha", selectedItem.fecha)
            putExtra("nombre", selectedItem.nombre)
            putExtra("telefono", selectedItem.telefono)
            putExtra("descripcion", selectedItem.descripcion)
            putExtra("inventario", selectedItem.inventario)
            putExtra("latitud", selectedItem.latitud)
            putExtra("longitud", selectedItem.longitud)
            putExtra("estado", selectedItem.estado)
        }
        addProductLauncher.launch(intent)
    }

    override fun onResume() {
        super.onResume()
        loadHistorial()
    }
}