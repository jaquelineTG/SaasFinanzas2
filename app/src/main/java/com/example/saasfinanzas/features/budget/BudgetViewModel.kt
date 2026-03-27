package com.example.saasfinanzas.features.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saasfinanzas.data.model.Movimiento
import com.example.saasfinanzas.data.model.Presupuesto
import com.example.saasfinanzas.data.repository.AuthRepository
import com.example.saasfinanzas.data.repository.BudgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class BudgetViewModel@Inject constructor(
    private val repository: BudgetRepository,
    private val authRepository: AuthRepository
): ViewModel()
{
    private val _presupuestos = MutableStateFlow<List<Presupuesto>>(emptyList())
    val presupuestos: StateFlow<List<Presupuesto>> = _presupuestos


    fun addBudget(presupuesto: Presupuesto){
        val uid = authRepository.getCurrentUserUid() ?: return

        viewModelScope.launch {
            val result = repository.addBudget(uid,presupuesto)

            result.onSuccess {
                println("Guardado correctamente")
            }.onFailure {
                println("Error: ${it.message}")
            }
        }
    }


    fun getBudgets(){
        val uid = authRepository.getCurrentUserUid() ?: return

        viewModelScope.launch {
            val result=repository.getBudgets(uid)

            result.onSuccess {
                _presupuestos.value=it
            }
        }
    }





}