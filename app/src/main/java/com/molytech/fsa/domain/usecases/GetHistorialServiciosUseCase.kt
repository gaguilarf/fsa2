package com.molytech.fsa.domain.usecases

import com.molytech.fsa.domain.entities.HistorialItem
import com.molytech.fsa.domain.repositories.HistorialRepository

class GetHistorialServiciosUseCase(
    private val historialRepository: HistorialRepository
) {
    suspend operator fun invoke(userEmail: String): Result<List<HistorialItem>> {
        return historialRepository.getHistorialByUserEmail(userEmail)
    }
}
