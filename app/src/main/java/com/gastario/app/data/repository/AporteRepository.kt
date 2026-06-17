package com.gastario.app.data.repository

import com.gastario.app.data.model.Aporte
import com.gastario.app.data.remote.AporteDataSource
import javax.inject.Inject

class AporteRepository @Inject constructor(
    private val dataSource: AporteDataSource

){
    suspend fun addAporte(uid: String,aporte: Aporte) =
        dataSource.addAporte(uid,aporte)

    suspend fun cargarAportes(uid: String) =
        dataSource.cargarAportes(uid)

}