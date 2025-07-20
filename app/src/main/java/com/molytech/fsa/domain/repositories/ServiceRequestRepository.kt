package com.molytech.fsa.domain.repositories

import com.molytech.fsa.domain.entities.ServiceRequest

interface ServiceRequestRepository {
    suspend fun getAllServiceRequests(): List<ServiceRequest>
    suspend fun getServiceRequestById(id: String): ServiceRequest?
    suspend fun updateServiceRequestStatus(id: String, status: String): Boolean
}
