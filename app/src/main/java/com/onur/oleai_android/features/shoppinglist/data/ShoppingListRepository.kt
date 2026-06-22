package com.onur.oleai_android.features.shoppinglist.data

import com.onur.oleai_android.core.network.ApiService
import com.onur.oleai_android.core.network.dto.*
import javax.inject.Inject

class ShoppingListRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getMyShoppingList(): Result<List<MyShoppingListItemResponse>> {
        return try {
            val response = apiService.getMyShoppingList()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addShoppingListItem(request: AddShoppingListItemRequest): Result<ShoppingListItemResponse> {
        return try {
            val response = apiService.addShoppingListItem(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addMultipleShoppingListItems(request: AddMultipleShoppingListItemsRequest): Result<Unit> {
        return try {
            apiService.addMultipleShoppingListItems(request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateShoppingListItem(id: String, request: UpdateShoppingListItemRequest): Result<Unit> {
        return try {
            apiService.updateShoppingListItem(id, request)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAsPurchased(id: String): Result<Unit> {
        return try {
            apiService.markAsPurchased(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun bulkMarkAsPurchased(itemIds: List<String>): Result<Unit> {
        return try {
            apiService.bulkMarkAsPurchased(itemIds)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeShoppingListItem(id: String): Result<Unit> {
        return try {
            apiService.removeShoppingListItem(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
