package com.molytech.fsa.data.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.data.dataSources.FirebaseAuthDataSource
import com.molytech.fsa.data.dataSources.FirebaseAuthDataSourceImpl
import com.molytech.fsa.data.dataSources.FirestoreDataSource
import com.molytech.fsa.data.dataSources.FirestoreDataSourceImpl
import com.molytech.fsa.data.dataSources.LocalDataSource
import com.molytech.fsa.data.dataSources.LocalDataSourceImpl
import com.molytech.fsa.data.repositories.AuthRepositoryImpl
import com.molytech.fsa.domain.repositories.AuthRepository
import com.molytech.fsa.domain.usecases.LoadUserProfileUseCase
import com.molytech.fsa.domain.usecases.UpdateUserProfileUseCase
import com.molytech.fsa.domain.usecases.ValidateProfileFieldsUseCase
import com.molytech.fsa.ui.editprofile.EditProfileViewModelFactory

object EditProfileDependencyProvider {

    fun provideEditProfileViewModelFactory(context: Context): EditProfileViewModelFactory {
        // Data sources
        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val firebaseAuthDataSource: FirebaseAuthDataSource = FirebaseAuthDataSourceImpl(firebaseAuth)
        val firestoreDataSource: FirestoreDataSource = FirestoreDataSourceImpl(firestore)
        val localDataSource: LocalDataSource = LocalDataSourceImpl(sharedPreferences)

        // Repository
        val authRepository: AuthRepository = AuthRepositoryImpl(
            firebaseAuthDataSource,
            firestoreDataSource,
            localDataSource
        )

        // Use cases
        val loadUserProfileUseCase = LoadUserProfileUseCase(authRepository)
        val updateUserProfileUseCase = UpdateUserProfileUseCase(authRepository)
        val validateProfileFieldsUseCase = ValidateProfileFieldsUseCase()

        return EditProfileViewModelFactory(
            loadUserProfileUseCase,
            updateUserProfileUseCase,
            validateProfileFieldsUseCase
        )
    }
}
