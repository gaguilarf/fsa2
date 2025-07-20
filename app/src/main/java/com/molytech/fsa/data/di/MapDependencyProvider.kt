package com.molytech.fsa.data.di

import com.molytech.fsa.domain.usecases.SelectLocationUseCase
import com.molytech.fsa.domain.usecases.ValidateLocationUseCase
import com.molytech.fsa.ui.map.MapViewModelFactory

object MapDependencyProvider {

    fun provideMapViewModelFactory(): MapViewModelFactory {
        // Use cases
        val selectLocationUseCase = SelectLocationUseCase()
        val validateLocationUseCase = ValidateLocationUseCase()

        return MapViewModelFactory(
            selectLocationUseCase,
            validateLocationUseCase
        )
    }
}
