package com.molytech.fsa.ui.inventario

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
import com.molytech.fsa.data.di.InventoryDependencyProvider
import com.molytech.fsa.databinding.ActivityInventarioBinding
import com.molytech.fsa.domain.entities.InventarioItem
import com.molytech.fsa.domain.entities.InventoryProduct
import com.molytech.fsa.domain.entities.InventoryResult
import kotlinx.coroutines.launch

class InventarioActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventarioBinding
    private lateinit var inventarioAdapter: InventarioAdapter
    private lateinit var addProductLauncher: ActivityResultLauncher<Intent>

    private val viewModel: InventarioViewModel by viewModels {
        InventoryDependencyProvider.provideInventarioViewModelFactory(this)
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

        binding = ActivityInventarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
        setupActivityResultLauncher()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshInventory()
    }

    private fun setupRecyclerView() {
        inventarioAdapter = InventarioAdapter(emptyList()) { selectedItem ->
            navigateToAddProduct(selectedItem)
        }

        binding.recyclerInventario.apply {
            layoutManager = LinearLayoutManager(this@InventarioActivity)
            adapter = inventarioAdapter
        }
    }

    private fun setupClickListeners() {
        binding.txtClickableBack.setOnClickListener {
            finish()
        }

        binding.fabAgregar.setOnClickListener {
            val intent = Intent(this, AgregarInventarioActivity::class.java)
            addProductLauncher.launch(intent)
        }
    }

    private fun setupActivityResultLauncher() {
        addProductLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.refreshInventory()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.inventoryState.collect { inventoryResult ->
                when (inventoryResult) {
                    is InventoryResult.Success -> {
                        // Los datos se manejan a través de filteredProducts
                    }
                    is InventoryResult.Error -> {
                        Toast.makeText(this@InventarioActivity, inventoryResult.message, Toast.LENGTH_SHORT).show()
                    }
                    is InventoryResult.Loading -> {
                        // Puedes mostrar un loading indicator aquí si quieres
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.filteredProducts.collect { products ->
                updateRecyclerView(products)
            }
        }
    }

    private fun updateRecyclerView(products: List<InventoryProduct>) {
        // Convertir InventoryProduct a InventarioItem para mantener compatibilidad con el adapter existente
        val inventarioItems = products.map { product ->
            InventarioItem(
                nombre = product.name,
                descripcion = product.description,
                precio = product.price,
                estado = product.status,
                imagen = product.imageUrl
            )
        }

        inventarioAdapter.updateItems(inventarioItems)
    }

    private fun navigateToAddProduct(selectedItem: InventarioItem) {
        val intent = Intent(this, AgregarInventarioActivity::class.java).apply {
            putExtra("nombre", selectedItem.nombre)
            putExtra("descripcion", selectedItem.descripcion)
            putExtra("precio", selectedItem.precio)
            putExtra("estado", selectedItem.estado)
            putExtra("imagen", selectedItem.imagen)
        }
        addProductLauncher.launch(intent)
    }
}
