package com.onur.oleai_android.features.notifications.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.onur.oleai_android.core.network.dto.NotificationDto
import java.io.IOException

class NotificationsPagingSource(
    private val repository: NotificationRepository,
    private val onlyUnread: Boolean
) : PagingSource<Int, NotificationDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationDto> {
        val page = params.key ?: 0
        return try {
            val response = repository.getMyNotifications(
                page = page,
                size = params.loadSize,
                onlyUnread = onlyUnread
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

    override fun getRefreshKey(state: PagingState<Int, NotificationDto>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
