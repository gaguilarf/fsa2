package com.molytech.fsa.data.dataSources

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.domain.entities.InventoryProduct
import com.molytech.fsa.domain.entities.InventoryProductRequest
import kotlinx.coroutines.tasks.await

interface InventoryDataSource {
    suspend fun getAllProducts(): List<InventoryProduct>
    suspend fun getProductByName(name: String): InventoryProduct?
    suspend fun saveProduct(productRequest: InventoryProductRequest): Boolean
    suspend fun deleteProduct(name: String): Boolean
}

class InventoryDataSourceImpl(
    private val firestore: FirebaseFirestore
) : InventoryDataSource {

    override suspend fun getAllProducts(): List<InventoryProduct> {
        return try {
            val result = firestore.collection("Inventario")
                .get()
                .await()

            result.documents.map { document ->
                InventoryProduct(
                    name = document.id,
                    description = document.getString("invDes") ?: "",
                    price = document.getString("invPre") ?: "",
                    status = document.getString("invEst") ?: "",
                    imageUrl = document.getString("invUrl") ?: ""
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getProductByName(name: String): InventoryProduct? {
        return try {
            val document = firestore.collection("Inventario")
                .document(name)
                .get()
                .await()

            if (document.exists()) {
                InventoryProduct(
                    name = document.id,
                    description = document.getString("invDes") ?: "",
                    price = document.getString("invPre") ?: "",
                    status = document.getString("invEst") ?: "",
                    imageUrl = document.getString("invUrl") ?: ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveProduct(productRequest: InventoryProductRequest): Boolean {
        return try {
            val productData = hashMapOf<String, Any>(
                "invDes" to productRequest.description,
                "invPre" to productRequest.price,
                "invEst" to productRequest.status,
                "invUrl" to productRequest.imageUrl
            )

            firestore.collection("Inventario")
                .document(productRequest.name)
                .set(productData)
                .await()
            true
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteProduct(name: String): Boolean {
        return try {
            firestore.collection("Inventario")
                .document(name)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            throw e
        }
    }
}
