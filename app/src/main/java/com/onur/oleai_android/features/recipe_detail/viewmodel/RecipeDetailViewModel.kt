package com.onur.oleai_android.features.recipe_detail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onur.oleai_android.core.network.ApiService
import com.onur.oleai_android.core.network.dto.AddMultipleShoppingListItemsRequest
import com.onur.oleai_android.core.network.dto.AddMultipleUserIngredientsCommand
import com.onur.oleai_android.core.network.dto.IngredientResponse
import com.onur.oleai_android.core.network.dto.RecipeCommentResponse
import com.onur.oleai_android.core.network.dto.RecipeDetailResponse
import com.onur.oleai_android.features.recipe_detail.data.RecipeDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class CookingFlowState {
    Idle,
    CheckingMissing,
    MissingIngredientsDialog,
    StartingCooking,
    CookingAssistant,
    FinishedDialog
}

data class MissingIngredientItem(
    val ingredientId: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val unitId: Int
)

data class RecipeDetailUiState(
    val isLoading: Boolean = true,
    val recipeDetail: RecipeDetailResponse? = null,
    val comments: List<RecipeCommentResponse> = emptyList(),
    val error: String? = null,
    
    val currentServings: Int = 1,
    val isMetric: Boolean = true,
    
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val isSaved: Boolean = false,
    val totalLikes: Int = 0,
    val totalDislikes: Int = 0,
    val totalSaves: Int = 0,

    val cookingFlowState: CookingFlowState = CookingFlowState.Idle,
    val missingIngredients: List<MissingIngredientItem> = emptyList(),
    val depletedIngredients: List<MissingIngredientItem> = emptyList(),

    val currentUserId: String = "",

    // Comment form state
    val isSubmittingComment: Boolean = false,
    val commentSubmitError: String? = null,
    val editingComment: RecipeCommentResponse? = null,
    val showDeleteConfirmFor: RecipeCommentResponse? = null,
    val replyingToComment: RecipeCommentResponse? = null
)

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val repository: RecipeDetailRepository,
    private val apiService: ApiService,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recipeId: String = checkNotNull(savedStateHandle["recipeId"])

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    val commentsPagingData: kotlinx.coroutines.flow.Flow<androidx.paging.PagingData<RecipeCommentResponse>> =
        repository.getRecipeCommentsPagingData(recipeId)
            .cachedIn(viewModelScope)

    init {
        loadRecipeDetail()
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val profile = apiService.getUserProfile()
                _uiState.update { it.copy(currentUserId = profile.id) }
            } catch (_: Exception) {}
        }
    }

    fun loadRecipeDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.getRecipeDetail(recipeId)
            result.onSuccess { response ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        recipeDetail = response,
                        currentServings = response.servings,
                        isLiked = response.userInteraction?.isLiked == true,
                        isDisliked = response.userInteraction?.isDisliked == true,
                        isSaved = response.userInteraction?.isSaved == true,
                        totalLikes = response.statistics.totalLikes,
                        totalDislikes = response.statistics.totalDislikes,
                        totalSaves = response.statistics.totalSaves
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    // --- Comment CRUD ---

    fun submitComment(content: String, rating: Int?, onSuccess: () -> Unit) {
        val editingComment = _uiState.value.editingComment
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingComment = true, commentSubmitError = null) }
            val result = if (editingComment != null) {
                repository.updateComment(
                    editingComment.id,
                    com.onur.oleai_android.core.network.dto.UpdateCommentRequest(newContent = content, rating = rating)
                )
            } else {
                repository.addComment(
                    recipeId,
                    com.onur.oleai_android.core.network.dto.AddCommentRequest(content = content, rating = rating)
                )
            }
            result.onSuccess {
                _uiState.update { it.copy(isSubmittingComment = false, editingComment = null) }
                onSuccess()
            }.onFailure { e ->
                _uiState.update { it.copy(isSubmittingComment = false, commentSubmitError = e.message) }
            }
        }
    }

    fun startEditingComment(comment: RecipeCommentResponse) {
        _uiState.update { it.copy(editingComment = comment, replyingToComment = null) }
    }

    fun cancelEditing() {
        _uiState.update { it.copy(editingComment = null) }
    }

    fun startReply(comment: RecipeCommentResponse) {
        _uiState.update { it.copy(replyingToComment = comment, editingComment = null) }
    }

    fun cancelReply() {
        _uiState.update { it.copy(replyingToComment = null) }
    }

    fun submitReply(content: String, onSuccess: () -> Unit) {
        val parentComment = _uiState.value.replyingToComment ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmittingComment = true, commentSubmitError = null) }
            val result = repository.addComment(
                recipeId,
                com.onur.oleai_android.core.network.dto.AddCommentRequest(
                    content = content,
                    parentCommentId = parentComment.id
                )
            )
            result.onSuccess {
                _uiState.update { it.copy(isSubmittingComment = false, replyingToComment = null) }
                onSuccess()
            }.onFailure { e ->
                _uiState.update { it.copy(isSubmittingComment = false, commentSubmitError = e.message) }
            }
        }
    }

    fun confirmDeleteComment(comment: RecipeCommentResponse) {
        _uiState.update { it.copy(showDeleteConfirmFor = comment) }
    }

    fun cancelDeleteComment() {
        _uiState.update { it.copy(showDeleteConfirmFor = null) }
    }

    fun deleteComment(comment: RecipeCommentResponse, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(showDeleteConfirmFor = null) }
            val result = repository.deleteComment(comment.id)
            result.onSuccess { onSuccess() }
                .onFailure { e -> _uiState.update { it.copy(commentSubmitError = e.message) } }
        }
    }

    fun toggleMetric() {
        _uiState.update { it.copy(isMetric = !it.isMetric) }
    }

    fun updateServings(newServings: Int) {
        if (newServings > 0) {
            _uiState.update { it.copy(currentServings = newServings) }
        }
    }

    fun toggleLike() {
        val currentState = _uiState.value
        val newIsLiked = !currentState.isLiked
        val newTotalLikes = if (newIsLiked) currentState.totalLikes + 1 else currentState.totalLikes - 1
        
        // Optimistic UI update
        _uiState.update { 
            it.copy(
                isLiked = newIsLiked, 
                totalLikes = newTotalLikes,
                // Automatically undislike if liking
                isDisliked = if (newIsLiked) false else it.isDisliked,
                totalDislikes = if (newIsLiked && it.isDisliked) it.totalDislikes - 1 else it.totalDislikes
            )
        }

        viewModelScope.launch {
            val result = repository.toggleLike(recipeId)
            result.onFailure {
                // Revert on failure
                _uiState.update { 
                    it.copy(
                        isLiked = currentState.isLiked, 
                        totalLikes = currentState.totalLikes,
                        isDisliked = currentState.isDisliked,
                        totalDislikes = currentState.totalDislikes
                    )
                }
            }
        }
    }

    fun toggleDislike() {
        val currentState = _uiState.value
        val newIsDisliked = !currentState.isDisliked
        val newTotalDislikes = if (newIsDisliked) currentState.totalDislikes + 1 else currentState.totalDislikes - 1

        _uiState.update { 
            it.copy(
                isDisliked = newIsDisliked, 
                totalDislikes = newTotalDislikes,
                // Automatically unlike if disliking
                isLiked = if (newIsDisliked) false else it.isLiked,
                totalLikes = if (newIsDisliked && it.isLiked) it.totalLikes - 1 else it.totalLikes
            )
        }

        viewModelScope.launch {
            val result = repository.toggleDislike(recipeId)
            result.onFailure {
                _uiState.update { 
                    it.copy(
                        isDisliked = currentState.isDisliked, 
                        totalDislikes = currentState.totalDislikes,
                        isLiked = currentState.isLiked,
                        totalLikes = currentState.totalLikes
                    )
                }
            }
        }
    }

    fun toggleSave() {
        val currentState = _uiState.value
        val newIsSaved = !currentState.isSaved
        val newTotalSaves = if (newIsSaved) currentState.totalSaves + 1 else currentState.totalSaves - 1

        _uiState.update { it.copy(isSaved = newIsSaved, totalSaves = newTotalSaves) }

        viewModelScope.launch {
            val result = repository.toggleSave(recipeId)
            result.onFailure {
                _uiState.update { it.copy(isSaved = currentState.isSaved, totalSaves = currentState.totalSaves) }
            }
        }
    }

    // Cooking Flow
    fun startCookingFlow() {
        viewModelScope.launch {
            _uiState.update { it.copy(cookingFlowState = CookingFlowState.CheckingMissing) }
            val result = repository.getMissingIngredients(recipeId)
            result.onSuccess { missing ->
                if (missing.isEmpty()) {
                    // No missing ingredients, proceed to cooking directly
                    startCooking(emptyList())
                } else {
                    val recipeIngredients = _uiState.value.recipeDetail?.ingredients ?: emptyList()
                    val mappedMissing = missing.map { ingredient ->
                        val recipeIng = recipeIngredients.find { it.ingredientId == ingredient.id }
                        MissingIngredientItem(
                            ingredientId = ingredient.id,
                            name = ingredient.name,
                            quantity = recipeIng?.amount ?: recipeIng?.quantity ?: 1.0,
                            unit = recipeIng?.unit ?: "",
                            unitId = recipeIng?.unitId ?: 1
                        )
                    }
                    _uiState.update { 
                        it.copy(
                            cookingFlowState = CookingFlowState.MissingIngredientsDialog,
                            missingIngredients = mappedMissing
                        )
                    }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(cookingFlowState = CookingFlowState.Idle, error = e.message) }
            }
        }
    }

    fun handleMissingIngredientsAction(ignoredIds: List<String>) {
        startCooking(ignoredIds)
    }
    
    fun cancelCookingFlow() {
        _uiState.update { it.copy(cookingFlowState = CookingFlowState.Idle) }
    }

    private fun startCooking(ignoredIds: List<String>) {
        viewModelScope.launch {
            _uiState.update { it.copy(cookingFlowState = CookingFlowState.StartingCooking) }
            val result = repository.startCooking(recipeId, ignoredIds)
            result.onSuccess { depleted ->
                val recipeIngredients = _uiState.value.recipeDetail?.ingredients ?: emptyList()
                val mappedDepleted = depleted.map { ingredient ->
                    val recipeIng = recipeIngredients.find { it.ingredientId == ingredient.id }
                    val originalUnitId = recipeIng?.unitId ?: 1
                    
                    var defaultQuantity = 1.0
                    var defaultUnitId = 1
                    var defaultUnitName = "Adet"
                    
                    if (originalUnitId == 2 || originalUnitId == 3) {
                        defaultQuantity = 250.0
                        defaultUnitId = 3
                        defaultUnitName = "Gram"
                    } else if (originalUnitId == 4 || originalUnitId == 5) {
                        defaultQuantity = 500.0
                        defaultUnitId = 5
                        defaultUnitName = "Mililitre"
                    }

                    MissingIngredientItem(
                        ingredientId = ingredient.id,
                        name = ingredient.name,
                        quantity = defaultQuantity,
                        unit = defaultUnitName,
                        unitId = defaultUnitId
                    )
                }
                _uiState.update { 
                    it.copy(
                        cookingFlowState = CookingFlowState.CookingAssistant,
                        depletedIngredients = mappedDepleted
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(cookingFlowState = CookingFlowState.Idle, error = e.message) }
            }
        }
    }

    fun finishCookingAssistant() {
        val depleted = _uiState.value.depletedIngredients
        if (depleted.isEmpty()) {
            _uiState.update { it.copy(cookingFlowState = CookingFlowState.Idle) }
        } else {
            _uiState.update { it.copy(cookingFlowState = CookingFlowState.FinishedDialog) }
        }
    }

    fun addIngredientsToPantry(userId: String, ingredients: List<com.onur.oleai_android.core.network.dto.AddUserIngredientRequest>) {
        viewModelScope.launch {
            try {
                apiService.addMultipleIngredients(
                    com.onur.oleai_android.core.network.dto.AddMultipleUserIngredientsCommand(
                        com.onur.oleai_android.core.network.dto.AddMultipleUserIngredientsRequest(ingredients)
                    )
                )
            } catch (e: Exception) {
                // Handled silently or update error state
            }
        }
    }

    fun addIngredientsToShoppingList(ingredients: List<com.onur.oleai_android.core.network.dto.AddShoppingListItemRequest>) {
        viewModelScope.launch {
            try {
                apiService.addMultipleShoppingListItems(
                    com.onur.oleai_android.core.network.dto.AddMultipleShoppingListItemsRequest(ingredients)
                )
            } catch (e: Exception) {
                // Handled silently or update error state
            }
        }
    }
}
