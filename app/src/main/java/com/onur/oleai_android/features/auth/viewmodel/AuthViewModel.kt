package com.onur.oleai_android.features.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onur.oleai_android.core.network.dto.LoginRequest
import com.onur.oleai_android.core.network.dto.RegisterRequest
import com.onur.oleai_android.features.auth.data.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess.asStateFlow()

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = authRepository.login(request)
            result.onSuccess {
                _loginSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "Bilinmeyen bir hata oluştu"
            }
            
            _isLoading.value = false
        }
    }

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = authRepository.register(request)
            result.onSuccess {
                _registerSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "Kayıt işlemi başarısız"
            }
            
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
