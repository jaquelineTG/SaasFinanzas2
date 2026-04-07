package com.example.saasfinanzas.data.remote


import com.example.saasfinanzas.data.model.Usuario
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject


class AuthDataSource @Inject constructor() {

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
                "fechaRegistro" to FieldValue.serverTimestamp()
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

    suspend fun getUserData(): Result<Usuario> {
        return try {
            val uid = auth.currentUser?.uid ?: throw Exception("No user")

            val document = firestore.collection("usuarios")
                .document(uid)
                .get()
                .await()

            val usuario = Usuario(
                nombre = document.getString("nombre") ?: "",
                correo = document.getString("email") ?: ""
            )
            Result.success(usuario)

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

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Result<String> {
        return try {
            val user = auth.currentUser ?: throw Exception("No user")
            val email = user.email ?: throw Exception("No email")

            val credential = EmailAuthProvider.getCredential(email, currentPassword)

            // Reautenticar
            user.reauthenticate(credential).await()

            // Cambiar contraseña
            user.updatePassword(newPassword).await()

            Result.success("se cambio la contraseña con exito")

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}