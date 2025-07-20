package com.molytech.fsa.data.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.data.dataSources.CloudinaryDataSource
import com.molytech.fsa.data.dataSources.CloudinaryDataSourceImpl
import com.molytech.fsa.data.dataSources.InventoryDataSource
import com.molytech.fsa.data.dataSources.InventoryDataSourceImpl
import com.molytech.fsa.data.repositories.InventoryRepositoryImpl
import com.molytech.fsa.domain.repositories.InventoryRepository
import com.molytech.fsa.domain.usecases.FilterInventoryByStatusUseCase
import com.molytech.fsa.domain.usecases.GetActiveInventoryUseCase
import com.molytech.fsa.domain.usecases.GetAllInventoryProductsUseCase
import com.molytech.fsa.domain.usecases.SaveInventoryProductUseCase
import com.molytech.fsa.domain.usecases.UploadProductImageUseCase
import com.molytech.fsa.domain.usecases.ValidateProductFieldsUseCase
import com.molytech.fsa.ui.inventario.AgregarInventarioViewModelFactory
import com.molytech.fsa.ui.inventario.InventarioViewModelFactory

object InventoryDependencyProvider {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private fun getInventoryRepository(context: Context): InventoryRepository {
        val inventoryDataSource: InventoryDataSource = InventoryDataSourceImpl(firestore)
        val cloudinaryDataSource: CloudinaryDataSource = CloudinaryDataSourceImpl(context)
        return InventoryRepositoryImpl(inventoryDataSource, cloudinaryDataSource)
    }

    // Exponer el use case para obtener inventario activo
    fun getActiveInventoryUseCase(context: Context): GetActiveInventoryUseCase {
        return GetActiveInventoryUseCase(getInventoryRepository(context))
    }

    fun provideInventarioViewModelFactory(context: Context): InventarioViewModelFactory {
        // Repository
        val inventoryRepository = getInventoryRepository(context)

        // Use cases
        val getAllInventoryProductsUseCase = GetAllInventoryProductsUseCase(inventoryRepository)
        val filterInventoryByStatusUseCase = FilterInventoryByStatusUseCase()

        return InventarioViewModelFactory(
            getAllInventoryProductsUseCase,
            filterInventoryByStatusUseCase
        )
    }

    fun provideAgregarInventarioViewModelFactory(context: Context): AgregarInventarioViewModelFactory {
        // Repository
        val inventoryRepository = getInventoryRepository(context)

        // Use cases
        val saveInventoryProductUseCase = SaveInventoryProductUseCase(inventoryRepository)
        val uploadProductImageUseCase = UploadProductImageUseCase(inventoryRepository)
        val validateProductFieldsUseCase = ValidateProductFieldsUseCase()

        return AgregarInventarioViewModelFactory(
            saveInventoryProductUseCase,
            uploadProductImageUseCase,
            validateProductFieldsUseCase
        )
    }
}
