package com.onur.oleai_android.features.home.data

import com.onur.oleai_android.core.network.ApiService
import com.onur.oleai_android.core.network.dto.PaginatedResponse
import com.onur.oleai_android.core.network.dto.RecipeCategoryResponse
import com.onur.oleai_android.core.network.dto.RecipeListItemResponse
import com.onur.oleai_android.core.network.dto.MyPantryItemResponse
import com.onur.oleai_android.core.network.dto.ShoppingListItemResponse
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getTrendingRecipes(): PaginatedResponse<RecipeListItemResponse> {
        return apiService.getPublishedRecipes(
            page = 0,
            size = 5,
            sortBy = "daily_trend"
        )
    }

    suspend fun getPersonalizedRecipes(
        categoryId: Int?,
        page: Int = 0,
        size: Int = 12
    ): PaginatedResponse<RecipeListItemResponse> {
        return apiService.getPublishedRecipes(
            page = page,
            size = size,
            categoryId = categoryId,
            sortBy = "newest",
            isForYou = true
        )
    }

    suspend fun getMyPantry(): List<MyPantryItemResponse> {
        return apiService.getMyPantry()
    }

    suspend fun getMyShoppingList(): List<com.onur.oleai_android.core.network.dto.MyShoppingListItemResponse> {
        return apiService.getMyShoppingList()
    }

    suspend fun getRecipeCategories(): List<RecipeCategoryResponse> {
        return apiService.getRecipeCategories()
    }
}
