package com.molytech.fsa.ui.historial

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.FirebaseApp
import com.molytech.fsa.data.di.AdminHistorialDependencyProvider
import com.molytech.fsa.databinding.ActivityAdminHistorialBinding
import com.molytech.fsa.domain.entities.HistorialResult
import com.molytech.fsa.domain.entities.ServiceRequest
import com.molytech.fsa.ui.servicios.VerSolicitudActivity
import kotlinx.coroutines.launch
import com.molytech.fsa.domain.entities.HistorialItem
import com.molytech.fsa.ui.historial.HistorialAdapter

class AdminHistorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminHistorialBinding
    private lateinit var historialAdapter: HistorialAdapter
    private lateinit var addProductLauncher: ActivityResultLauncher<Intent>

    private val viewModel: AdminHistorialViewModel by viewModels {
        AdminHistorialDependencyProvider.provideAdminHistorialViewModelFactory(this)
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

        binding = ActivityAdminHistorialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)

        setupRecyclerView()
        setupClickListeners()
        setupActivityResultLauncher()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData()
    }

    private fun setupRecyclerView() {
        historialAdapter = HistorialAdapter(emptyList()) { selectedItem ->
            navigateToServiceRequestDetail(selectedItem)
        }

        binding.recyclerHistorial.apply {
            layoutManager = LinearLayoutManager(this@AdminHistorialActivity)
            adapter = historialAdapter
        }
    }

    private fun setupClickListeners() {
        binding.txtClickableBack.setOnClickListener {
            finish()
        }
    }

    private fun setupActivityResultLauncher() {
        addProductLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.refreshData()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.historialState.collect { historialResult ->
                when (historialResult) {
                    is HistorialResult.Success -> {
                        // Los datos se manejan a través de filteredRequests
                    }
                    is HistorialResult.Error -> {
                        Toast.makeText(this@AdminHistorialActivity, historialResult.message, Toast.LENGTH_SHORT).show()
                    }
                    is HistorialResult.Loading -> {
                        // Puedes mostrar un loading indicator aquí si quieres
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.filteredRequests.collect { requests ->
                updateRecyclerView(requests)
            }
        }
    }

    private fun updateRecyclerView(requests: List<ServiceRequest>) {
        // Convertir ServiceRequest a HistorialItem para mantener compatibilidad con el adapter existente
        val historialItems = requests.map { request ->
            HistorialItem(
                id = request.id,
                correo = request.userEmail,
                hora = request.time,
                fecha = request.date,
                nombre = request.userName,
                telefono = request.userPhone,
                descripcion = request.description,
                inventario = request.inventory,
                latitud = request.latitude,
                longitud = request.longitude,
                estado = request.status
            )
        }
        historialAdapter.updateItems(historialItems)
    }

    private fun navigateToServiceRequestDetail(selectedItem: HistorialItem) {
        val intent = Intent(this, VerSolicitudActivity::class.java).apply {
            putExtra("id", selectedItem.id)
            putExtra("correo", selectedItem.correo)
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
}