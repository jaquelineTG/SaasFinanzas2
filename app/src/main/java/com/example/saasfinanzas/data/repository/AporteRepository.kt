package com.example.saasfinanzas.data.repository

import android.net.Uri
import com.example.saasfinanzas.data.model.Aporte
import com.example.saasfinanzas.data.model.Meta
import com.example.saasfinanzas.data.remote.AporteDataSource
import javax.inject.Inject

class AporteRepository @Inject constructor(
    private val dataSource: AporteDataSource

){
    suspend fun addAporte(uid: String,aporte: Aporte) =
        dataSource.addAporte(uid,aporte)

    suspend fun cargarAportes(uid: String) =
        dataSource.cargarAportes(uid)

}