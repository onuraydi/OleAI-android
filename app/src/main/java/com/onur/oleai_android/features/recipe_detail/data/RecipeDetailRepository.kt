package com.onur.oleai_android.features.recipe_detail.data

import com.onur.oleai_android.core.network.ApiService
import com.onur.oleai_android.core.network.dto.AddCommentRequest
import com.onur.oleai_android.core.network.dto.UpdateCommentRequest
import com.onur.oleai_android.core.network.dto.IngredientResponse
import com.onur.oleai_android.core.network.dto.PaginatedResponse
import com.onur.oleai_android.core.network.dto.RecipeCommentResponse
import com.onur.oleai_android.core.network.dto.RecipeDetailResponse
import com.onur.oleai_android.core.network.dto.StartCookingRequest
import com.onur.oleai_android.core.network.dto.ToggleInteractionResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeDetailRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getRecipeDetail(id: String): Result<RecipeDetailResponse> {
        return try {
            val response = apiService.getRecipeDetail(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleLike(id: String): Result<ToggleInteractionResponse> {
        return try {
            val response = apiService.toggleLike(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleDislike(id: String): Result<ToggleInteractionResponse> {
        return try {
            val response = apiService.toggleDislike(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleSave(id: String): Result<ToggleInteractionResponse> {
        return try {
            val response = apiService.toggleSave(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRecipeComments(id: String, page: Int = 0, size: Int = 10): Result<PaginatedResponse<RecipeCommentResponse>> {
        return try {
            val response = apiService.getRecipeComments(id, page, size)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getRecipeCommentsPagingData(id: String): kotlinx.coroutines.flow.Flow<androidx.paging.PagingData<RecipeCommentResponse>> {
        return androidx.paging.Pager(
            config = androidx.paging.PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { RecipeCommentsPagingSource(apiService, id) }
        ).flow
    }

    suspend fun addComment(id: String, request: AddCommentRequest): Result<ToggleInteractionResponse> {
        return try {
            val response = apiService.addComment(id, request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateComment(commentId: String, request: UpdateCommentRequest): Result<ToggleInteractionResponse> {
        return try {
            val response = apiService.updateComment(commentId, request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteComment(commentId: String): Result<Unit> {
        return try {
            // Retrofit expects Response<Unit>, so we extract body (which is Unit) or return success
            val response = apiService.deleteComment(commentId)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete comment"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMissingIngredients(id: String): Result<List<IngredientResponse>> {
        return try {
            val response = apiService.getMissingIngredients(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun startCooking(id: String, ignoredIds: List<String>): Result<List<IngredientResponse>> {
        return try {
            val response = apiService.startCooking(id, StartCookingRequest(ignoredIds))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
