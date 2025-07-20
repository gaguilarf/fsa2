package com.molytech.fsa.domain.usecases

import com.molytech.fsa.domain.repositories.AuthRepository

class GetUserRoleUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): String? {
        return authRepository.getUserRole()
    }
}
