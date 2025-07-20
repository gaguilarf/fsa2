package com.molytech.fsa.data.repositories

import com.molytech.fsa.data.dataSources.FirebaseAuthDataSource
import com.molytech.fsa.data.dataSources.FirestoreDataSource
import com.molytech.fsa.data.dataSources.LocalDataSource
import com.molytech.fsa.domain.entities.AuthResult
import com.molytech.fsa.domain.entities.RegisterCredentials
import com.molytech.fsa.domain.entities.RegisterResult
import com.molytech.fsa.domain.entities.User
import com.molytech.fsa.domain.repositories.AuthRepository
import com.molytech.fsa.domain.entities.PasswordResetResult
import com.molytech.fsa.domain.entities.UpdateProfileRequest
import com.molytech.fsa.domain.entities.UpdateProfileResult

class AuthRepositoryImpl(
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val firestoreDataSource: FirestoreDataSource,
    private val localDataSource: LocalDataSource
) : AuthRepository {

    override suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult {
        return try {
            val userEmail = firebaseAuthDataSource.signInWithEmailAndPassword(email, password)
            if (userEmail != null) {
                val user = getUserFromFirestore(userEmail)
                if (user != null) {
                    saveUserData(user)
                    AuthResult.Success(user)
                } else {
                    AuthResult.Error("Usuario no encontrado en la base de datos.")
                }
            } else {
                AuthResult.Error("Datos incorrectos.")
            }
        } catch (e: Exception) {
            AuthResult.Error("Error de autenticación: ${e.message}")
        }
    }

    override suspend fun signInWithGoogle(idToken: String): AuthResult {
        return try {
            val userEmail = firebaseAuthDataSource.signInWithGoogle(idToken)
            if (userEmail != null) {
                val existingUser = getUserFromFirestore(userEmail)
                if (existingUser != null) {
                    // Usuario ya existe en Firestore
                    saveUserData(existingUser)
                    AuthResult.Success(existingUser)
                } else {
                    // Usuario no existe en Firestore, crearlo automáticamente
                    val firebaseUser = firebaseAuthDataSource.getCurrentFirebaseUser()
                    val displayName = firebaseUser?.displayName ?: "Usuario Google"

                    val newUser = User(
                        email = userEmail,
                        name = displayName,
                        role = "0", // Rol por defecto para usuarios normales
                        phone = "",
                        year = "",
                        carBrand = ""
                    )

                    val created = firestoreDataSource.createUser(newUser)
                    if (created) {
                        saveUserData(newUser)
                        AuthResult.Success(newUser)
                    } else {
                        AuthResult.Error("Error al crear usuario en la base de datos")
                    }
                }
            } else {
                AuthResult.Error("Error en la autenticación con Google.")
            }
        } catch (e: Exception) {
            AuthResult.Error("Error de autenticación con Google: ${e.message}")
        }
    }

    override suspend fun getUserFromFirestore(email: String): User? {
        return try {
            firestoreDataSource.getUserByEmail(email)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createGoogleUser(email: String, displayName: String): AuthResult {
        return try {
            val newUser = User(
                email = email,
                name = displayName,
                role = "0",
                phone = "",
                year = "",
                carBrand = ""
            )
            val created = firestoreDataSource.createUser(newUser)
            if (created) {
                saveUserData(newUser)
                AuthResult.Success(newUser)
            } else {
                AuthResult.Error("Error al crear usuario")
            }
        } catch (e: Exception) {
            AuthResult.Error("Error al crear usuario: ${e.message}")
        }
    }

    override suspend fun saveUserData(user: User) {
        localDataSource.saveUser(user)
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return localDataSource.isUserLoggedIn()
    }

    override suspend fun getUserRole(): String? {
        return localDataSource.getUserRole()
    }

    override suspend fun signOut() {
        firebaseAuthDataSource.signOut()
        localDataSource.clearUserData()
    }

    override suspend fun registerWithEmailAndPassword(credentials: RegisterCredentials): RegisterResult {
        return try {
            // Crear usuario en Firebase Auth
            val userEmail = firebaseAuthDataSource.registerWithEmailAndPassword(
                credentials.email,
                credentials.password
            )

            if (userEmail != null) {
                // Crear usuario en Firestore con los datos completos
                val newUser = User(
                    email = credentials.email,
                    name = credentials.name,
                    role = "0", // Rol por defecto para usuarios normales
                    phone = credentials.phone,
                    year = credentials.carYear,
                    carBrand = credentials.carBrand
                )

                val created = firestoreDataSource.createUser(newUser)

                if (created) {
                    saveUserData(newUser)
                    RegisterResult.Success(newUser)
                } else {
                    RegisterResult.Error("Error al crear usuario en la base de datos")
                }
            } else {
                RegisterResult.Error("Error en la autenticación")
            }
        } catch (e: Exception) {
            RegisterResult.Error("Error de registro: ${e.message}")
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): PasswordResetResult {
        return try {
            firebaseAuthDataSource.sendPasswordResetEmail(email)
            PasswordResetResult.Success
        } catch (e: Exception) {
            PasswordResetResult.Error("Error al enviar el correo: ${e.message}")
        }
    }

    override suspend fun checkUserExists(email: String): Boolean {
        return try {
            val user = firestoreDataSource.getUserByEmail(email)
            user != null
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun updateUserProfile(updateRequest: UpdateProfileRequest): UpdateProfileResult {
        return try {
            // Crear el usuario actualizado
            val updatedUser = User(
                email = updateRequest.email,
                name = updateRequest.name,
                role = updateRequest.role,
                phone = updateRequest.phone,
                year = updateRequest.carYear,
                carBrand = updateRequest.carBrand
            )

            // Actualizar en Firestore
            val updated = firestoreDataSource.updateUser(updatedUser)

            if (updated) {
                // Actualizar también en almacenamiento local
                saveUserData(updatedUser)
                UpdateProfileResult.Success(updatedUser)
            } else {
                UpdateProfileResult.Error("Error al actualizar el perfil")
            }
        } catch (e: Exception) {
            UpdateProfileResult.Error("Error al actualizar perfil: ${e.message}")
        }
    }

    override suspend fun getCurrentUser(): User? {
        return localDataSource.getUser()
    }
}
