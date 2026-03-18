package com.example.saasfinanzas.data.remote


import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FirebaseFirestore



class AuthDataSource {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

//    suspend fun register(
//        email: String,
//        password: String
//    ): Result<String> {
//        return try {
//            val result = auth
//                .createUserWithEmailAndPassword(email, password)
//                .await()
//
//            Result.success(result.user?.uid ?: "")
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
private val firestore = FirebaseFirestore.getInstance()

    suspend fun register(
        email: String,
        password: String,
        name: String
    ): Result<String> {
        return try {
            val result = auth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val uid = result.user?.uid ?: throw Exception("No UID")

            val userData = hashMapOf(
                "email" to email,
                "nombre" to name,
                "fechaRegistro" to System.currentTimeMillis()
            )

            firestore.collection("usuarios")
                .document(uid)
                .set(userData)
                .await()

            Result.success(uid)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserData(): Result<String> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("No user")

            val document = firestore.collection("usuarios")
                .document(uid)
                .get()
                .await()

            val nombre = document.getString("nombre") ?: ""

            Result.success(nombre)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(
        email: String,
        password: String
    ): Result<String> {
        return try {
            val result = auth
                .signInWithEmailAndPassword(email, password)
                .await()

            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser
}