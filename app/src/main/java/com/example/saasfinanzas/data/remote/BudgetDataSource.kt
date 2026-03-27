package com.example.saasfinanzas.data.remote

import com.example.saasfinanzas.data.model.Movimiento
import com.example.saasfinanzas.data.model.Presupuesto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BudgetDataSource @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    suspend fun addBudget(uid: String, presupuesto: Presupuesto): Result<Unit> {
        return try {

            val docRef = firestore.collection("usuarios")
                .document(uid)
                .collection("presupuestos")
                .document() // creas el doc manualmente

            val presupuestoConId = presupuesto.copy(id = docRef.id)

            docRef.set(presupuestoConId).await()

            Result.success(Unit)


        } catch (e: Exception) {
            Result.failure(e)
        }
    }



suspend fun getBudgets(uid: String): Result<List<Presupuesto>> {
    return try {

        val snapshot = firestore.collection("usuarios")
            .document(uid)
            .collection("presupuestos")
            .get()
            .await()

        val lista = snapshot.documents.mapNotNull { doc ->
            doc.toObject(Presupuesto::class.java)?.copy(id = doc.id)
        }

        Result.success(lista)

    } catch (e: Exception) {
        Result.failure(e)
    }
}
}