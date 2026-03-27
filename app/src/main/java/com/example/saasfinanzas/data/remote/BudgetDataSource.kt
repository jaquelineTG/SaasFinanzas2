package com.example.saasfinanzas.data.remote

import com.example.saasfinanzas.data.model.Movimiento
import com.example.saasfinanzas.data.model.Presupuesto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class BudgetDataSource @Inject constructor(){

    private val firestore = FirebaseFirestore.getInstance()
    suspend fun addBudget(uid: String, presupuesto: Presupuesto): Result<Unit> {
        return try {

            firestore.collection("usuarios")
                .document(uid)
                .collection("presupuestos")
                .add(presupuesto)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}