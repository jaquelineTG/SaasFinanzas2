package com.example.saasfinanzas.data.remote

import com.example.saasfinanzas.data.model.Meta
import com.example.saasfinanzas.data.model.Movimiento
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MetaDataSource @Inject constructor(){
    private val firestore = FirebaseFirestore.getInstance()
    suspend fun addMeta(uid: String, meta: Meta): Result<Unit> {
        return try {

            val docRef = firestore.collection("usuarios")
                .document(uid)
                .collection("metas")
                .document() // creas el doc manualmente

            val metaConId = meta.copy(id = docRef.id)

            docRef.set(metaConId).await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    //obtener movimientos
    suspend fun cargarMetas(uid: String): Result<List<Meta>> {
        return try {

            val snapshot = firestore.collection("usuarios")
                .document(uid)
                .collection("metas")
                .get()
                .await()

            val lista = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Meta::class.java)?.copy(id = doc.id)
            }

            Result.success(lista)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}