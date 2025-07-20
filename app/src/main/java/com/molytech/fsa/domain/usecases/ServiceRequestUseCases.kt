package com.molytech.fsa.domain.usecases

import com.molytech.fsa.domain.entities.HistorialResult
import com.molytech.fsa.domain.entities.ServiceRequest
import com.molytech.fsa.domain.repositories.ServiceRequestRepository

class GetAllServiceRequestsUseCase(
    private val serviceRequestRepository: ServiceRequestRepository
) {
    suspend operator fun invoke(): HistorialResult {
        return try {
            val requests = serviceRequestRepository.getAllServiceRequests()
            HistorialResult.Success(requests)
        } catch (e: Exception) {
            HistorialResult.Error("Error al cargar las solicitudes: ${e.message}")
        }
    }
}

class FilterServiceRequestsByStatusUseCase {
    operator fun invoke(
        requests: List<ServiceRequest>,
        status: String? = null
    ): List<ServiceRequest> {
        return if (status.isNullOrEmpty()) {
            requests
        } else {
            requests.filter { it.status == status }
        }
    }
}

class SortServiceRequestsByDateUseCase {
    operator fun invoke(requests: List<ServiceRequest>): List<ServiceRequest> {
        return requests.sortedBy { it.date }
    }
}
