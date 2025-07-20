package com.molytech.fsa.domain.repositories

import com.molytech.fsa.domain.entities.AuthResult
import com.molytech.fsa.domain.entities.User
import com.molytech.fsa.domain.entities.RegisterCredentials
import com.molytech.fsa.domain.entities.RegisterResult
import com.molytech.fsa.domain.entities.PasswordResetResult
import com.molytech.fsa.domain.entities.UpdateProfileRequest
import com.molytech.fsa.domain.entities.UpdateProfileResult

interface AuthRepository {
    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult
    suspend fun signInWithGoogle(idToken: String): AuthResult
    suspend fun registerWithEmailAndPassword(credentials: RegisterCredentials): RegisterResult
    suspend fun sendPasswordResetEmail(email: String): PasswordResetResult
    suspend fun checkUserExists(email: String): Boolean
    suspend fun updateUserProfile(updateRequest: UpdateProfileRequest): UpdateProfileResult
    suspend fun getCurrentUser(): User?
    suspend fun getUserFromFirestore(email: String): User?
    suspend fun createGoogleUser(email: String, displayName: String): AuthResult
    suspend fun saveUserData(user: User)
    suspend fun isUserLoggedIn(): Boolean
    suspend fun getUserRole(): String?
    suspend fun signOut()
}
