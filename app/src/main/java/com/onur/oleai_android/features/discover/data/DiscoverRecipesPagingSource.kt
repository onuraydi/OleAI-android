package com.onur.oleai_android.features.discover.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.onur.oleai_android.core.network.dto.RecipeListItemResponse

class DiscoverRecipesPagingSource(
    private val repository: DiscoverRepository,
    private val searchTerm: String?,
    private val categoryId: Int?,
    private val maxTotalTime: Int?,
    private val minRating: Double?,
    private val difficulty: Int?,
    private val tagId: Int?,
    private val sortBy: String?
) : PagingSource<Int, RecipeListItemResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecipeListItemResponse> {
        val page = params.key ?: 0
        return try {
            val response = repository.getDiscoverRecipes(
                page = page,
                size = params.loadSize,
                searchTerm = searchTerm,
                categoryId = categoryId,
                maxTotalTime = maxTotalTime,
                minRating = minRating,
                difficulty = difficulty,
                tagId = tagId,
                sortBy = sortBy
            )
            LoadResult.Page(
                data = response.items,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (response.items.isEmpty() || !response.hasNextPage) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, RecipeListItemResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
