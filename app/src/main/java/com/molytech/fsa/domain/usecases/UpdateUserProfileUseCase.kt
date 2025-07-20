package com.molytech.fsa.domain.usecases

import com.molytech.fsa.domain.entities.UpdateProfileRequest
import com.molytech.fsa.domain.entities.UpdateProfileResult
import com.molytech.fsa.domain.entities.UserProfile
import com.molytech.fsa.domain.repositories.AuthRepository

class UpdateUserProfileUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(updateRequest: UpdateProfileRequest): UpdateProfileResult {
        // Validaciones básicas
        if (updateRequest.name.isBlank()) {
            return UpdateProfileResult.Error("El nombre es requerido")
        }

        return authRepository.updateUserProfile(updateRequest)
    }
}

class LoadUserProfileUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(): UserProfile? {
        val user = authRepository.getCurrentUser()
        return user?.let {
            UserProfile(
                name = it.name,
                phone = it.phone,
                carBrand = it.carBrand,
                carYear = it.year
            )
        }
    }
}

class ValidateProfileFieldsUseCase {
    operator fun invoke(name: String, phone: String): String? {
        return when {
            name.isBlank() -> "El nombre es requerido"
            name.length < 2 -> "El nombre debe tener al menos 2 caracteres"
            phone.isNotBlank() && phone.length < 8 -> "El teléfono debe tener al menos 8 dígitos"
            else -> null
        }
    }
}
