package com.example.saasfinanzas.features.plus.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saasfinanzas.data.model.Usuario
import com.example.saasfinanzas.data.repository.AuthRepository
import com.google.firebase.auth.EmailAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {
                //dato string
//    private val _currentUser = MutableStateFlow("")
//    val currentUser: StateFlow<String> = _currentUser

                //dato objeto
    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser

    private val _menssage = MutableStateFlow<String?>(null)
    val menssage : StateFlow<String?> = _menssage

    fun userData(){
        viewModelScope.launch {
            val result = repository.getUserData()

            result.onSuccess {
                _currentUser.value= it
            }
        }

    }
    fun CambiarContraseña( currentPassword: String, newPassword: String,confirmPassword:String){
        viewModelScope.launch {

            if (newPassword != confirmPassword) {
                _menssage.value = "Las contraseñas no coinciden"
                return@launch
            }

            if (newPassword.length < 6) {
                _menssage.value = "Mínimo 6 caracteres"
                return@launch
            }
            val result = repository.changePassword(currentPassword,newPassword)

            result.onSuccess {
                _menssage.value= it
            }
        }
    }
}

