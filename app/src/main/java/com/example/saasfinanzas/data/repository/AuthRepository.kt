package com.example.saasfinanzas.data.repository


import com.example.saasfinanzas.data.model.Usuario
import com.example.saasfinanzas.data.remote.AuthDataSource
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val dataSource: AuthDataSource
) {

    suspend fun register(email: String, password: String, name: String) =
        dataSource.register(email, password,name)

    suspend fun getUserData(): Result<Usuario> {
        return dataSource.getUserData()
    }
    suspend fun login(email: String, password: String) =
        dataSource.login(email, password)

    fun logout() = dataSource.logout()

    fun getCurrentUser() = dataSource.getCurrentUser()

    fun getCurrentUserUid(): String? {
        return dataSource.getCurrentUser()?.uid
    }
    suspend fun changePassword(currentPassword:String, newPassword: String): Result<String> {
        return dataSource.changePassword(currentPassword, newPassword)
    }




}