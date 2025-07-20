package com.molytech.fsa.ui.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.molytech.fsa.domain.usecases.SaveInventoryProductUseCase
import com.molytech.fsa.domain.usecases.UploadProductImageUseCase
import com.molytech.fsa.domain.usecases.ValidateProductFieldsUseCase

class AgregarInventarioViewModelFactory(
    private val saveInventoryProductUseCase: SaveInventoryProductUseCase,
    private val uploadProductImageUseCase: UploadProductImageUseCase,
    private val validateProductFieldsUseCase: ValidateProductFieldsUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AgregarInventarioViewModel::class.java)) {
            return AgregarInventarioViewModel(
                saveInventoryProductUseCase,
                uploadProductImageUseCase,
                validateProductFieldsUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
