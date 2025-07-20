package com.molytech.fsa.domain.repositories

import com.molytech.fsa.domain.entities.SolicitudServicio

interface SolicitudServicioRepository {
    suspend fun createSolicitud(solicitud: SolicitudServicio): Result<String>
    suspend fun updateSolicitud(solicitud: SolicitudServicio): Result<Unit>
    suspend fun getSolicitudById(id: String): Result<SolicitudServicio>
}
