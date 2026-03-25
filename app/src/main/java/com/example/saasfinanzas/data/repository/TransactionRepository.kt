package com.example.saasfinanzas.data.repository

import com.example.saasfinanzas.data.remote.AuthDataSource
import com.example.saasfinanzas.data.remote.TransactionDataSource
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val dataSource: TransactionDataSource
){

    suspend fun getMovimientos(uid: String) =
        dataSource.getMovimientos(uid)


}