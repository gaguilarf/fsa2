package com.molytech.fsa.domain.entities

data class LocationCoordinates(
    val latitude: Double,
    val longitude: Double
)

data class MapPoint(
    val coordinates: LocationCoordinates,
    val title: String = "Ubicación seleccionada"
)
