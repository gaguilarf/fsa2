package com.molytech.fsa.ui.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.molytech.fsa.domain.entities.UpdateProfileRequest
import com.molytech.fsa.domain.entities.UpdateProfileResult
import com.molytech.fsa.domain.entities.UserProfile
import com.molytech.fsa.domain.usecases.LoadUserProfileUseCase
import com.molytech.fsa.domain.usecases.UpdateUserProfileUseCase
import com.molytech.fsa.domain.usecases.ValidateProfileFieldsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val loadUserProfileUseCase: LoadUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val validateProfileFieldsUseCase: ValidateProfileFieldsUseCase
) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _updateState = MutableStateFlow<UpdateProfileResult>(UpdateProfileResult.Loading)
    val updateState: StateFlow<UpdateProfileResult> = _updateState

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val profile = loadUserProfileUseCase()
                _userProfile.value = profile
            } catch (e: Exception) {
                // Manejar error si es necesario
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(
        email: String,
        name: String,
        phone: String,
        carBrand: String,
        carYear: String,
        role: String
    ) {
        // Validar campos primero
        val validationError = validateProfileFieldsUseCase(name, phone)
        if (validationError != null) {
            _validationError.value = validationError
            return
        }

        _validationError.value = null
        _updateState.value = UpdateProfileResult.Loading

        val updateRequest = UpdateProfileRequest(
            email = email,
            name = name.trim(),
            phone = phone.trim(),
            carBrand = carBrand.trim(),
            carYear = carYear.trim(),
            role = role
        )

        viewModelScope.launch {
            val result = updateUserProfileUseCase(updateRequest)
            _updateState.value = result

            // Si la actualización fue exitosa, actualizar el perfil local
            if (result is UpdateProfileResult.Success) {
                _userProfile.value = UserProfile(
                    name = name,
                    phone = phone,
                    carBrand = carBrand,
                    carYear = carYear
                )
            }
        }
    }

    fun clearValidationError() {
        _validationError.value = null
    }

    fun resetUpdateState() {
        _updateState.value = UpdateProfileResult.Loading
    }
}
