package com.onur.oleai_android.features.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onur.oleai_android.core.network.ApiService
import com.onur.oleai_android.core.network.dto.RecipeCategoryResponse
import com.onur.oleai_android.core.network.dto.RecipeListItemResponse
import com.onur.oleai_android.features.home.data.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.onur.oleai_android.features.home.data.PersonalizedRecipesPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import java.util.concurrent.TimeUnit

data class CategoryStat(
    val name: String,
    val count: Int,
    val percentage: Int
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val firstName: String = "",

    // Dashboard Stats
    val totalPantryItems: Int = 0,
    val topCategories: List<CategoryStat> = emptyList(),
    val freshItems: Int = 0,
    val expiringItems: Int = 0,
    val expiredItems: Int = 0,
    val purchasedItems: Int = 0,
    val totalShoppingItems: Int = 0,

    // Recipes
    val trendingRecipes: List<RecipeListItemResponse> = emptyList(),
    // Categories
    val categories: List<RecipeCategoryResponse> = emptyList(),
    val selectedCategoryId: Int? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val apiService: ApiService // Need this for getUserProfile
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val categoryIdFlow = MutableStateFlow<Int?>(null)

    val personalizedRecipesPagingData: Flow<PagingData<RecipeListItemResponse>> = categoryIdFlow
        .flatMapLatest { categoryId ->
            Pager(
                config = PagingConfig(pageSize = 12, enablePlaceholders = false),
                pagingSourceFactory = { PersonalizedRecipesPagingSource(homeRepository, categoryId) }
            ).flow
        }
        .cachedIn(viewModelScope)

    init {
        loadHomeData()
    }

    fun refreshHomeData() {
        _uiState.update { it.copy(isRefreshing = true, error = null) }
        fetchData()
    }

    fun loadHomeData() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            try {
                // Fetch User Profile
                val userProfile = apiService.getUserProfile()
                
                // Fetch Data concurrently or sequentially
                val trending = homeRepository.getTrendingRecipes()
                val pantry = homeRepository.getMyPantry()
                val shopping = homeRepository.getMyShoppingList()
                val categoriesList = homeRepository.getRecipeCategories()

                // Calculate Pantry Stats
                val totalPantry = pantry.size
                val categoryCounts = mutableMapOf<String, Int>()
                var fresh = 0
                var expiring = 0
                var expired = 0

                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val today = Date()

                pantry.forEach { item ->
                    // Categories
                    val count = categoryCounts.getOrDefault(item.categoryName, 0)
                    categoryCounts[item.categoryName] = count + 1

                    // Freshness
                    if (item.expirationDate.isNullOrEmpty()) {
                        fresh++
                    } else {
                        try {
                            val expDate = dateFormat.parse(item.expirationDate)
                            if (expDate != null) {
                                val diffInMillis = expDate.time - today.time
                                val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)
                                if (days < 0) {
                                    expired++
                                } else if (days <= 5) {
                                    expiring++
                                } else {
                                    fresh++
                                }
                            } else {
                                fresh++
                            }
                        } catch (e: Exception) {
                            fresh++ // Fallback
                        }
                    }
                }

                val sortedCategories = categoryCounts.entries.sortedByDescending { it.value }
                val topCatsList = sortedCategories.take(3).map {
                    CategoryStat(
                        name = it.key,
                        count = it.value,
                        percentage = if (totalPantry == 0) 0 else (it.value * 100) / totalPantry
                    )
                }.toMutableList()

                if (sortedCategories.size > 3) {
                    val otherCount = sortedCategories.drop(3).sumOf { it.value }
                    topCatsList.add(
                        CategoryStat(
                            name = "Diğer",
                            count = otherCount,
                            percentage = if (totalPantry == 0) 0 else (otherCount * 100) / totalPantry
                        )
                    )
                }

                // Calculate Shopping Stats
                val totalShopping = shopping.size
                val purchased = shopping.count { it.isPurchased }

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        isRefreshing = false,
                        firstName = userProfile.firstName,
                        totalPantryItems = totalPantry,
                        topCategories = topCatsList,
                        freshItems = fresh,
                        expiringItems = expiring,
                        expiredItems = expired,
                        totalShoppingItems = totalShopping,
                        purchasedItems = purchased,
                        trendingRecipes = trending.items,
                        categories = categoriesList
                    )
                }

                // personalized recipes are handled by Paging3, no need to load here
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, isRefreshing = false, error = e.message ?: "Bilinmeyen bir hata oluştu.") }
            }
        }
    }

    fun onCategorySelected(categoryId: Int?) {
        if (_uiState.value.selectedCategoryId == categoryId) return
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
        categoryIdFlow.value = categoryId
    }
}
