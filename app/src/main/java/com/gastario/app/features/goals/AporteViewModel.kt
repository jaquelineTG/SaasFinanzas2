package com.gastario.app.features.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastario.app.data.model.Aporte
import com.gastario.app.data.repository.AporteRepository
import com.gastario.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AporteViewModel @Inject constructor(
    private val repository: AporteRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _aportes = MutableStateFlow<List<Aporte>>(emptyList())
    val aportes: StateFlow<List<Aporte>> = _aportes
    fun addAporte(aporte: Aporte){
        val uid= authRepository.getCurrentUserUid()?: return

        viewModelScope.launch {
            val result = repository.addAporte(uid,aporte)

            result.onSuccess {
                println("Guardado correctamente")
            }.onFailure {
                println("Error: ${it.message}")
            }
        }


    }
    fun cargarAportes() {
        val uid = authRepository.getCurrentUserUid() ?: return
        viewModelScope.launch {
            val result = repository.cargarAportes(uid)
            result.onSuccess {
                _aportes.value = it
            }
        }
    }


}