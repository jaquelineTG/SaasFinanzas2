package com.gastario.app.data.repository

import android.net.Uri
import com.gastario.app.data.model.Meta
import com.gastario.app.data.remote.MetaDataSource
import javax.inject.Inject

class MetaRepository @Inject constructor(
    private val dataSource: MetaDataSource

){

    suspend fun cargarMetas(uid: String) =
        dataSource.cargarMetas(uid)

    suspend fun addMeta(uid: String,meta: Meta,imageUri: Uri?) =
        dataSource.addMeta(uid,meta,imageUri)
}