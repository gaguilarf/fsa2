package com.molytech.fsa.data.dataSources

import com.google.firebase.firestore.FirebaseFirestore
import com.molytech.fsa.domain.entities.User
import kotlinx.coroutines.tasks.await

interface FirestoreDataSource {
    suspend fun getUserByEmail(email: String): User?
    suspend fun createUser(user: User): Boolean
    suspend fun updateUser(user: User): Boolean
}

class FirestoreDataSourceImpl(
    private val firestore: FirebaseFirestore
) : FirestoreDataSource {

    override suspend fun getUserByEmail(email: String): User? {
        return try {
            val document = firestore.collection("Usuarios").document(email).get().await()
            if (document.exists()) {
                val data = document.data!!
                User(
                    email = email,
                    name = data["usuNom"].toString(),
                    role = data["usuTip"].toString(),
                    phone = data["usuTel"].toString(),
                    year = data["usuAño"].toString(),
                    carBrand = data["usuMar"].toString()
                )
            } else {
                null
            }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun createUser(user: User): Boolean {
        return try {
            val userData = hashMapOf<String, Any>(
                "usuNom" to user.name,
                "usuCor" to user.email,
                "usuTip" to user.role,
                "usuTel" to user.phone,
                "usuAño" to user.year,
                "usuMar" to user.carBrand
            )
            firestore.collection("Usuarios").document(user.email).set(userData).await()
            true
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateUser(user: User): Boolean {
        return try {
            val userData = hashMapOf<String, Any>(
                "usuNom" to user.name,
                "usuCor" to user.email,
                "usuTip" to user.role,
                "usuTel" to user.phone,
                "usuAño" to user.year,
                "usuMar" to user.carBrand
            )
            firestore.collection("Usuarios").document(user.email).update(userData).await()
            true
        } catch (e: Exception) {
            throw e
        }
    }
}
