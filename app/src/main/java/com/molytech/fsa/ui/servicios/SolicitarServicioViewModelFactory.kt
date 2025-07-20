package com.molytech.fsa.ui.servicios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.molytech.fsa.domain.usecases.CreateSolicitudUseCase
import com.molytech.fsa.domain.usecases.UpdateSolicitudUseCase
import com.molytech.fsa.domain.usecases.GetActiveInventoryUseCase

class SolicitarServicioViewModelFactory(
    private val createSolicitudUseCase: CreateSolicitudUseCase,
    private val updateSolicitudUseCase: UpdateSolicitudUseCase,
    private val getActiveInventoryUseCase: GetActiveInventoryUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SolicitarServicioViewModel::class.java)) {
            return SolicitarServicioViewModel(
                createSolicitudUseCase,
                updateSolicitudUseCase,
                getActiveInventoryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
