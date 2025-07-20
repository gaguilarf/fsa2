package com.molytech.fsa.domain.usecases

import com.molytech.fsa.domain.entities.EmailValidationResult
import com.molytech.fsa.domain.entities.PasswordResetResult
import com.molytech.fsa.domain.repositories.AuthRepository

class ResetPasswordUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String): PasswordResetResult {
        // Validar formato del email
        val emailValidation = ValidateEmailUseCase()(email)
        if (!emailValidation.isValid) {
            return PasswordResetResult.Error(emailValidation.errorMessage ?: "Email inválido")
        }

        // Verificar si el email está registrado
        val userExists = authRepository.checkUserExists(email)
        if (!userExists) {
            return PasswordResetResult.Error("Correo no registrado")
        }

        // Enviar correo de restablecimiento
        return authRepository.sendPasswordResetEmail(email)
    }
}

class ValidateEmailUseCase {
    operator fun invoke(email: String): EmailValidationResult {
        return when {
            email.isBlank() -> EmailValidationResult(false, "El correo es requerido")
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                EmailValidationResult(false, "Correo electrónico inválido")
            else -> EmailValidationResult(true)
        }
    }
}

class CheckUserExistsUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String): Boolean {
        return authRepository.checkUserExists(email)
    }
}
