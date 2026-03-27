package com.example.saasfinanzas.data.repository

import com.example.saasfinanzas.data.model.Movimiento
import com.example.saasfinanzas.data.model.Presupuesto
import com.example.saasfinanzas.data.remote.BudgetDataSource
import com.example.saasfinanzas.data.remote.TransactionDataSource
import javax.inject.Inject

class BudgetRepository  @Inject constructor(
    private val dataSource: BudgetDataSource
){

    suspend fun addBudget(uid: String,presupuesto: Presupuesto) =
        dataSource.addBudget(uid,presupuesto)
}