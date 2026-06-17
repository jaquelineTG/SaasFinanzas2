package com.gastario.app.features.plus.premium

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PremiumViewModel @Inject constructor() : ViewModel() {

    // 1. Simulamos los precios fijos para que la UI se vea bien
    var precioMensual by mutableStateOf("$49.00 MXN")
        private set
    var precioAnual by mutableStateOf("$399.00 MXN")
        private set

    private val firestore = FirebaseFirestore.getInstance()

    // 2. Simulamos la compra exitosa y actualizamos Firestore
    fun simularCompra(uidUsuario: String, alTerminarExito: () -> Unit) {
        // Buscamos el documento del usuario en Firestore (Asegúrate de que tu colección se llame así)
        firestore.collection("usuarios").document(uidUsuario)
            .update("isPremium", true)
            .addOnSuccessListener {
                Log.d("BILLING_MOCK", "¡Compra simulada con éxito! Usuario ahora es Premium.")
                alTerminarExito()
            }
            .addOnFailureListener { error ->
                Log.e("BILLING_MOCK", "Error al actualizar Firestore: ${error.message}")
            }
    }
}