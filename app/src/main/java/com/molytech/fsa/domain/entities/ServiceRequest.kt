package com.molytech.fsa.domain.entities

data class ServiceRequest(
    val id: String,
    val userEmail: String,
    val userName: String,
    val userPhone: String,
    val description: String,
    val date: String,
    val time: String,
    val inventory: String,
    val latitude: String,
    val longitude: String,
    val status: String
)

sealed class HistorialResult {
    data class Success(val requests: List<ServiceRequest>) : HistorialResult()
    data class Error(val message: String) : HistorialResult()
    object Loading : HistorialResult()
}
