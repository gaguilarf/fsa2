package com.molytech.fsa.ui.historial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.molytech.fsa.domain.usecases.FilterServiceRequestsByStatusUseCase
import com.molytech.fsa.domain.usecases.GetAllServiceRequestsUseCase
import com.molytech.fsa.domain.usecases.SortServiceRequestsByDateUseCase

class AdminHistorialViewModelFactory(
    private val getAllServiceRequestsUseCase: GetAllServiceRequestsUseCase,
    private val filterServiceRequestsByStatusUseCase: FilterServiceRequestsByStatusUseCase,
    private val sortServiceRequestsByDateUseCase: SortServiceRequestsByDateUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminHistorialViewModel::class.java)) {
            return AdminHistorialViewModel(
                getAllServiceRequestsUseCase,
                filterServiceRequestsByStatusUseCase,
                sortServiceRequestsByDateUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
