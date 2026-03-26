package com.example.saasfinanzas.features.auth



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.saasfinanzas.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor (
    private val repository: AuthRepository
) : ViewModel() {
    private val _currentUser = MutableStateFlow<String?>(null)
    val currentUser: StateFlow<String?> = _currentUser
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun register(email: String, password: String,name:String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.register(email, password, name)

            _authState.value = result.fold(
                onSuccess = { AuthState.Success(it) },
                onFailure = { AuthState.Error(it.message ?: "Error") }
            )
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = repository.login(email, password)

            _authState.value = result.fold(
                onSuccess = { AuthState.Success(it) },
                onFailure = { AuthState.Error(it.message ?: "Error") }
            )
        }
    }


    fun getCurrentUserUid() {
        val userUid = repository.getCurrentUserUid()
        _currentUser.value = userUid
    }

    fun logout(){
        repository.logout();
    }

}