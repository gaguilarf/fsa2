package com.molytech.fsa.domain.usecases

import com.molytech.fsa.domain.entities.AuthResult
import com.molytech.fsa.domain.repositories.AuthRepository

class LoginWithGoogleUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(idToken: String): AuthResult {
        return authRepository.signInWithGoogle(idToken)
    }
}
