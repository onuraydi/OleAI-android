package com.onur.oleai_android.features.recipe_detail.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.onur.oleai_android.core.network.ApiService
import com.onur.oleai_android.core.network.dto.RecipeCommentResponse

class RecipeCommentsPagingSource(
    private val apiService: ApiService,
    private val recipeId: String
) : PagingSource<Int, RecipeCommentResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecipeCommentResponse> {
        val page = params.key ?: 0
        return try {
            val response = apiService.getRecipeComments(
                id = recipeId,
                page = page,
                size = params.loadSize
            )

            LoadResult.Page(
                data = response.items,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (response.hasNextPage || response.hasNext) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RecipeCommentResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
