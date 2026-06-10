package com.example.saasfinanzas.features.categorys

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saasfinanzas.data.model.Presupuesto
import com.example.saasfinanzas.data.repository.AuthRepository
import com.example.saasfinanzas.data.repository.BudgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.saasfinanzas.data.model.Categoria
import com.example.saasfinanzas.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository,
    private val authRepository: AuthRepository
): ViewModel()
{

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias


    fun addCategory(category: Categoria, onSuccess: () -> Unit){
        val uid = authRepository.getCurrentUserUid() ?: return

        viewModelScope.launch {
            val result = repository.addCategory(uid,category)

            result.onSuccess {
                println("Guardado correctamente")
                onSuccess() // <-- Le avisamos a la pantalla que ya puede cerrarse
            }.onFailure {
                println("Error: ${it.message}")
            }
        }
    }

    fun getCategory(){
        val uid = authRepository.getCurrentUserUid() ?: return

        viewModelScope.launch {
            val result = repository.getCategory(uid)


            result.onSuccess {
                    _categorias.value=it
              }

        }
    }




}