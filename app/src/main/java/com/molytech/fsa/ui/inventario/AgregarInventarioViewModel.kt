package com.molytech.fsa.ui.inventario

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.molytech.fsa.domain.entities.ImageUploadResult
import com.molytech.fsa.domain.entities.InventoryProductRequest
import com.molytech.fsa.domain.entities.SaveProductResult
import com.molytech.fsa.domain.usecases.SaveInventoryProductUseCase
import com.molytech.fsa.domain.usecases.UploadProductImageUseCase
import com.molytech.fsa.domain.usecases.ValidateProductFieldsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AgregarInventarioViewModel(
    private val saveInventoryProductUseCase: SaveInventoryProductUseCase,
    private val uploadProductImageUseCase: UploadProductImageUseCase,
    private val validateProductFieldsUseCase: ValidateProductFieldsUseCase
) : ViewModel() {

    private val _saveProductState = MutableStateFlow<SaveProductResult>(SaveProductResult.Loading)
    val saveProductState: StateFlow<SaveProductResult> = _saveProductState

    private val _imageUploadState = MutableStateFlow<ImageUploadResult>(ImageUploadResult.Loading)
    val imageUploadState: StateFlow<ImageUploadResult> = _imageUploadState

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var uploadedImageUrl: String = ""

    fun saveProduct(
        name: String,
        description: String,
        price: String,
        isActive: Boolean,
        currentImageUrl: String = "",
        hasNewImage: Boolean = false
    ) {
        // Validar campos primero
        val validationError = validateProductFieldsUseCase(name, description, price)
        if (validationError != null) {
            _validationError.value = validationError
            return
        }

        _validationError.value = null
        _isLoading.value = true
        _saveProductState.value = SaveProductResult.Loading

        val finalImageUrl = if (hasNewImage && uploadedImageUrl.isNotEmpty()) {
            uploadedImageUrl
        } else {
            currentImageUrl
        }

        val productRequest = InventoryProductRequest(
            name = name.trim(),
            description = description.trim(),
            price = price.trim(),
            status = if (isActive) "A" else "I",
            imageUrl = finalImageUrl
        )

        viewModelScope.launch {
            val result = saveInventoryProductUseCase(productRequest)
            _saveProductState.value = result
            _isLoading.value = false
        }
    }

    fun uploadImage(imageUri: Uri) {
        viewModelScope.launch {
            _imageUploadState.value = ImageUploadResult.Loading
            val result = uploadProductImageUseCase(imageUri)
            _imageUploadState.value = result

            if (result is ImageUploadResult.Success) {
                uploadedImageUrl = result.imageUrl
            }
        }
    }

    fun clearValidationError() {
        _validationError.value = null
    }

    fun resetSaveState() {
        _saveProductState.value = SaveProductResult.Loading
    }

    fun resetImageUploadState() {
        _imageUploadState.value = ImageUploadResult.Loading
        uploadedImageUrl = ""
    }
}
