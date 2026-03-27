package com.example.saasfinanzas.data.repository

import com.example.saasfinanzas.data.model.Meta
import com.example.saasfinanzas.data.model.Movimiento
import com.example.saasfinanzas.data.remote.MetaDataSource
import com.example.saasfinanzas.data.remote.TransactionDataSource
import javax.inject.Inject

class MetaRepository @Inject constructor(
    private val dataSource: MetaDataSource

){

    suspend fun cargarMetas(uid: String) =
        dataSource.cargarMetas(uid)

    suspend fun addMeta(uid: String,meta: Meta) =
        dataSource.addMeta(uid,meta)
}