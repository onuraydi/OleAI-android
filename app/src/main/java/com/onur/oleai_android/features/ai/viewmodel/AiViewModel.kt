package com.onur.oleai_android.features.ai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onur.oleai_android.core.network.ApiService
import com.onur.oleai_android.core.network.dto.AiRecommendationRequest
import com.onur.oleai_android.core.network.dto.AiRecommendationResponse
import com.onur.oleai_android.core.network.dto.RecipeListItemResponse
import com.onur.oleai_android.features.profile.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnalysisModeDef(
    val id: String,
    val label: String,
    val description: String
)

val availableAnalysisModes = listOf(
    AnalysisModeDef("Strict", "SADECE DOLAP", "Alışverişe çıkmadan, kesinlikle sadece dolabındaki malzemelerle yapabileceğin tarifler."),
    AnalysisModeDef("Default", "DENGELİ", "Eldeki malzemelerinle damak zevkini harmanlayan en optimal günlük denge."),
    AnalysisModeDef("ForYou", "SANA ÖZEL", "Malzemeleri umursamadan, tamamen geçmiş beğenilerine göre senin tarzına en uygun lezzetler."),
    AnalysisModeDef("Ultimate", "GURME", "Şefin seçimi! Hem zevkine uyan hem de 1'e 1 eşleşen kusursuz, garantili tarifler."),
    AnalysisModeDef("Quick", "HIZLI PİŞİR", "Zamanın az olduğunda, minimum çaba ve eldeki malzemeyle şipşak kurtarıcı öğünler."),
    AnalysisModeDef("Explore", "KEŞFET", "Klasikleri unut! Daha az bilinen, daha önce hiç denemediğin yeni gizli lezzetler."),
    AnalysisModeDef("MinimalShopping", "AZ ALIŞVERİŞ", "Eksikleri minimumda tutarak, marketten sadece 1-2 şey alıp hemen hazırlayabileceğin tarifler.")
)

data class AiUiState(
    val isLoadingProfile: Boolean = true,
    val isLoadingRecommendations: Boolean = false,
    val pantryItemCount: Int = 0,
    val dietsText: String = "Özel diyet seçilmedi",
    
    // Motor Ayarları (Settings)
    val analysisMode: AnalysisModeDef = availableAnalysisModes[0], // SADECE DOLAP by default
    val preventWaste: Boolean = false,
    val allergyProtection: Boolean = true,
    val strictDietRules: Boolean = true,
    val excludeDisliked: Boolean = true,

    // Sonuçlar (Results)
    val recommendations: List<AiRecommendationResponse> = emptyList(),
    val hasPerfectMatch: Boolean = true,
    val error: String? = null,
    val recommendationsFetched: Boolean = false
)

@HiltViewModel
class AiViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val apiService: ApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiUiState())
    val uiState: StateFlow<AiUiState> = _uiState.asStateFlow()

    init {
        loadProfileSummary()
    }

    fun loadProfileSummary() {
        viewModelScope.launch {
            var dietsStr = "Özel diyet seçilmedi"
            var pantryCount = 0
            
            var hasAllergies = true
            var hasDiets = true
            var hasDisliked = true
            
            try {
                val profileResult = userRepository.getUserProfile()
                if (profileResult.isSuccess) {
                    val profile = profileResult.getOrNull()
                    if (profile != null) {
                        hasAllergies = profile.allergies.isNotEmpty()
                        hasDiets = profile.dietaryPreferences.isNotEmpty()
                        hasDisliked = profile.dislikedIngredients.isNotEmpty()
                        
                        if (hasDiets) {
                            dietsStr = profile.dietaryPreferences.joinToString(", ") { it.name }
                        }
                    }
                }
            } catch (e: Exception) {
                // ignore
            }
            
            try {
                val pantry = apiService.getMyPantry()
                pantryCount = pantry.size
            } catch (e: Exception) {
                // ignore
            }
            
            _uiState.update { 
                it.copy(
                    isLoadingProfile = false,
                    pantryItemCount = pantryCount,
                    dietsText = dietsStr,
                    allergyProtection = hasAllergies,
                    strictDietRules = hasDiets,
                    excludeDisliked = hasDisliked
                ) 
            }
        }
    }

    fun updateAnalysisMode(modeId: String) {
        val newMode = availableAnalysisModes.find { it.id == modeId } ?: availableAnalysisModes[0]
        _uiState.update { it.copy(analysisMode = newMode) }
    }
    fun togglePreventWaste(enabled: Boolean) = _uiState.update { it.copy(preventWaste = enabled) }
    fun toggleAllergyProtection(enabled: Boolean) = _uiState.update { it.copy(allergyProtection = enabled) }
    fun toggleStrictDietRules(enabled: Boolean) = _uiState.update { it.copy(strictDietRules = enabled) }
    fun toggleExcludeDisliked(enabled: Boolean) = _uiState.update { it.copy(excludeDisliked = enabled) }

    fun getRecommendations() {
        _uiState.update { it.copy(isLoadingRecommendations = true, error = null, recommendationsFetched = false) }
        viewModelScope.launch {
            try {
                val response = apiService.getAiRecommendations(
                    mode = _uiState.value.analysisMode.id,
                    useZeroWaste = _uiState.value.preventWaste,
                    useAllergyFilters = _uiState.value.allergyProtection,
                    useDietFilters = _uiState.value.strictDietRules,
                    useDislikedFilters = _uiState.value.excludeDisliked
                )
                
                val hasFallback = response.any { it.isFallback }
                
                _uiState.update { 
                    it.copy(
                        isLoadingRecommendations = false,
                        recommendations = response,
                        hasPerfectMatch = !hasFallback, // If there's a fallback, perfect match is false
                        recommendationsFetched = true
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoadingRecommendations = false,
                        error = "Tarifler getirilirken bir hata oluştu: ${e.message}",
                        recommendationsFetched = false
                    )
                }
            }
        }
    }
}
