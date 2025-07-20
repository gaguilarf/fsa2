package com.molytech.fsa.domain.usecases

import com.molytech.fsa.domain.entities.LocationCoordinates
import com.molytech.fsa.domain.entities.MapPoint

class SelectLocationUseCase {
    operator fun invoke(latitude: Double, longitude: Double): MapPoint {
        val coordinates = LocationCoordinates(latitude, longitude)
        return MapPoint(coordinates, "Ubicación seleccionada")
    }
}

class ValidateLocationUseCase {
    operator fun invoke(coordinates: LocationCoordinates?): Boolean {
        return coordinates != null &&
               coordinates.latitude != 0.0 &&
               coordinates.longitude != 0.0
    }
}
