package com.onur.oleai_android.features.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onur.oleai_android.core.network.dto.UserProfileResponse
import com.onur.oleai_android.features.profile.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    sealed class AuthState {
        object Loading : AuthState()
        object Unauthenticated : AuthState()
        object OnboardingRequired : AuthState()
        object Authenticated : AuthState()
    }

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfileResponse?>(null)
    val userProfile: StateFlow<UserProfileResponse?> = _userProfile.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    fun fetchUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = userRepository.getUserProfile()
            result.onSuccess {
                _userProfile.value = it
            }.onFailure {
                _error.value = it.message ?: "Profil bilgileri alınamadı."
            }
            
            _isLoading.value = false
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = userRepository.setOnboardedStatus(true)
            result.onSuccess {
                _onboardingCompleted.value = true
                // Profil nesnemizi güncelleyelim
                _userProfile.value = _userProfile.value?.copy(isOnboarded = true)
            }.onFailure {
                _error.value = it.message ?: "Onboarding durumu güncellenemedi."
            }
            
            _isLoading.value = false
        }
    }

    fun clearState() {
        _error.value = null
        _onboardingCompleted.value = false
    }

    fun checkInitialState() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            val result = userRepository.getUserProfile()
            result.onSuccess { profile ->
                _userProfile.value = profile
                if (profile.isOnboarded) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.OnboardingRequired
                }
            }.onFailure {
                // If the request fails (e.g. 401 Unauthorized), we treat them as Unauthenticated
                _authState.value = AuthState.Unauthenticated
            }
        }
    }
}
