package com.molytech.fsa.ui.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.molytech.fsa.domain.usecases.LoadUserProfileUseCase
import com.molytech.fsa.domain.usecases.UpdateUserProfileUseCase
import com.molytech.fsa.domain.usecases.ValidateProfileFieldsUseCase

class EditProfileViewModelFactory(
    private val loadUserProfileUseCase: LoadUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val validateProfileFieldsUseCase: ValidateProfileFieldsUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditProfileViewModel::class.java)) {
            return EditProfileViewModel(
                loadUserProfileUseCase,
                updateUserProfileUseCase,
                validateProfileFieldsUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
