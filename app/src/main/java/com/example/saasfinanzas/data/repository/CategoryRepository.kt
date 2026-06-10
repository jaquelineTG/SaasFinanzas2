package com.example.saasfinanzas.data.repository

import com.example.saasfinanzas.data.model.Categoria
import com.example.saasfinanzas.data.model.Presupuesto
import com.example.saasfinanzas.data.remote.BudgetDataSource
import com.example.saasfinanzas.data.remote.CategoryDataSource
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val dataSource: CategoryDataSource
){
    suspend fun addCategory(uid: String,category: Categoria) =
        dataSource.addCategory(uid,category)

    suspend fun getCategory(uid: String) =
        dataSource.getCategory(uid)
}