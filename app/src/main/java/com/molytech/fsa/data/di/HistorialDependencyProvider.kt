package com.molytech.fsa.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.data.repositories.HistorialRepositoryImpl
import com.molytech.fsa.domain.repositories.HistorialRepository
import com.molytech.fsa.domain.usecases.GetHistorialServiciosUseCase

object HistorialDependencyProvider {

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val historialRepository: HistorialRepository by lazy {
        HistorialRepositoryImpl(firestore)
    }

    val getHistorialServiciosUseCase: GetHistorialServiciosUseCase by lazy {
        GetHistorialServiciosUseCase(historialRepository)
    }
}
