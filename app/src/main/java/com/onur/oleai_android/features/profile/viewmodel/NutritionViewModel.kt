package com.onur.oleai_android.features.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onur.oleai_android.core.network.dto.*
import com.onur.oleai_android.features.onboarding.data.PersonalizationRepository
import com.onur.oleai_android.features.profile.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NutritionState(
    val dietaryPreferences: List<DietaryPreferenceListResponse> = emptyList(),
    val selectedDietaryPreferenceIds: Set<String> = emptySet(),
    val isDietaryPreferencesSaving: Boolean = false,

    val allergies: List<AllergyRestrictionListResponse> = emptyList(),
    val selectedAllergyIds: Set<String> = emptySet(),
    val isAllergiesSaving: Boolean = false,

    val selectedDislikedIngredients: List<DislikedIngredientItemResponse> = emptyList(),
    val searchResults: List<IngredientSearchResultResponse> = emptyList(),
    val isSearching: Boolean = false,
    val searchQuery: String = "",
    val isDislikedIngredientsSaving: Boolean = false,

    val isLoading: Boolean = true,
    val error: String? = null,
    val successMessage: String? = null
)

sealed class NutritionEvent {
    data class ToggleDietaryPreference(val id: String) : NutritionEvent()
    object SaveDietaryPreferences : NutritionEvent()

    data class ToggleAllergy(val id: String) : NutritionEvent()
    object SaveAllergies : NutritionEvent()

    data class SearchQueryChanged(val query: String) : NutritionEvent()
    data class AddDislikedIngredient(val ingredient: IngredientSearchResultResponse) : NutritionEvent()
    data class RemoveDislikedIngredient(val id: String) : NutritionEvent()
    object SaveDislikedIngredients : NutritionEvent()

    object DismissMessage : NutritionEvent()
}

@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val personalizationRepository: PersonalizationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NutritionState())
    val state: StateFlow<NutritionState> = _state.asStateFlow()

    private var searchJob: Job? = null
    private var isInitialized = false

    fun initData(userProfile: UserProfileResponse) {
        if (isInitialized) return
        isInitialized = true

        _state.update {
            it.copy(
                selectedDietaryPreferenceIds = userProfile.dietaryPreferences.map { pref -> pref.id }.toSet(),
                selectedAllergyIds = userProfile.allergies.map { alg -> alg.id }.toSet(),
                selectedDislikedIngredients = userProfile.dislikedIngredients
            )
        }

        fetchOptions()
    }

    private fun fetchOptions() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val dietaryRes = personalizationRepository.getDietaryPreferences()
            val allergyRes = personalizationRepository.getAllergyRestrictions()

            if (dietaryRes.isSuccess && allergyRes.isSuccess) {
                _state.update {
                    it.copy(
                        dietaryPreferences = dietaryRes.getOrNull() ?: emptyList(),
                        allergies = allergyRes.getOrNull() ?: emptyList(),
                        isLoading = false
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Seçenekler yüklenirken bir hata oluştu"
                    )
                }
            }
        }
    }

    fun onEvent(event: NutritionEvent) {
        when (event) {
            is NutritionEvent.ToggleDietaryPreference -> {
                val current = _state.value.selectedDietaryPreferenceIds.toMutableSet()
                if (current.contains(event.id)) {
                    current.remove(event.id)
                } else {
                    current.add(event.id)
                }
                _state.update { it.copy(selectedDietaryPreferenceIds = current) }
            }
            is NutritionEvent.SaveDietaryPreferences -> {
                viewModelScope.launch {
                    _state.update { it.copy(isDietaryPreferencesSaving = true) }
                    val req = UpdateUserPreferencesRequest(_state.value.selectedDietaryPreferenceIds.toList())
                    val res = userRepository.updateDietaryPreferences(req)
                    if (res.isSuccess) {
                        _state.update { it.copy(isDietaryPreferencesSaving = false, successMessage = "Beslenme tercihleri başarıyla kaydedildi") }
                    } else {
                        _state.update { it.copy(isDietaryPreferencesSaving = false, error = "Beslenme tercihleri kaydedilemedi") }
                    }
                }
            }

            is NutritionEvent.ToggleAllergy -> {
                val current = _state.value.selectedAllergyIds.toMutableSet()
                if (current.contains(event.id)) {
                    current.remove(event.id)
                } else {
                    current.add(event.id)
                }
                _state.update { it.copy(selectedAllergyIds = current) }
            }
            is NutritionEvent.SaveAllergies -> {
                viewModelScope.launch {
                    _state.update { it.copy(isAllergiesSaving = true) }
                    val req = UpdateUserAllergiesRequest(_state.value.selectedAllergyIds.toList())
                    val res = userRepository.updateAllergies(req)
                    if (res.isSuccess) {
                        _state.update { it.copy(isAllergiesSaving = false, successMessage = "Alerjiler başarıyla kaydedildi") }
                    } else {
                        _state.update { it.copy(isAllergiesSaving = false, error = "Alerjiler kaydedilemedi") }
                    }
                }
            }

            is NutritionEvent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
                searchJob?.cancel()
                if (event.query.length >= 2) {
                    searchJob = viewModelScope.launch {
                        _state.update { it.copy(isSearching = true) }
                        delay(500)
                        val res = personalizationRepository.searchIngredients(event.query)
                        if (res.isSuccess) {
                            _state.update { it.copy(searchResults = res.getOrNull() ?: emptyList(), isSearching = false) }
                        } else {
                            _state.update { it.copy(searchResults = emptyList(), isSearching = false) }
                        }
                    }
                } else {
                    _state.update { it.copy(searchResults = emptyList(), isSearching = false) }
                }
            }
            is NutritionEvent.AddDislikedIngredient -> {
                val current = _state.value.selectedDislikedIngredients.toMutableList()
                if (current.none { it.id == event.ingredient.masterIngredientId }) {
                    current.add(DislikedIngredientItemResponse(id = event.ingredient.masterIngredientId, name = event.ingredient.displayName))
                }
                _state.update { it.copy(selectedDislikedIngredients = current, searchQuery = "", searchResults = emptyList()) }
            }
            is NutritionEvent.RemoveDislikedIngredient -> {
                val current = _state.value.selectedDislikedIngredients.filter { it.id != event.id }
                _state.update { it.copy(selectedDislikedIngredients = current) }
            }
            is NutritionEvent.SaveDislikedIngredients -> {
                viewModelScope.launch {
                    _state.update { it.copy(isDislikedIngredientsSaving = true) }
                    val req = UpdateUserDislikedIngredientsRequest(_state.value.selectedDislikedIngredients.map { it.id })
                    val res = userRepository.updateDislikedIngredients(req)
                    if (res.isSuccess) {
                        _state.update { it.copy(isDislikedIngredientsSaving = false, successMessage = "Sevilmeyen malzemeler başarıyla kaydedildi") }
                    } else {
                        _state.update { it.copy(isDislikedIngredientsSaving = false, error = "Malzemeler kaydedilemedi") }
                    }
                }
            }

            is NutritionEvent.DismissMessage -> {
                _state.update { it.copy(error = null, successMessage = null) }
            }
        }
    }
}
