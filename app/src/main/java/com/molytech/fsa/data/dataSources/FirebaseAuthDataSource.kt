package com.molytech.fsa.data.dataSources

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.molytech.fsa.domain.entities.User
import kotlinx.coroutines.tasks.await

interface FirebaseAuthDataSource {
    suspend fun signInWithEmailAndPassword(email: String, password: String): String?
    suspend fun signInWithGoogle(idToken: String): String?
    suspend fun registerWithEmailAndPassword(email: String, password: String): String?
    suspend fun sendPasswordResetEmail(email: String)
    fun getCurrentUserEmail(): String?
    fun getCurrentFirebaseUser(): com.google.firebase.auth.FirebaseUser?
    fun signOut()
}

class FirebaseAuthDataSourceImpl(
    private val firebaseAuth: FirebaseAuth
) : FirebaseAuthDataSource {

    override suspend fun signInWithEmailAndPassword(email: String, password: String): String? {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user?.email
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun signInWithGoogle(idToken: String): String? {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            result.user?.email
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun registerWithEmailAndPassword(email: String, password: String): String? {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.email
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    override fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    override fun getCurrentFirebaseUser(): com.google.firebase.auth.FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
