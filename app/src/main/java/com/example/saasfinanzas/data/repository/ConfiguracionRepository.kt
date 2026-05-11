package com.example.saasfinanzas.data.repository

import com.example.saasfinanzas.data.model.Aporte
import com.example.saasfinanzas.data.remote.AporteDataSource
import com.example.saasfinanzas.data.remote.ConfiguracionDataSource
import javax.inject.Inject

class ConfiguracionRepository @Inject constructor(
    private val dataSource: ConfiguracionDataSource

){
    suspend fun saveAlertStatus(uid: String, transaction: Boolean, budget: Boolean) =
        dataSource.saveStatus(uid,transaction,budget)


}