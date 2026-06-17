package com.gastario.app.data.repository


import com.gastario.app.data.model.Usuario
import com.gastario.app.data.remote.AuthDataSource
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
    suspend fun updateFcmToken(token: String): Result<Unit> {
        return dataSource.updateFcmToken(token)
    }
    suspend fun loginWithGoogle(idToken: String): Result<String> {
        return dataSource.loginWithGoogle(idToken)
    }


}