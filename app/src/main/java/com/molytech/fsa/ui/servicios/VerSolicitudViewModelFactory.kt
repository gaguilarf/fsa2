package com.molytech.fsa.ui.servicios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.molytech.fsa.domain.usecases.UpdateSolicitudUseCase
import com.molytech.fsa.domain.usecases.GetSolicitudByIdUseCase
import com.molytech.fsa.domain.usecases.GetActiveInventoryUseCase

class VerSolicitudViewModelFactory(
    private val updateSolicitudUseCase: UpdateSolicitudUseCase,
    private val getSolicitudByIdUseCase: GetSolicitudByIdUseCase,
    private val getActiveInventoryUseCase: GetActiveInventoryUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VerSolicitudViewModel::class.java)) {
            return VerSolicitudViewModel(
                updateSolicitudUseCase,
                getSolicitudByIdUseCase,
                getActiveInventoryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
