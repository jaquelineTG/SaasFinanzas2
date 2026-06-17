package com.gastario.app.data.remote

import com.gastario.app.data.model.Categoria
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

    suspend fun getCategory(uid: String): Result<List<Categoria>> {
        return try {

            val docRef = firestore.collection("usuarios")
                .document(uid)
                .collection("categorias")
                .get()
                .await()

            val lista = docRef.documents.mapNotNull { doc ->
                doc.toObject(Categoria::class.java)?.copy(id = doc.id)
            }

            Result.success(lista)




        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}