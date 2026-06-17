package com.gastario.app.features.plus.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gastario.app.data.model.Usuario
import com.gastario.app.data.repository.AuthRepository
import com.gastario.app.data.repository.ConfiguracionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val respositoryConf: ConfiguracionRepository
) : ViewModel() {
                //dato string
//    private val _currentUser = MutableStateFlow("")
//    val currentUser: StateFlow<String> = _currentUser

                //dato objeto
    private val _currentUser = MutableStateFlow<Usuario?>(null)
    val currentUser: StateFlow<Usuario?> = _currentUser

    private val _menssage = MutableStateFlow<String?>(null)
    val menssage : StateFlow<String?> = _menssage

    private val _transactionAlerts = MutableStateFlow(false) // Puedes poner tu valor por defecto
    val transactionAlerts : StateFlow<Boolean> = _transactionAlerts

    private val _budgetAlerts = MutableStateFlow(false)
    val budgetAlerts : StateFlow<Boolean> = _budgetAlerts

    fun userData(){
        viewModelScope.launch {
            val result = repository.getUserData()

            result.onSuccess { usuario ->
                // Guardamos el usuario
                _currentUser.value = usuario

                // Si el usuario se descargó correctamente,
                // actualizamos los estados de las alertas con los datos de Firebase
                if (usuario != null) {
                    _transactionAlerts.value = usuario.transactionAlerts
                    _budgetAlerts.value = usuario.budgetAlerts
                }
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



    fun saveAlertsConfig(transaction: Boolean, budget: Boolean) {
        val uid = repository.getCurrentUserUid() ?: return

        // Actualizamos la UI al instante para que se sienta rápida
        _transactionAlerts.value = transaction
        _budgetAlerts.value = budget

        viewModelScope.launch {
            val result = respositoryConf.saveAlertStatus(uid, transaction, budget)

            result.onFailure {
                // Si falla en Firebase, podrías revertir los switches o mostrar un error
                _menssage.value = "Error al guardar preferencias"
            }
        }
    }
    }


