package com.molytech.fsa.domain.usecases

import com.molytech.fsa.domain.entities.SolicitudServicio
import com.molytech.fsa.domain.repositories.SolicitudServicioRepository

class CreateSolicitudUseCase(
    private val solicitudRepository: SolicitudServicioRepository
) {
    suspend operator fun invoke(solicitud: SolicitudServicio): Result<String> {
        return solicitudRepository.createSolicitud(solicitud)
    }
}

class UpdateSolicitudUseCase(
    private val solicitudRepository: SolicitudServicioRepository
) {
    suspend operator fun invoke(solicitud: SolicitudServicio): Result<Unit> {
        return solicitudRepository.updateSolicitud(solicitud)
    }
}

class GetSolicitudByIdUseCase(
    private val solicitudRepository: SolicitudServicioRepository
) {
    suspend operator fun invoke(id: String): Result<SolicitudServicio> {
        return solicitudRepository.getSolicitudById(id)
    }
}
