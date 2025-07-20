package com.molytech.fsa.domain.entities

data class UserProfile(
    val name: String,
    val phone: String,
    val carBrand: String,
    val carYear: String
)

data class UpdateProfileRequest(
    val email: String,
    val name: String,
    val phone: String,
    val carBrand: String,
    val carYear: String,
    val role: String
)

sealed class UpdateProfileResult {
    data class Success(val user: User) : UpdateProfileResult()
    data class Error(val message: String) : UpdateProfileResult()
    object Loading : UpdateProfileResult()
}
