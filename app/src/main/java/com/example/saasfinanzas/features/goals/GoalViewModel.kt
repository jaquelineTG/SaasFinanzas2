package com.example.saasfinanzas.features.goals

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saasfinanzas.data.model.Meta
import com.example.saasfinanzas.data.model.Movimiento
import com.example.saasfinanzas.data.repository.AuthRepository
import com.example.saasfinanzas.data.repository.MetaRepository
import com.example.saasfinanzas.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
    class GoalViewModel @Inject constructor(
        private val repository: MetaRepository,
        private val authRepository: AuthRepository
    ) : ViewModel() {

    private val _metas = MutableStateFlow<List<Meta>>(emptyList())
    val metas: StateFlow<List<Meta>> = _metas

    fun cargarMetas() {
        val uid = authRepository.getCurrentUserUid() ?: return
        viewModelScope.launch {
            val result = repository.cargarMetas(uid)
            result.onSuccess {
                _metas.value = it
            }
        }
    }




    fun addMeta( meta: Meta,imageUri: Uri?) {

        val uid = authRepository.getCurrentUserUid() ?: return

        viewModelScope.launch {
            val result = repository.addMeta(uid,meta,imageUri)

            result.onSuccess {
                println("Guardado correctamente")
            }.onFailure {
                println("Error: ${it.message}")
            }
        }
    }




}