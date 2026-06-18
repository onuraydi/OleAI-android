package com.onur.oleai_android.features.home.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.onur.oleai_android.core.network.dto.RecipeListItemResponse
import java.io.IOException

class PersonalizedRecipesPagingSource(
    private val repository: HomeRepository,
    private val categoryId: Int?
) : PagingSource<Int, RecipeListItemResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecipeListItemResponse> {
        val page = params.key ?: 0
        return try {
            val response = repository.getPersonalizedRecipes(
                categoryId = categoryId,
                page = page,
                size = params.loadSize
            )

            LoadResult.Page(
                data = response.items,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (response.items.isEmpty() || !response.hasNextPage) null else page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RecipeListItemResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
