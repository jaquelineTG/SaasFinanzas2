package com.example.saasfinanzas.data.remote

import com.example.saasfinanzas.data.model.Movimiento
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TransactionDataSource  @Inject constructor(){
    private val firestore = FirebaseFirestore.getInstance()
    suspend fun addMovimiento(uid: String, movimiento: Movimiento): Result<Unit> {
        return try {

            firestore.collection("usuarios")
                .document(uid)
                .collection("movimientos")
                .add(movimiento)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    //obtener movimientos
    suspend fun getMovimientos(uid: String): Result<List<Movimiento>> {
        return try {

            val snapshot = firestore.collection("usuarios")
                .document(uid)
                .collection("movimientos")
                .get()
                .await()

            val lista = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Movimiento::class.java)?.copy(id = doc.id)
            }

            Result.success(lista)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}