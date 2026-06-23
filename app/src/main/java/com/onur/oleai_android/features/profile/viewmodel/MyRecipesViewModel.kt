package com.onur.oleai_android.features.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onur.oleai_android.core.network.dto.RecipeListItemResponse
import com.onur.oleai_android.features.profile.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.onur.oleai_android.core.network.dto.TagDto

enum class MyRecipesTab {
    CREATED, SAVED, LIKED, DISLIKED
}

data class MyRecipesState(
    val selectedTab: MyRecipesTab = MyRecipesTab.CREATED,
    val recipes: List<RecipeListItemResponse> = emptyList(),
    val tags: List<TagDto> = emptyList(),
    val totalRecipes: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val sortBy: String = "En Yeni",
    val minRating: Double? = null,
    val difficulty: Int? = null,
    val maxTotalTime: Int? = null,
    val visibility: String? = null,
    val tagId: Int? = null,
    val page: Int = 0,
    val pageSize: Int = 10,
    val hasMore: Boolean = true
)

@HiltViewModel
class MyRecipesViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MyRecipesState())
    val state: StateFlow<MyRecipesState> = _state.asStateFlow()

    init {
        fetchTags()
        fetchRecipes(isLoadMore = false)
    }

    private fun fetchTags() {
        viewModelScope.launch {
            userRepository.getRecipeTags().onSuccess { tags ->
                _state.value = _state.value.copy(tags = tags)
            }.onFailure { e ->
                // Handle tag fetching error if needed
            }
        }
    }

    fun onTabSelected(tab: MyRecipesTab) {
        _state.value = _state.value.copy(selectedTab = tab)
        fetchRecipes(isLoadMore = false)
    }

    fun onTagSelected(tagId: Int?) {
        if (_state.value.tagId == tagId) return
        _state.value = _state.value.copy(tagId = tagId)
        fetchRecipes(isLoadMore = false)
    }

    fun onSearchQueryChanged(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun onSearch() {
        fetchRecipes(isLoadMore = false)
    }
    
    fun applyFilters(
        minRating: Double?,
        difficulty: Int?,
        maxTotalTime: Int?,
        visibility: String?,
        tagId: Int?
    ) {
        _state.value = _state.value.copy(
            minRating = minRating,
            difficulty = difficulty,
            maxTotalTime = maxTotalTime,
            visibility = visibility,
            tagId = tagId
        )
        fetchRecipes(isLoadMore = false)
    }

    fun applySort(sortBy: String) {
        _state.value = _state.value.copy(sortBy = sortBy)
        fetchRecipes(isLoadMore = false)
    }

    fun loadMoreRecipes() {
        if (_state.value.isLoadingMore || !_state.value.hasMore || _state.value.isLoading) return
        _state.value = _state.value.copy(page = _state.value.page + 1)
        fetchRecipes(isLoadMore = true)
    }

    fun fetchRecipes(isLoadMore: Boolean = false) {
        if (!isLoadMore) {
            _state.value = _state.value.copy(isLoading = true, error = null, page = 0, hasMore = true)
        } else {
            _state.value = _state.value.copy(isLoadingMore = true, error = null)
        }
        
        viewModelScope.launch {
            val currentState = _state.value
            
            // Map SortBy to backend expected format
            val apiSortBy = when (currentState.sortBy) {
                "En Yeni" -> "newest"
                "Popüler" -> "popular"
                "En Çok Oylanan" -> "top_rated"
                "En Hızlı" -> "fastest"
                else -> "newest"
            }
            
            val result = when (currentState.selectedTab) {
                MyRecipesTab.CREATED -> userRepository.getMyRecipes(
                    page = currentState.page,
                    size = currentState.pageSize,
                    searchTerm = currentState.searchQuery.takeIf { it.isNotBlank() },
                    minRating = currentState.minRating,
                    difficulty = currentState.difficulty,
                    maxTotalTime = currentState.maxTotalTime,
                    visibility = currentState.visibility,
                    tagId = currentState.tagId,
                    sortBy = apiSortBy
                )
                MyRecipesTab.SAVED -> userRepository.getSavedRecipes(
                    page = currentState.page,
                    size = currentState.pageSize,
                    searchTerm = currentState.searchQuery.takeIf { it.isNotBlank() },
                    minRating = currentState.minRating,
                    difficulty = currentState.difficulty,
                    maxTotalTime = currentState.maxTotalTime,
                    visibility = currentState.visibility,
                    tagId = currentState.tagId,
                    sortBy = apiSortBy
                )
                MyRecipesTab.LIKED -> userRepository.getLikedRecipes(
                    page = currentState.page,
                    size = currentState.pageSize,
                    searchTerm = currentState.searchQuery.takeIf { it.isNotBlank() },
                    minRating = currentState.minRating,
                    difficulty = currentState.difficulty,
                    maxTotalTime = currentState.maxTotalTime,
                    visibility = currentState.visibility,
                    tagId = currentState.tagId,
                    sortBy = apiSortBy
                )
                MyRecipesTab.DISLIKED -> userRepository.getDislikedRecipes(
                    page = currentState.page,
                    size = currentState.pageSize,
                    searchTerm = currentState.searchQuery.takeIf { it.isNotBlank() },
                    minRating = currentState.minRating,
                    difficulty = currentState.difficulty,
                    maxTotalTime = currentState.maxTotalTime,
                    visibility = currentState.visibility,
                    tagId = currentState.tagId,
                    sortBy = apiSortBy
                )
            }

            result.onSuccess { response ->
                val newRecipes = if (isLoadMore) {
                    currentState.recipes + response.items
                } else {
                    response.items
                }
                _state.value = _state.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    recipes = newRecipes,
                    totalRecipes = response.totalItems,
                    hasMore = response.hasNextPage
                )
            }.onFailure { exception ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = exception.message ?: "Tarifler yüklenemedi",
                    recipes = if (!isLoadMore) emptyList() else currentState.recipes,
                    totalRecipes = if (!isLoadMore) 0 else currentState.totalRecipes,
                    hasMore = false
                )
            }
        }
    }
}
