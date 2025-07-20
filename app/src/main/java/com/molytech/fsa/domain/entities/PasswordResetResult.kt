package com.molytech.fsa.domain.entities

sealed class PasswordResetResult {
    object Success : PasswordResetResult()
    data class Error(val message: String) : PasswordResetResult()
    object Loading : PasswordResetResult()
}

data class EmailValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)
