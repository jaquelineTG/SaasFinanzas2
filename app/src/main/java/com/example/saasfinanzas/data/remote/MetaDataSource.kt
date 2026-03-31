package com.example.saasfinanzas.data.remote

import android.net.Uri
import com.example.saasfinanzas.data.model.Meta
import com.example.saasfinanzas.data.model.Movimiento
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MetaDataSource @Inject constructor(){
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun addMeta(uid: String, meta: Meta, imageUri: Uri?): Result<Unit> {
        return try {

            val docRef = firestore.collection("usuarios")
                .document(uid)
                .collection("metas")
                .document()

            //  subir imagen si existe
            val imageUrl = imageUri?.let {
                uploadImage(uid, it)
            } ?: ""

            val metaConId = meta.copy(
                id = docRef.id,
                imageUrl = imageUrl
            )

            docRef.set(metaConId).await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadImage(uid: String, uri: Uri): String {
        val ref = storage.reference
            .child("metas/$uid/${System.currentTimeMillis()}.jpg")

        ref.putFile(uri).await()

        return ref.downloadUrl.await().toString()
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