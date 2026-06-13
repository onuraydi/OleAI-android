package com.onur.oleai_android.features.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onur.oleai_android.core.network.dto.LoginRequest
import com.onur.oleai_android.core.network.dto.RegisterRequest
import com.onur.oleai_android.core.network.dto.ForgotPasswordRequest
import com.onur.oleai_android.core.network.dto.ResetPasswordRequest
import com.onur.oleai_android.core.network.dto.VerifyEmailRequest
import com.onur.oleai_android.core.network.dto.ResendOtpCodeRequest
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

    private val _forgotPasswordSuccess = MutableStateFlow(false)
    val forgotPasswordSuccess: StateFlow<Boolean> = _forgotPasswordSuccess.asStateFlow()

    private val _resetPasswordSuccess = MutableStateFlow(false)
    val resetPasswordSuccess: StateFlow<Boolean> = _resetPasswordSuccess.asStateFlow()

    private val _verifyEmailSuccess = MutableStateFlow(false)
    val verifyEmailSuccess: StateFlow<Boolean> = _verifyEmailSuccess.asStateFlow()

    private val _resendOtpSuccess = MutableStateFlow<String?>(null)
    val resendOtpSuccess: StateFlow<String?> = _resendOtpSuccess.asStateFlow()

    private val _requireEmailVerification = MutableStateFlow<String?>(null)
    val requireEmailVerification: StateFlow<String?> = _requireEmailVerification.asStateFlow()

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = authRepository.login(request)
            result.onSuccess {
                _loginSuccess.value = true
            }.onFailure {
                val msg = it.message ?: "Bilinmeyen bir hata oluştu"
                if (msg.contains("doğrulayın", ignoreCase = true)) {
                    _requireEmailVerification.value = request.email
                } else {
                    _error.value = msg
                }
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

    fun forgotPassword(request: ForgotPasswordRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = authRepository.forgotPassword(request)
            result.onSuccess {
                _forgotPasswordSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "Kod gönderimi başarısız"
            }
            
            _isLoading.value = false
        }
    }

    fun resetPassword(request: ResetPasswordRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = authRepository.resetPassword(request)
            result.onSuccess {
                _resetPasswordSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "Şifre sıfırlama başarısız"
            }
            
            _isLoading.value = false
        }
    }

    fun verifyEmail(request: VerifyEmailRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = authRepository.verifyEmail(request)
            result.onSuccess {
                _verifyEmailSuccess.value = true
            }.onFailure {
                _error.value = it.message ?: "E-posta doğrulama başarısız"
            }
            
            _isLoading.value = false
        }
    }

    fun clearState() {
        _error.value = null
        _loginSuccess.value = false
        _registerSuccess.value = false
        _forgotPasswordSuccess.value = false
        _resetPasswordSuccess.value = false
        _verifyEmailSuccess.value = false
        _requireEmailVerification.value = null
    }

    fun clearError() {
        _error.value = null
    }

    fun resendOtp(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _resendOtpSuccess.value = null
            
            val result = authRepository.resendOtp(ResendOtpCodeRequest(email = email))
            result.onSuccess {
                _resendOtpSuccess.value = it.message ?: "Doğrulama kodu e-posta adresinize gönderildi."
            }.onFailure {
                _error.value = it.message ?: "Kod gönderilirken bir hata oluştu"
            }
            
            _isLoading.value = false
        }
    }
}
