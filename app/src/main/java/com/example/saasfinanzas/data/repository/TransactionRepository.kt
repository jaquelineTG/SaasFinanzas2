package com.example.saasfinanzas.data.repository

import com.example.saasfinanzas.data.model.Movimiento
import com.example.saasfinanzas.data.remote.AuthDataSource
import com.example.saasfinanzas.data.remote.TransactionDataSource
import javax.inject.Inject

class TransactionRepository @Inject constructor(
    private val dataSource: TransactionDataSource
){

    suspend fun getMovimientos(uid: String) =
        dataSource.getMovimientos(uid)

    suspend fun addMovimiento(uid: String,movimiento: Movimiento) =
        dataSource.addMovimiento(uid,movimiento)
}