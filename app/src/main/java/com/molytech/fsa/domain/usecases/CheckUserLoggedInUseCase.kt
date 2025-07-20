package com.molytech.fsa.domain.usecases

import com.molytech.fsa.domain.repositories.AuthRepository

class CheckUserLoggedInUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): Boolean {
        return authRepository.isUserLoggedIn()
    }
}
