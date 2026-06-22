package com.onur.oleai_android.features.shoppinglist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onur.oleai_android.core.network.dto.IngredientCategoryResponse
import com.onur.oleai_android.core.network.dto.IngredientSearchResultResponse
import com.onur.oleai_android.core.network.dto.MyShoppingListItemResponse
import com.onur.oleai_android.core.network.dto.AddMultipleShoppingListItemsRequest
import com.onur.oleai_android.core.network.dto.AddShoppingListItemRequest
import com.onur.oleai_android.core.network.dto.UpdateShoppingListItemRequest
import com.onur.oleai_android.features.pantry.data.PantryRepository
import com.onur.oleai_android.features.shoppinglist.data.ShoppingListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartShoppingListItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val masterIngredientId: String?,
    val displayName: String,
    val categoryName: String?,
    val categoryIconName: String?,
    val quantity: Double,
    val unit: Int,
    val suggestedCategoryId: String?
)

@OptIn(FlowPreview::class)
@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val shoppingListRepository: ShoppingListRepository,
    private val pantryRepository: PantryRepository
) : ViewModel() {

    private val _shoppingListItems = MutableStateFlow<List<MyShoppingListItemResponse>>(emptyList())
    val shoppingListItems: StateFlow<List<MyShoppingListItemResponse>> = _shoppingListItems.asStateFlow()

    private val _selectedItemIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedItemIds: StateFlow<Set<String>> = _selectedItemIds.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _sortOption = MutableStateFlow(0) // 0: Varsayılan, 1: İsim (A-Z)
    val sortOption: StateFlow<Int> = _sortOption.asStateFlow()

    private val _filterOption = MutableStateFlow(0) // 0: Tümü, 1: Eksikler (Not yet implemented deeply, maybe just filter out purchased?)
    val filterOption: StateFlow<Int> = _filterOption.asStateFlow()

    // Add Bottom Sheet states
    val searchQuery = MutableStateFlow("")
    
    private val _searchResults = MutableStateFlow<List<IngredientSearchResultResponse>>(emptyList())
    val searchResults: StateFlow<List<IngredientSearchResultResponse>> = _searchResults.asStateFlow()

    private val _ingredientCategories = MutableStateFlow<List<IngredientCategoryResponse>>(emptyList())
    val ingredientCategories: StateFlow<List<IngredientCategoryResponse>> = _ingredientCategories.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartShoppingListItem>>(emptyList())
    val cartItems: StateFlow<List<CartShoppingListItem>> = _cartItems.asStateFlow()

    private val _isAddBottomSheetVisible = MutableStateFlow(false)
    val isAddBottomSheetVisible: StateFlow<Boolean> = _isAddBottomSheetVisible.asStateFlow()

    private val _itemToUpdate = MutableStateFlow<MyShoppingListItemResponse?>(null)
    val itemToUpdate: StateFlow<MyShoppingListItemResponse?> = _itemToUpdate.asStateFlow()

    private val _popularCategories = MutableStateFlow<List<com.onur.oleai_android.core.network.dto.CategorizedPopularIngredientsResponse>>(emptyList())
    val popularCategories: StateFlow<List<com.onur.oleai_android.core.network.dto.CategorizedPopularIngredientsResponse>> = _popularCategories.asStateFlow()

    init {
        fetchShoppingList()
        fetchIngredientCategories()
        fetchPopularCategories()
        observeSearchQuery()
    }

    private fun fetchPopularCategories() {
        viewModelScope.launch {
            pantryRepository.getPopularIngredientsByCategory().fold(
                onSuccess = { categories -> _popularCategories.value = categories },
                onFailure = { _popularCategories.value = emptyList() }
            )
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.length >= 2) {
                        performSearch(query)
                    } else {
                        _searchResults.value = emptyList()
                    }
                }
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            pantryRepository.searchIngredients(query).fold(
                onSuccess = { results -> _searchResults.value = results },
                onFailure = { _searchResults.value = emptyList() }
            )
        }
    }

    private fun fetchIngredientCategories() {
        viewModelScope.launch {
            pantryRepository.getIngredientCategories().fold(
                onSuccess = { categories -> _ingredientCategories.value = categories },
                onFailure = { /* Fail silently */ }
            )
        }
    }

    fun fetchShoppingList() {
        viewModelScope.launch {
            _isLoading.value = true
            shoppingListRepository.getMyShoppingList().fold(
                onSuccess = { items ->
                    _shoppingListItems.value = items
                },
                onFailure = {
                    // Fail silently or show error
                }
            )
            _isLoading.value = false
        }
    }

    fun toggleItemSelection(id: String) {
        _selectedItemIds.update { current ->
            if (current.contains(id)) current - id else current + id
        }
    }

    fun setSortOption(option: Int) {
        _sortOption.value = option
    }

    fun setFilterOption(option: Int) {
        _filterOption.value = option
    }

    fun showAddBottomSheet() {
        _isAddBottomSheetVisible.value = true
    }

    fun hideAddBottomSheet() {
        _isAddBottomSheetVisible.value = false
        _cartItems.value = emptyList()
        searchQuery.value = ""
    }

    fun addToCart(item: CartShoppingListItem) {
        val existing = _cartItems.value.find { 
            it.masterIngredientId == item.masterIngredientId && it.displayName == item.displayName 
        }
        if (existing != null) {
            _cartItems.value = _cartItems.value.map {
                if (it == existing) it.copy(quantity = it.quantity + item.quantity) else it
            }
        } else {
            _cartItems.value = _cartItems.value + item
        }
        searchQuery.value = ""
        _searchResults.value = emptyList()
    }

    fun removeFromCart(id: String) {
        val item = _cartItems.value.find { it.id == id }
        if (item != null) {
            _cartItems.value = _cartItems.value - item
        }
    }

    fun submitCart() {
        viewModelScope.launch {
            _isLoading.value = true
            val requests = _cartItems.value.map { cartItem ->
                AddShoppingListItemRequest(
                    ingredientId = cartItem.masterIngredientId,
                    requestedName = if (cartItem.masterIngredientId == null) cartItem.displayName else null,
                    suggestedCategoryId = cartItem.suggestedCategoryId,
                    quantity = cartItem.quantity,
                    unit = cartItem.unit
                )
            }
            shoppingListRepository.addMultipleShoppingListItems(AddMultipleShoppingListItemsRequest(requests)).fold(
                onSuccess = {
                    hideAddBottomSheet()
                    fetchShoppingList()
                },
                onFailure = {
                    // Show error
                }
            )
            _isLoading.value = false
        }
    }

    fun bulkPurchaseSelected() {
        viewModelScope.launch {
            val selected = _selectedItemIds.value.toList()
            if (selected.isEmpty()) return@launch
            
            _isLoading.value = true
            shoppingListRepository.bulkMarkAsPurchased(selected).fold(
                onSuccess = {
                    _selectedItemIds.value = emptySet()
                    fetchShoppingList()
                },
                onFailure = {
                    // Show error
                }
            )
            _isLoading.value = false
        }
    }

    fun showUpdateDialog(item: MyShoppingListItemResponse) {
        _itemToUpdate.value = item
    }

    fun hideUpdateDialog() {
        _itemToUpdate.value = null
    }

    fun updateItem(id: String, quantity: Double, unit: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            shoppingListRepository.updateShoppingListItem(id, UpdateShoppingListItemRequest(quantity, unit)).fold(
                onSuccess = {
                    hideUpdateDialog()
                    fetchShoppingList()
                },
                onFailure = {
                    // Show error
                }
            )
            _isLoading.value = false
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            _isLoading.value = true
            shoppingListRepository.removeShoppingListItem(id).fold(
                onSuccess = {
                    hideUpdateDialog()
                    fetchShoppingList()
                },
                onFailure = {
                    // Show error
                }
            )
            _isLoading.value = false
        }
    }
}
