package com.molytech.fsa.data.repositories

import com.molytech.fsa.data.dataSources.ServiceRequestDataSource
import com.molytech.fsa.domain.entities.ServiceRequest
import com.molytech.fsa.domain.repositories.ServiceRequestRepository

class ServiceRequestRepositoryImpl(
    private val serviceRequestDataSource: ServiceRequestDataSource
) : ServiceRequestRepository {

    override suspend fun getAllServiceRequests(): List<ServiceRequest> {
        return serviceRequestDataSource.getAllServiceRequests()
    }

    override suspend fun getServiceRequestById(id: String): ServiceRequest? {
        return serviceRequestDataSource.getServiceRequestById(id)
    }

    override suspend fun updateServiceRequestStatus(id: String, status: String): Boolean {
        return serviceRequestDataSource.updateServiceRequestStatus(id, status)
    }
}
