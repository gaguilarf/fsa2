package com.molytech.fsa.domain.usecases

import com.molytech.fsa.domain.entities.RegisterCredentials
import com.molytech.fsa.domain.entities.RegisterResult
import com.molytech.fsa.domain.repositories.AuthRepository

class RegisterUserUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(credentials: RegisterCredentials): RegisterResult {
        // Validaciones de entrada
        if (credentials.name.isBlank()) {
            return RegisterResult.Error("El nombre es requerido")
        }
        if (credentials.email.isBlank()) {
            return RegisterResult.Error("El correo es requerido")
        }
        if (credentials.password.isBlank()) {
            return RegisterResult.Error("La contraseña es requerida")
        }
        if (credentials.password.length < 6) {
            return RegisterResult.Error("La contraseña debe tener al menos 6 caracteres")
        }
        if (!isValidEmail(credentials.email)) {
            return RegisterResult.Error("El formato del correo no es válido")
        }

        return authRepository.registerWithEmailAndPassword(credentials)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}

class ValidateRegistrationFieldsUseCase {
    operator fun invoke(
        name: String,
        email: String,
        password: String
    ): String? {
        when {
            name.isBlank() -> return "El nombre es requerido"
            email.isBlank() -> return "El correo es requerido"
            password.isBlank() -> return "La contraseña es requerida"
            password.length < 6 -> return "La contraseña debe tener al menos 6 caracteres"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                return "El formato del correo no es válido"
            else -> return null
        }
    }
}
