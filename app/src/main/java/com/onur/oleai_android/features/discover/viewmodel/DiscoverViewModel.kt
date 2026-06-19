package com.onur.oleai_android.features.discover.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.onur.oleai_android.core.network.dto.RecipeCategoryResponse
import com.onur.oleai_android.core.network.dto.RecipeListItemResponse
import com.onur.oleai_android.core.network.dto.TagDto
import com.onur.oleai_android.features.discover.data.DiscoverRecipesPagingSource
import com.onur.oleai_android.features.discover.data.DiscoverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DiscoverUiState(
    val categories: List<RecipeCategoryResponse> = emptyList(),
    val tags: List<TagDto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class DiscoverFilterState(
    val searchTerm: String = "",
    val categoryId: Int? = null,
    val maxTotalTime: Int? = null, // in minutes (e.g. 15, 30, 60)
    val minRating: Double? = null,
    val difficulty: Int? = null, // 1=Easy, 2=Medium, 3=Hard
    val tagId: Int? = null,
    val sortBy: String = "popular"
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val repository: DiscoverRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    private val _filterState = MutableStateFlow(DiscoverFilterState())
    val filterState: StateFlow<DiscoverFilterState> = _filterState.asStateFlow()

    // Internal flow for debounced search term
    private val _searchTermFlow = MutableStateFlow("")

    val pagedRecipes: Flow<PagingData<RecipeListItemResponse>> = _filterState
        .flatMapLatest { filter ->
            Pager(
                config = PagingConfig(
                    pageSize = 12,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    DiscoverRecipesPagingSource(
                        repository = repository,
                        searchTerm = filter.searchTerm.ifBlank { null },
                        categoryId = filter.categoryId,
                        maxTotalTime = filter.maxTotalTime,
                        minRating = filter.minRating,
                        difficulty = filter.difficulty,
                        tagId = filter.tagId,
                        sortBy = filter.sortBy
                    )
                }
            ).flow
        }
        .cachedIn(viewModelScope)

    init {
        loadInitialData()
        
        // Handle debounce for search
        viewModelScope.launch {
            _searchTermFlow
                .debounce(500)
                .distinctUntilChanged()
                .collect { debouncedQuery ->
                    _filterState.update { it.copy(searchTerm = debouncedQuery) }
                }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val categories = repository.getRecipeCategories()
                val tags = repository.getRecipeTags()
                _uiState.update {
                    it.copy(
                        categories = categories,
                        tags = tags,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Bilinmeyen bir hata oluştu"
                    )
                }
            }
        }
    }

    fun updateSearchTerm(term: String) {
        _searchTermFlow.value = term
    }

    fun updateCategory(categoryId: Int?) {
        _filterState.update { it.copy(categoryId = categoryId) }
    }

    fun applyFilters(
        maxTotalTime: Int?,
        minRating: Double?,
        difficulty: Int?,
        tagId: Int?,
        sortBy: String
    ) {
        _filterState.update {
            it.copy(
                maxTotalTime = maxTotalTime,
                minRating = minRating,
                difficulty = difficulty,
                tagId = tagId,
                sortBy = sortBy
            )
        }
    }

    fun clearFilters() {
        val currentCategory = _filterState.value.categoryId
        _filterState.value = DiscoverFilterState(
            searchTerm = _searchTermFlow.value,
            categoryId = currentCategory
        )
    }
}
