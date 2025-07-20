package com.molytech.fsa.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.molytech.fsa.domain.usecases.CheckUserLoggedInUseCase
import com.molytech.fsa.domain.usecases.GetUserRoleUseCase
import com.molytech.fsa.domain.usecases.LoginWithEmailUseCase
import com.molytech.fsa.domain.usecases.LoginWithGoogleUseCase

class LoginViewModelFactory(
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val checkUserLoggedInUseCase: CheckUserLoggedInUseCase,
    private val getUserRoleUseCase: GetUserRoleUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(
                loginWithEmailUseCase,
                loginWithGoogleUseCase,
                checkUserLoggedInUseCase,
                getUserRoleUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
