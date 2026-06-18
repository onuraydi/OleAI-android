package com.onur.oleai_android.features.onboarding.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onur.oleai_android.core.network.dto.AllergyRestrictionListResponse
import com.onur.oleai_android.core.network.dto.CategorizedPopularIngredientsResponse
import com.onur.oleai_android.core.network.dto.DietaryPreferenceListResponse
import com.onur.oleai_android.core.network.dto.IngredientSearchResultResponse
import com.onur.oleai_android.core.network.dto.PopularDislikedIngredientResponse
import com.onur.oleai_android.features.onboarding.data.PersonalizationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import android.net.Uri

data class PersonalizationState(
    val username: String = "",
    val usernameError: String? = null,
    val birthDate: Date? = null,
    val birthDateText: String = "",
    val profileImageUri: Uri? = null,
    val profileImageBytes: ByteArray? = null,
    val selectedDietaryPreferenceIds: Set<String> = emptySet(),
    val selectedAllergyIds: Set<String> = emptySet(),
    val selectedDislikedIngredients: Set<IngredientSearchResultResponse> = emptySet(),
    val selectedDislikedIngredientIds: Set<String> = emptySet()
)

@HiltViewModel
class PersonalizationViewModel @Inject constructor(
    private val repository: PersonalizationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PersonalizationState())
    val state: StateFlow<PersonalizationState> = _state.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isCompleted = MutableStateFlow(false)
    val isCompleted: StateFlow<Boolean> = _isCompleted.asStateFlow()

    // Data from APIs
    private val _dietaryPreferences = MutableStateFlow<List<DietaryPreferenceListResponse>>(emptyList())
    val dietaryPreferences: StateFlow<List<DietaryPreferenceListResponse>> = _dietaryPreferences.asStateFlow()

    private val _allergies = MutableStateFlow<List<AllergyRestrictionListResponse>>(emptyList())
    val allergies: StateFlow<List<AllergyRestrictionListResponse>> = _allergies.asStateFlow()

    private val _popularDislikes = MutableStateFlow<List<PopularDislikedIngredientResponse>>(emptyList())
    val popularDislikes: StateFlow<List<PopularDislikedIngredientResponse>> = _popularDislikes.asStateFlow()

    private val _searchResults = MutableStateFlow<List<IngredientSearchResultResponse>>(emptyList())
    val searchResults: StateFlow<List<IngredientSearchResultResponse>> = _searchResults.asStateFlow()

    init {
        fetchDietaryPreferences()
        fetchAllergies()
        fetchPopularDislikes()
    }

    private fun fetchDietaryPreferences() {
        viewModelScope.launch {
            repository.getDietaryPreferences().onSuccess { data ->
                _dietaryPreferences.value = data
            }.onFailure { e ->
                _error.value = e.message
            }
        }
    }

    private fun fetchAllergies() {
        viewModelScope.launch {
            repository.getAllergyRestrictions().onSuccess { data ->
                _allergies.value = data
            }.onFailure { e ->
                _error.value = e.message
            }
        }
    }

    private fun fetchPopularDislikes() {
        viewModelScope.launch {
            repository.getPopularDislikedIngredients().onSuccess { data ->
                _popularDislikes.value = data
            }.onFailure { e ->
                _error.value = e.message
            }
        }
    }

    fun searchIngredients(keyword: String) {
        if (keyword.length < 2) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            repository.searchIngredients(keyword).onSuccess { data ->
                _searchResults.value = data
            }.onFailure { e ->
                _error.value = e.message
            }
        }
    }

    fun setUsername(username: String) {
        val error = when {
            username.length < 3 -> "Kullanıcı adı en az 3 karakter olmalıdır"
            !username.matches("^[a-zA-Z0-9._]+$".toRegex()) -> "Özel karakter kullanılamaz"
            else -> null
        }
        _state.update { it.copy(username = username, usernameError = error) }
    }

    fun setBirthDate(date: Date?) {
        val text = date?.let { SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(it) } ?: ""
        _state.update { it.copy(birthDate = date, birthDateText = text) }
    }

    fun setBirthDateText(text: String) {
        val digits = text.filter { it.isDigit() }.take(8)
        
        var parsedDate: Date? = null
        if (digits.length == 8) {
            try {
                val formatted = "${digits.substring(0, 2)}.${digits.substring(2, 4)}.${digits.substring(4, 8)}"
                val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                sdf.isLenient = false
                val date = sdf.parse(formatted)
                
                // check 13 years
                val cal = Calendar.getInstance()
                cal.add(Calendar.YEAR, -13)
                if (date != null && !date.after(cal.time)) {
                    parsedDate = date
                }
            } catch (e: Exception) { }
        }
        
        _state.update { 
            it.copy(
                birthDateText = digits,
                birthDate = parsedDate
            ) 
        }
    }

    fun setProfileImage(uri: Uri?, bytes: ByteArray?) {
        _state.update { it.copy(profileImageUri = uri, profileImageBytes = bytes) }
    }

    fun toggleDietaryPreference(id: String) {
        _state.update {
            val current = it.selectedDietaryPreferenceIds.toMutableSet()
            if (current.contains(id)) current.remove(id) else current.add(id)
            it.copy(selectedDietaryPreferenceIds = current)
        }
    }

    fun clearDietaryPreferences() {
        _state.update { it.copy(selectedDietaryPreferenceIds = emptySet()) }
    }

    fun toggleAllergy(id: String) {
        _state.update {
            val current = it.selectedAllergyIds.toMutableSet()
            if (current.contains(id)) current.remove(id) else current.add(id)
            it.copy(selectedAllergyIds = current)
        }
    }

    fun clearAllergies() {
        _state.update { it.copy(selectedAllergyIds = emptySet()) }
    }

    fun toggleDislikedIngredient(ingredient: IngredientSearchResultResponse) {
        _state.update {
            val currentIngredients = it.selectedDislikedIngredients.toMutableSet()
            val currentIds = it.selectedDislikedIngredientIds.toMutableSet()
            
            if (currentIds.contains(ingredient.masterIngredientId)) {
                currentIds.remove(ingredient.masterIngredientId)
                currentIngredients.removeAll { item -> item.masterIngredientId == ingredient.masterIngredientId }
            } else {
                currentIds.add(ingredient.masterIngredientId)
                currentIngredients.add(ingredient)
            }
            it.copy(
                selectedDislikedIngredients = currentIngredients,
                selectedDislikedIngredientIds = currentIds
            )
        }
    }

    fun completePersonalization() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val currentState = _state.value

            if (currentState.username.length < 3) {
                _error.value = "Lütfen geçerli bir kullanıcı adı girin"
                _isLoading.value = false
                return@launch
            }

            val birthDateStr = currentState.birthDate?.let {
                SimpleDateFormat("yyyy-MM-dd", Locale.US).format(it)
            }

            val result = repository.savePersonalization(
                username = currentState.username,
                birthDate = birthDateStr,
                profileImageBytes = currentState.profileImageBytes,
                dietaryPreferenceIds = currentState.selectedDietaryPreferenceIds.toList(),
                allergyRestrictionIds = currentState.selectedAllergyIds.toList(),
                dislikedIngredientIds = currentState.selectedDislikedIngredientIds.toList()
            )

            result.onSuccess {
                _isCompleted.value = true
            }.onFailure { e ->
                _error.value = e.message ?: "Kişiselleştirme kaydedilirken hata oluştu"
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
