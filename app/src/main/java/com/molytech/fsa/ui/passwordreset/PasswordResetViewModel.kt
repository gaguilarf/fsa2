package com.molytech.fsa.ui.passwordreset

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.molytech.fsa.domain.entities.PasswordResetResult
import com.molytech.fsa.domain.usecases.ResetPasswordUseCase
import com.molytech.fsa.domain.usecases.ValidateEmailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PasswordResetViewModel(
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val validateEmailUseCase: ValidateEmailUseCase
) : ViewModel() {

    private val _resetState = MutableStateFlow<PasswordResetResult>(PasswordResetResult.Loading)
    val resetState: StateFlow<PasswordResetResult> = _resetState

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError

    fun resetPassword(email: String) {
        // Validar email primero
        val emailValidation = validateEmailUseCase(email)
        if (!emailValidation.isValid) {
            _validationError.value = emailValidation.errorMessage
            return
        }

        _validationError.value = null
        _resetState.value = PasswordResetResult.Loading

        viewModelScope.launch {
            val result = resetPasswordUseCase(email.trim())
            _resetState.value = result
        }
    }

    fun clearValidationError() {
        _validationError.value = null
    }

    fun resetState() {
        _resetState.value = PasswordResetResult.Loading
    }
}
