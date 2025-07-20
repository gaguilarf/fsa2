package com.molytech.fsa.domain.usecases

import com.molytech.fsa.domain.entities.AuthResult
import com.molytech.fsa.domain.repositories.AuthRepository

class LoginWithEmailUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        if (email.isBlank() || password.isBlank()) {
            return AuthResult.Error("Por favor, ingresa correo y contraseña.")
        }
        return authRepository.signInWithEmailAndPassword(email, password)
    }
}
