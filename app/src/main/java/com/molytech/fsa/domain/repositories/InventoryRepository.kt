package com.molytech.fsa.domain.repositories

import android.net.Uri
import com.molytech.fsa.domain.entities.InventoryProduct
import com.molytech.fsa.domain.entities.InventoryProductRequest

interface InventoryRepository {
    suspend fun getAllProducts(): List<InventoryProduct>
    suspend fun getProductByName(name: String): InventoryProduct?
    suspend fun saveProduct(productRequest: InventoryProductRequest): Boolean
    suspend fun deleteProduct(name: String): Boolean
    suspend fun uploadImage(imageUri: Uri): String
}
