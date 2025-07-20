package com.molytech.fsa.ui.passwordreset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.molytech.fsa.domain.usecases.ResetPasswordUseCase
import com.molytech.fsa.domain.usecases.ValidateEmailUseCase

class PasswordResetViewModelFactory(
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PasswordResetViewModel::class.java)) {
            return PasswordResetViewModel(
                resetPasswordUseCase,
                validateEmailUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
