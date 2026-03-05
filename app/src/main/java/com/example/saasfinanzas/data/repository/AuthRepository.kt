package com.example.saasfinanzas.data.repository


import com.example.saasfinanzas.data.remote.AuthDataSource

class AuthRepository(
    private val dataSource: AuthDataSource
) {

    suspend fun register(email: String, password: String) =
        dataSource.register(email, password)

    suspend fun login(email: String, password: String) =
        dataSource.login(email, password)

    fun logout() = dataSource.logout()

    fun getCurrentUser() = dataSource.getCurrentUser()
}