package com.molytech.fsa.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.molytech.fsa.domain.usecases.SelectLocationUseCase
import com.molytech.fsa.domain.usecases.ValidateLocationUseCase

class MapViewModelFactory(
    private val selectLocationUseCase: SelectLocationUseCase,
    private val validateLocationUseCase: ValidateLocationUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            return MapViewModel(
                selectLocationUseCase,
                validateLocationUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
