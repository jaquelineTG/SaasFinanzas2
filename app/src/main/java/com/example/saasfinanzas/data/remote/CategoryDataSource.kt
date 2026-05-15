package com.example.saasfinanzas.data.remote

import com.example.saasfinanzas.data.model.Categoria
import com.example.saasfinanzas.data.model.Presupuesto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CategoryDataSource @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    suspend fun addCategory(uid: String, category: Categoria): Result<Unit> {
        return try {

            val docRef = firestore.collection("usuarios")
                .document(uid)
                .collection("categorias")
                .document() // creas el doc manualmente

            val categoriaConId = category.copy(id = docRef.id)

            docRef.set(categoriaConId).await()

            Result.success(Unit)


        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}