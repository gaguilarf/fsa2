package com.molytech.fsa.data.di

import android.content.Context
import android.content.SharedPreferences
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
import com.molytech.fsa.domain.usecases.CheckUserLoggedInUseCase
import com.molytech.fsa.domain.usecases.GetUserRoleUseCase
import com.molytech.fsa.domain.usecases.LoginWithEmailUseCase
import com.molytech.fsa.domain.usecases.LoginWithGoogleUseCase
import com.molytech.fsa.ui.login.LoginViewModelFactory

object LoginDependencyProvider {

    fun provideLoginViewModelFactory(context: Context): LoginViewModelFactory {
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
        val loginWithEmailUseCase = LoginWithEmailUseCase(authRepository)
        val loginWithGoogleUseCase = LoginWithGoogleUseCase(authRepository)
        val checkUserLoggedInUseCase = CheckUserLoggedInUseCase(authRepository)
        val getUserRoleUseCase = GetUserRoleUseCase(authRepository)

        return LoginViewModelFactory(
            loginWithEmailUseCase,
            loginWithGoogleUseCase,
            checkUserLoggedInUseCase,
            getUserRoleUseCase
        )
    }
}
