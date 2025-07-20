package com.molytech.fsa.ui.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.molytech.fsa.domain.usecases.FilterInventoryByStatusUseCase
import com.molytech.fsa.domain.usecases.GetAllInventoryProductsUseCase

class InventarioViewModelFactory(
    private val getAllInventoryProductsUseCase: GetAllInventoryProductsUseCase,
    private val filterInventoryByStatusUseCase: FilterInventoryByStatusUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventarioViewModel::class.java)) {
            return InventarioViewModel(
                getAllInventoryProductsUseCase,
                filterInventoryByStatusUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
