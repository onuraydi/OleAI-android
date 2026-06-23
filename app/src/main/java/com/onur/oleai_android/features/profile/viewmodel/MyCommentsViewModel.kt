package com.onur.oleai_android.features.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onur.oleai_android.core.network.dto.MyCommentDto
import com.onur.oleai_android.features.profile.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MyCommentsState(
    val comments: List<MyCommentDto> = emptyList(),
    val totalComments: Int = 0,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val sortBy: String = "En Yeni",
    val page: Int = 0,
    val pageSize: Int = 10,
    val hasMore: Boolean = true
)

@HiltViewModel
class MyCommentsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MyCommentsState())
    val state: StateFlow<MyCommentsState> = _state.asStateFlow()

    init {
        fetchComments(isLoadMore = false)
    }

    fun onSearchQueryChanged(query: String) {
        _state.value = _state.value.copy(searchQuery = query)
    }

    fun onSearch() {
        fetchComments(isLoadMore = false)
    }

    fun applySort(sortBy: String) {
        _state.value = _state.value.copy(sortBy = sortBy)
        fetchComments(isLoadMore = false)
    }

    fun loadMoreComments() {
        if (_state.value.isLoadingMore || !_state.value.hasMore || _state.value.isLoading) return
        _state.value = _state.value.copy(page = _state.value.page + 1)
        fetchComments(isLoadMore = true)
    }

    private fun fetchComments(isLoadMore: Boolean = false) {
        if (!isLoadMore) {
            _state.value = _state.value.copy(isLoading = true, error = null, page = 0, hasMore = true)
        } else {
            _state.value = _state.value.copy(isLoadingMore = true, error = null)
        }

        viewModelScope.launch {
            val currentState = _state.value
            
            // Map SortBy to backend expected format
            val apiSortBy = when (currentState.sortBy) {
                "En Yeni" -> "newest"
                "En Eski" -> "oldest"
                "En Yüksek Puan" -> "highest_rating"
                "En Düşük Puan" -> "lowest_rating"
                else -> "newest"
            }
            
            val result = userRepository.getMyComments(
                page = currentState.page,
                size = currentState.pageSize,
                searchTerm = currentState.searchQuery.takeIf { it.isNotBlank() },
                sortBy = apiSortBy
            )

            result.onSuccess { response ->
                android.util.Log.d("MyCommentsViewModel", "Comments fetched successfully: ${response.items.size}")
                val newComments = if (isLoadMore) {
                    currentState.comments + response.items
                } else {
                    response.items
                }
                _state.value = _state.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    comments = newComments,
                    totalComments = response.totalItems,
                    hasMore = response.hasNextPage
                )
            }.onFailure { exception ->
                android.util.Log.e("MyCommentsViewModel", "Failed to fetch comments", exception)
                _state.value = _state.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = exception.message ?: "Yorumlar yüklenemedi",
                    comments = if (!isLoadMore) emptyList() else currentState.comments,
                    totalComments = if (!isLoadMore) 0 else currentState.totalComments,
                    hasMore = false
                )
            }
        }
    }

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            val result = userRepository.deleteComment(commentId)
            if (result.isSuccess) {
                fetchComments() // Refresh list after deletion
            } else {
                _state.value = _state.value.copy(error = "Yorum silinemedi")
            }
        }
    }
}
