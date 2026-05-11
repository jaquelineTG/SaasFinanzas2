package com.example.saasfinanzas.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ConfiguracionDataSource @Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun saveStatus(uid: String, transaction: Boolean, budget: Boolean): Result<Unit> {
        return try {
            // Creamos un mapa con los datos a actualizar en el documento del usuario
            val updates = mapOf(
                "transactionAlerts" to transaction,
                "budgetAlerts" to budget
            )

            // Actualizamos directamente el documento del usuario
            firestore.collection("usuarios")
                .document(uid)
                .update(updates)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}