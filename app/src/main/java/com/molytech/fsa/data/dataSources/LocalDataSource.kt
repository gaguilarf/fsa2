package com.molytech.fsa.data.dataSources

import android.content.SharedPreferences
import com.molytech.fsa.domain.entities.User
import androidx.core.content.edit

interface LocalDataSource {
    suspend fun saveUser(user: User)
    suspend fun getUser(): User?
    suspend fun isUserLoggedIn(): Boolean
    suspend fun getUserRole(): String?
    suspend fun clearUserData()
}

class LocalDataSourceImpl(
    private val sharedPreferences: SharedPreferences
) : LocalDataSource {

    override suspend fun saveUser(user: User) {
        val editor = sharedPreferences.edit()
        editor.putString("usuCor", user.email)
        editor.putString("usuNom", user.name)
        editor.putString("usuTip", user.role)
        editor.putString("usuTel", user.phone)
        editor.putString("usuAño", user.year)
        editor.putString("usuMar", user.carBrand)
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

    override suspend fun getUser(): User? {
        val email = sharedPreferences.getString("usuCor", null) ?: return null
        val name = sharedPreferences.getString("usuNom", "") ?: ""
        val role = sharedPreferences.getString("usuTip", "") ?: ""
        val phone = sharedPreferences.getString("usuTel", "") ?: ""
        val year = sharedPreferences.getString("usuAño", "") ?: ""
        val carBrand = sharedPreferences.getString("usuMar", "") ?: ""

        return User(email, name, role, phone, year, carBrand)
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    override suspend fun getUserRole(): String? {
        return sharedPreferences.getString("usuTip", null)
    }

    override suspend fun clearUserData() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
