package com.example.saasfinanzas.data.remote

import android.net.Uri
import com.example.saasfinanzas.data.model.Aporte
import com.example.saasfinanzas.data.model.Meta
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AporteDataSource@Inject constructor() {

    private val firestore = FirebaseFirestore.getInstance()


    suspend fun addAporte(uid: String, aporte: Aporte): Result<Unit> {
        return try {

            val docRef = firestore.collection("usuarios")
                .document(uid)
                .collection("aportes")
                .document()


            val aporteConId = aporte.copy(
                id = docRef.id
            )

            docRef.set(aporteConId).await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cargarAportes(uid: String): Result<List<Aporte>> {
        return try {

            val snapshot = firestore.collection("usuarios")
                .document(uid)
                .collection("aportes")
                .get()
                .await()

            val lista = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Aporte::class.java)?.copy(id = doc.id)
            }

            Result.success(lista)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}