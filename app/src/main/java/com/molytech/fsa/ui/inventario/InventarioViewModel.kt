package com.molytech.fsa.ui.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.molytech.fsa.domain.entities.InventoryProduct
import com.molytech.fsa.domain.entities.InventoryResult
import com.molytech.fsa.domain.usecases.FilterInventoryByStatusUseCase
import com.molytech.fsa.domain.usecases.GetAllInventoryProductsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InventarioViewModel(
    private val getAllInventoryProductsUseCase: GetAllInventoryProductsUseCase,
    private val filterInventoryByStatusUseCase: FilterInventoryByStatusUseCase
) : ViewModel() {

    private val _inventoryState = MutableStateFlow<InventoryResult>(InventoryResult.Loading)
    val inventoryState: StateFlow<InventoryResult> = _inventoryState

    private val _filteredProducts = MutableStateFlow<List<InventoryProduct>>(emptyList())
    val filteredProducts: StateFlow<List<InventoryProduct>> = _filteredProducts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var allProducts: List<InventoryProduct> = emptyList()

    init {
        loadInventory()
    }

    fun loadInventory() {
        viewModelScope.launch {
            _isLoading.value = true
            _inventoryState.value = InventoryResult.Loading

            val result = getAllInventoryProductsUseCase()
            _inventoryState.value = result

            if (result is InventoryResult.Success) {
                allProducts = result.products
                _filteredProducts.value = allProducts
            }

            _isLoading.value = false
        }
    }

    fun filterByStatus(status: String?) {
        val filtered = filterInventoryByStatusUseCase(allProducts, status)
        _filteredProducts.value = filtered
    }

    fun refreshInventory() {
        loadInventory()
    }

    fun getProductByName(name: String): InventoryProduct? {
        return allProducts.find { it.name == name }
    }
}
