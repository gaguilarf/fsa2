package com.molytech.fsa.ui.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.molytech.fsa.domain.usecases.GetHistorialServiciosUseCase

class HistorialServiciosViewModelFactory(
    private val getHistorialServiciosUseCase: GetHistorialServiciosUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistorialServiciosViewModel::class.java)) {
            return HistorialServiciosViewModel(getHistorialServiciosUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
