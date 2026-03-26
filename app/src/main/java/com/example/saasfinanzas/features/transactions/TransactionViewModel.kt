package com.example.saasfinanzas.features.transactions

import androidx.lifecycle.ViewModel
import com.example.saasfinanzas.data.model.Movimiento
import com.example.saasfinanzas.data.repository.AuthRepository
import com.example.saasfinanzas.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val repository: TransactionRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _movimientos = MutableStateFlow<List<Movimiento>>(emptyList())
    val movimientos: StateFlow<List<Movimiento>> = _movimientos

    fun cargarMovimientos() {
        val uid = authRepository.getCurrentUserUid() ?: return
        viewModelScope.launch {
            val result = repository.getMovimientos(uid)
            result.onSuccess {
                _movimientos.value = it
            }
        }
    }

    fun addMovimiento(movimiento: Movimiento) {

        val uid = authRepository.getCurrentUserUid() ?: return

        viewModelScope.launch {
            val result = repository.addMovimiento(uid, movimiento)

            result.onSuccess {
                println("Guardado correctamente")
            }.onFailure {
                println("Error: ${it.message}")
            }
        }
    }
}