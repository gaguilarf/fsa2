package com.molytech.fsa.domain.usecases

import android.net.Uri
import com.molytech.fsa.domain.entities.ImageUploadResult
import com.molytech.fsa.domain.entities.InventoryProduct
import com.molytech.fsa.domain.entities.InventoryProductRequest
import com.molytech.fsa.domain.entities.InventoryResult
import com.molytech.fsa.domain.entities.SaveProductResult
import com.molytech.fsa.domain.repositories.InventoryRepository

class GetAllInventoryProductsUseCase(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(): InventoryResult {
        return try {
            val products = inventoryRepository.getAllProducts()
            InventoryResult.Success(products)
        } catch (e: Exception) {
            InventoryResult.Error("Error al cargar inventario: ${e.message}")
        }
    }
}

class SaveInventoryProductUseCase(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(productRequest: InventoryProductRequest): SaveProductResult {
        // Validaciones
        if (productRequest.name.isBlank()) {
            return SaveProductResult.Error("El nombre del producto es requerido")
        }
        if (productRequest.description.isBlank()) {
            return SaveProductResult.Error("La descripción es requerida")
        }
        if (productRequest.price.isBlank()) {
            return SaveProductResult.Error("El precio es requerido")
        }

        // Validar que el precio sea un número válido
        try {
            productRequest.price.toDouble()
        } catch (e: NumberFormatException) {
            return SaveProductResult.Error("El precio debe ser un número válido")
        }

        return try {
            val success = inventoryRepository.saveProduct(productRequest)
            if (success) {
                SaveProductResult.Success
            } else {
                SaveProductResult.Error("Error al guardar el producto")
            }
        } catch (e: Exception) {
            SaveProductResult.Error("Error al guardar: ${e.message}")
        }
    }
}

class UploadProductImageUseCase(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(imageUri: Uri): ImageUploadResult {
        return try {
            val imageUrl = inventoryRepository.uploadImage(imageUri)
            if (imageUrl.isNotEmpty()) {
                ImageUploadResult.Success(imageUrl)
            } else {
                ImageUploadResult.Error("Error al subir la imagen")
            }
        } catch (e: Exception) {
            ImageUploadResult.Error("Error al subir imagen: ${e.message}")
        }
    }
}

class ValidateProductFieldsUseCase {
    operator fun invoke(name: String, description: String, price: String): String? {
        return when {
            name.isBlank() -> "El nombre del producto es requerido"
            name.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            description.isBlank() -> "La descripción es requerida"
            price.isBlank() -> "El precio es requerido"
            else -> {
                try {
                    val priceValue = price.toDouble()
                    if (priceValue <= 0) "El precio debe ser mayor a 0" else null
                } catch (e: NumberFormatException) {
                    "El precio debe ser un número válido"
                }
            }
        }
    }
}

class FilterInventoryByStatusUseCase {
    operator fun invoke(products: List<InventoryProduct>, status: String?): List<InventoryProduct> {
        return if (status.isNullOrEmpty()) {
            products
        } else {
            products.filter { it.status == status }
        }
    }
}

class GetActiveInventoryUseCase(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(): Result<List<InventoryProduct>> {
        return try {
            val allProducts = inventoryRepository.getAllProducts()
            val activeProducts = allProducts.filter { it.status == "A" }
            Result.success(activeProducts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
