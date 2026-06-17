package com.gastario.app.data.repository

import com.gastario.app.data.model.Presupuesto
import com.gastario.app.data.remote.BudgetDataSource
import javax.inject.Inject

class BudgetRepository  @Inject constructor(
    private val dataSource: BudgetDataSource
){

    suspend fun addBudget(uid: String,presupuesto: Presupuesto) =
        dataSource.addBudget(uid,presupuesto)
    suspend fun getBudgets(uid: String) =
        dataSource.getBudgets(uid)

    suspend fun deleteBudget(
        uid: String,
        budgetId: String
    ) = dataSource.deleteBudget(uid, budgetId)

    suspend fun updateBudget(
        uid: String,
        presupuesto: Presupuesto
    ) = dataSource.updateBudget(uid, presupuesto)
}