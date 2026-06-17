package com.gastario.app.features.user // Ponlo en una carpeta lógica

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor() : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Este es el estado que tus pantallas van a leer
    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium

    init {
        escucharEstadoPremium()
    }

    private fun escucharEstadoPremium() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // "addSnapshotListener" escucha en tiempo real. Si el usuario compra, esto se actualiza solo.
            firestore.collection("usuarios").document(currentUser.uid)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        // Lee el campo "isPremium" de Firestore. Si no existe, asume false.
                        val premiumStatus = snapshot.getBoolean("isPremium") ?: false
                        _isPremium.value = premiumStatus
                    }
                }
        }
    }
}