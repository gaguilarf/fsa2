package com.molytech.fsa.domain.entities

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Loading : AuthResult()
}

data class LoginCredentials(
    val email: String,
    val password: String
)
