package com.molytech.fsa.domain.entities

data class InventoryProduct(
    val name: String,
    val description: String,
    val price: String,
    val status: String, // "A" = Activo, "I" = Inactivo
    val imageUrl: String = ""
)

data class InventoryProductRequest(
    val name: String,
    val description: String,
    val price: String,
    val status: String,
    val imageUrl: String = ""
)

sealed class InventoryResult {
    data class Success(val products: List<InventoryProduct>) : InventoryResult()
    data class Error(val message: String) : InventoryResult()
    object Loading : InventoryResult()
}

sealed class SaveProductResult {
    object Success : SaveProductResult()
    data class Error(val message: String) : SaveProductResult()
    object Loading : SaveProductResult()
}

sealed class ImageUploadResult {
    data class Success(val imageUrl: String) : ImageUploadResult()
    data class Error(val message: String) : ImageUploadResult()
    object Loading : ImageUploadResult()
}
