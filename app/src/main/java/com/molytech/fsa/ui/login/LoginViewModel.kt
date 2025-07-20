package com.molytech.fsa.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.molytech.fsa.domain.entities.AuthResult
import com.molytech.fsa.domain.usecases.CheckUserLoggedInUseCase
import com.molytech.fsa.domain.usecases.GetUserRoleUseCase
import com.molytech.fsa.domain.usecases.LoginWithEmailUseCase
import com.molytech.fsa.domain.usecases.LoginWithGoogleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val checkUserLoggedInUseCase: CheckUserLoggedInUseCase,
    private val getUserRoleUseCase: GetUserRoleUseCase
) : ViewModel() {

    private val _loginState = MutableStateFlow<AuthResult>(AuthResult.Loading)
    val loginState: StateFlow<AuthResult> = _loginState

    private val _isUserLoggedIn = MutableStateFlow(false)
    val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    init {
        checkIfUserIsLoggedIn()
    }

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthResult.Loading
            val result = loginWithEmailUseCase(email, password)
            _loginState.value = result
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _loginState.value = AuthResult.Loading
            val result = loginWithGoogleUseCase(idToken)
            _loginState.value = result
        }
    }

    private fun checkIfUserIsLoggedIn() {
        viewModelScope.launch {
            val isLoggedIn = checkUserLoggedInUseCase()
            _isUserLoggedIn.value = isLoggedIn
            if (isLoggedIn) {
                val role = getUserRoleUseCase()
                _userRole.value = role
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = AuthResult.Loading
    }
}
