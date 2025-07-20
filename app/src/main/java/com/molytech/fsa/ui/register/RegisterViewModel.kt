package com.molytech.fsa.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.molytech.fsa.domain.entities.RegisterCredentials
import com.molytech.fsa.domain.entities.RegisterResult
import com.molytech.fsa.domain.usecases.RegisterUserUseCase
import com.molytech.fsa.domain.usecases.ValidateRegistrationFieldsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUserUseCase: RegisterUserUseCase,
    private val validateRegistrationFieldsUseCase: ValidateRegistrationFieldsUseCase
) : ViewModel() {

    private val _registerState = MutableStateFlow<RegisterResult>(RegisterResult.Loading)
    val registerState: StateFlow<RegisterResult> = _registerState

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError

    fun registerUser(
        name: String,
        email: String,
        password: String,
        phone: String = "",
        carBrand: String = "",
        carYear: String = ""
    ) {
        // Validar campos primero
        val validationError = validateRegistrationFieldsUseCase(name, email, password)
        if (validationError != null) {
            _validationError.value = validationError
            return
        }

        _validationError.value = null
        _registerState.value = RegisterResult.Loading

        val credentials = RegisterCredentials(
            name = name.trim(),
            email = email.trim(),
            password = password,
            phone = phone.trim(),
            carBrand = carBrand.trim(),
            carYear = carYear.trim()
        )

        viewModelScope.launch {
            val result = registerUserUseCase(credentials)
            _registerState.value = result
        }
    }

    fun clearValidationError() {
        _validationError.value = null
    }

    fun resetRegisterState() {
        _registerState.value = RegisterResult.Loading
    }
}
