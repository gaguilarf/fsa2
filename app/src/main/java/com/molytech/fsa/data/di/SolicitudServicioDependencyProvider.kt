package com.molytech.fsa.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.data.repositories.SolicitudServicioRepositoryImpl
import com.molytech.fsa.domain.repositories.SolicitudServicioRepository
import com.molytech.fsa.domain.usecases.CreateSolicitudUseCase
import com.molytech.fsa.domain.usecases.UpdateSolicitudUseCase
import com.molytech.fsa.domain.usecases.GetSolicitudByIdUseCase

object SolicitudServicioDependencyProvider {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val solicitudRepository: SolicitudServicioRepository by lazy {
        SolicitudServicioRepositoryImpl(firestore)
    }

    val createSolicitudUseCase: CreateSolicitudUseCase by lazy {
        CreateSolicitudUseCase(solicitudRepository)
    }

    val updateSolicitudUseCase: UpdateSolicitudUseCase by lazy {
        UpdateSolicitudUseCase(solicitudRepository)
    }

    val getSolicitudByIdUseCase: GetSolicitudByIdUseCase by lazy {
        GetSolicitudByIdUseCase(solicitudRepository)
    }
}
