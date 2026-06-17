package com.gastario.app.data.repository

import com.gastario.app.data.model.Categoria
import com.gastario.app.data.remote.CategoryDataSource
import javax.inject.Inject

class CategoryRepository @Inject constructor(
    private val dataSource: CategoryDataSource
){
    suspend fun addCategory(uid: String,category: Categoria) =
        dataSource.addCategory(uid,category)

    suspend fun getCategory(uid: String) =
        dataSource.getCategory(uid)
}