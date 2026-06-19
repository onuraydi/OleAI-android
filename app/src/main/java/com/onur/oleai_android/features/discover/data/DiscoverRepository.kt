package com.onur.oleai_android.features.discover.data

import com.onur.oleai_android.core.network.ApiService
import com.onur.oleai_android.core.network.dto.PaginatedResponse
import com.onur.oleai_android.core.network.dto.RecipeCategoryResponse
import com.onur.oleai_android.core.network.dto.RecipeListItemResponse
import com.onur.oleai_android.core.network.dto.TagDto
import javax.inject.Inject

class DiscoverRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getRecipeCategories(): List<RecipeCategoryResponse> {
        return apiService.getRecipeCategories()
    }

    suspend fun getRecipeTags(): List<TagDto> {
        return apiService.getRecipeTags()
    }

    suspend fun getDiscoverRecipes(
        page: Int = 0,
        size: Int = 12,
        searchTerm: String? = null,
        categoryId: Int? = null,
        maxTotalTime: Int? = null,
        minRating: Double? = null,
        difficulty: Int? = null,
        tagId: Int? = null,
        sortBy: String? = "popular"
    ): PaginatedResponse<RecipeListItemResponse> {
        return apiService.getPublishedRecipes(
            page = page,
            size = size,
            searchTerm = searchTerm,
            categoryId = categoryId,
            maxTotalTime = maxTotalTime,
            minRating = minRating,
            difficulty = difficulty,
            tagId = tagId,
            sortBy = sortBy,
            isForYou = false
        )
    }
}
