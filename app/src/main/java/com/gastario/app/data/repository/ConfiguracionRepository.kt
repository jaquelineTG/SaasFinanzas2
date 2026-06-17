package com.gastario.app.data.repository

import com.gastario.app.data.remote.ConfiguracionDataSource
import javax.inject.Inject

class ConfiguracionRepository @Inject constructor(
    private val dataSource: ConfiguracionDataSource

){
    suspend fun saveAlertStatus(uid: String, transaction: Boolean, budget: Boolean) =
        dataSource.saveStatus(uid,transaction,budget)


}