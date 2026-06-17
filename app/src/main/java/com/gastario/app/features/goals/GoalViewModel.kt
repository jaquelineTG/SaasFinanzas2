package com.gastario.app.features.goals

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastario.app.data.model.Meta
import com.gastario.app.data.repository.AuthRepository
import com.gastario.app.data.repository.MetaRepository
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

    // 1. Agregamos el parámetro onSuccess
    fun addMeta(meta: Meta, imageUri: Uri?, onSuccess: () -> Unit) {
        val uid = authRepository.getCurrentUserUid() ?: return

        viewModelScope.launch {
            val result = repository.addMeta(uid, meta, imageUri)

            result.onSuccess {
                println("Meta e imagen guardadas correctamente")
                // 2. Avisamos a la UI que ya terminó de subir todo
                onSuccess()
            }.onFailure {
                println("Error al guardar la meta: ${it.message}")
            }
        }
    }
}