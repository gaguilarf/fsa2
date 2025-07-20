package com.molytech.fsa.ui.map

import androidx.lifecycle.ViewModel
import com.molytech.fsa.domain.entities.LocationCoordinates
import com.molytech.fsa.domain.entities.MapPoint
import com.molytech.fsa.domain.usecases.SelectLocationUseCase
import com.molytech.fsa.domain.usecases.ValidateLocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapViewModel(
    private val selectLocationUseCase: SelectLocationUseCase,
    private val validateLocationUseCase: ValidateLocationUseCase
) : ViewModel() {

    private val _selectedLocation = MutableStateFlow<MapPoint?>(null)
    val selectedLocation: StateFlow<MapPoint?> = _selectedLocation

    private val _isLocationValid = MutableStateFlow(false)
    val isLocationValid: StateFlow<Boolean> = _isLocationValid

    fun selectLocation(latitude: Double, longitude: Double) {
        val mapPoint = selectLocationUseCase(latitude, longitude)
        _selectedLocation.value = mapPoint
        _isLocationValid.value = validateLocationUseCase(mapPoint.coordinates)
    }

    fun getSelectedCoordinates(): LocationCoordinates? {
        return _selectedLocation.value?.coordinates
    }

    fun clearSelection() {
        _selectedLocation.value = null
        _isLocationValid.value = false
    }
}
