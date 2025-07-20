package com.molytech.fsa.data.repositories

import android.net.Uri
import com.molytech.fsa.data.dataSources.CloudinaryDataSource
import com.molytech.fsa.data.dataSources.InventoryDataSource
import com.molytech.fsa.domain.entities.InventoryProduct
import com.molytech.fsa.domain.entities.InventoryProductRequest
import com.molytech.fsa.domain.repositories.InventoryRepository

class InventoryRepositoryImpl(
    private val inventoryDataSource: InventoryDataSource,
    private val cloudinaryDataSource: CloudinaryDataSource
) : InventoryRepository {

    override suspend fun getAllProducts(): List<InventoryProduct> {
        return inventoryDataSource.getAllProducts()
    }

    override suspend fun getProductByName(name: String): InventoryProduct? {
        return inventoryDataSource.getProductByName(name)
    }

    override suspend fun saveProduct(productRequest: InventoryProductRequest): Boolean {
        return inventoryDataSource.saveProduct(productRequest)
    }

    override suspend fun deleteProduct(name: String): Boolean {
        return inventoryDataSource.deleteProduct(name)
    }

    override suspend fun uploadImage(imageUri: Uri): String {
        return cloudinaryDataSource.uploadImage(imageUri)
    }
}
