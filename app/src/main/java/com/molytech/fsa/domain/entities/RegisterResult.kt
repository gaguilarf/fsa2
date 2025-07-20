package com.molytech.fsa.domain.entities

data class RegisterCredentials(
    val name: String,
    val email: String,
    val password: String,
    val phone: String = "",
    val carBrand: String = "",
    val carYear: String = ""
)

sealed class RegisterResult {
    data class Success(val user: User) : RegisterResult()
    data class Error(val message: String) : RegisterResult()
    object Loading : RegisterResult()
}
