package com.molytech.fsa.data.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.data.dataSources.ServiceRequestDataSource
import com.molytech.fsa.data.dataSources.ServiceRequestDataSourceImpl
import com.molytech.fsa.data.repositories.ServiceRequestRepositoryImpl
import com.molytech.fsa.domain.repositories.ServiceRequestRepository
import com.molytech.fsa.domain.usecases.FilterServiceRequestsByStatusUseCase
import com.molytech.fsa.domain.usecases.GetAllServiceRequestsUseCase
import com.molytech.fsa.domain.usecases.SortServiceRequestsByDateUseCase
import com.molytech.fsa.ui.historial.AdminHistorialViewModelFactory

object AdminHistorialDependencyProvider {

    fun provideAdminHistorialViewModelFactory(context: Context): AdminHistorialViewModelFactory {
        // Data sources
        val firestore = FirebaseFirestore.getInstance()
        val serviceRequestDataSource: ServiceRequestDataSource = ServiceRequestDataSourceImpl(firestore)

        // Repository
        val serviceRequestRepository: ServiceRequestRepository = ServiceRequestRepositoryImpl(
            serviceRequestDataSource
        )

        // Use cases
        val getAllServiceRequestsUseCase = GetAllServiceRequestsUseCase(serviceRequestRepository)
        val filterServiceRequestsByStatusUseCase = FilterServiceRequestsByStatusUseCase()
        val sortServiceRequestsByDateUseCase = SortServiceRequestsByDateUseCase()

        return AdminHistorialViewModelFactory(
            getAllServiceRequestsUseCase,
            filterServiceRequestsByStatusUseCase,
            sortServiceRequestsByDateUseCase
        )
    }
}
