package com.molytech.fsa.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.molytech.fsa.domain.usecases.RegisterUserUseCase
import com.molytech.fsa.domain.usecases.ValidateRegistrationFieldsUseCase

class RegisterViewModelFactory(
    private val registerUserUseCase: RegisterUserUseCase,
    private val validateRegistrationFieldsUseCase: ValidateRegistrationFieldsUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(
                registerUserUseCase,
                validateRegistrationFieldsUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
