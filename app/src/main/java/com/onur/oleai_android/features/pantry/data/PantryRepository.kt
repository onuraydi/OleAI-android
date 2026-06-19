package com.onur.oleai_android.features.pantry.data

import com.onur.oleai_android.core.network.ApiService
import com.onur.oleai_android.core.network.dto.AddMultipleUserIngredientsCommand
import com.onur.oleai_android.core.network.dto.AddedUserIngredientResponse
import com.onur.oleai_android.core.network.dto.CategorizedPopularIngredientsResponse
import com.onur.oleai_android.core.network.dto.IngredientSearchResultResponse
import com.onur.oleai_android.core.network.dto.MyPantryItemResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PantryRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getMyPantry(): Result<List<MyPantryItemResponse>> {
        return try {
            val response = apiService.getMyPantry()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getIngredientCategories(): Result<List<com.onur.oleai_android.core.network.dto.IngredientCategoryResponse>> {
        return try {
            val response = apiService.getIngredientCategories()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchIngredients(keyword: String): Result<List<IngredientSearchResultResponse>> {
        return try {
            val response = apiService.searchIngredients(keyword)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPopularIngredientsByCategory(): Result<List<CategorizedPopularIngredientsResponse>> {
        return try {
            val response = apiService.getPopularIngredientsByCategory()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addMultipleIngredients(request: AddMultipleUserIngredientsCommand): Result<List<AddedUserIngredientResponse>> {
        return try {
            val response = apiService.addMultipleIngredients(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateIngredientQuantity(
        id: String,
        request: com.onur.oleai_android.core.network.dto.UpdateUserIngredientRequest
    ): Result<com.onur.oleai_android.core.network.dto.GenericMessageResponse> {
        return try {
            val response = apiService.updateIngredientQuantity(id, request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteIngredient(id: String): Result<com.onur.oleai_android.core.network.dto.GenericMessageResponse> {
        return try {
            val response = apiService.deleteIngredient(id)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addShoppingListItem(
        request: com.onur.oleai_android.core.network.dto.AddShoppingListItemRequest
    ): Result<com.onur.oleai_android.core.network.dto.ShoppingListItemResponse> {
        return try {
            val response = apiService.addShoppingListItem(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
