package com.onur.oleai_android.features.pantry.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onur.oleai_android.core.network.dto.AddMultipleUserIngredientsCommand
import com.onur.oleai_android.core.network.dto.AddMultipleUserIngredientsRequest
import com.onur.oleai_android.core.network.dto.AddUserIngredientRequest
import com.onur.oleai_android.core.network.dto.CategorizedPopularIngredientsResponse
import com.onur.oleai_android.core.network.dto.IngredientSearchResultResponse
import com.onur.oleai_android.core.network.dto.MyPantryItemResponse
import com.onur.oleai_android.features.pantry.data.PantryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartIngredientItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val masterIngredientId: String?,
    val displayName: String,
    val quantity: Double,
    val unit: Int,
    val categoryName: String? = null,
    val categoryIconName: String? = null,
    val expirationDate: String? = null,
    val estimatedShelfLifeInDays: Int? = null,
    val suggestedCategoryId: String? = null
)

sealed class PantryUiState {
    object Loading : PantryUiState()
    data class Success(
        val pantryItems: Map<String, List<MyPantryItemResponse>> // Grouped by category
    ) : PantryUiState()
    data class Error(val message: String) : PantryUiState()
}

@HiltViewModel
class PantryViewModel @Inject constructor(
    private val pantryRepository: PantryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PantryUiState>(PantryUiState.Loading)
    val uiState: StateFlow<PantryUiState> = _uiState.asStateFlow()

    private val _popularCategories = MutableStateFlow<List<CategorizedPopularIngredientsResponse>>(emptyList())
    val popularCategories: StateFlow<List<CategorizedPopularIngredientsResponse>> = _popularCategories.asStateFlow()

    private val _ingredientCategories = MutableStateFlow<List<com.onur.oleai_android.core.network.dto.IngredientCategoryResponse>>(emptyList())
    val ingredientCategories: StateFlow<List<com.onur.oleai_android.core.network.dto.IngredientCategoryResponse>> = _ingredientCategories.asStateFlow()

    private val _searchResults = MutableStateFlow<List<IngredientSearchResultResponse>>(emptyList())
    val searchResults: StateFlow<List<IngredientSearchResultResponse>> = _searchResults.asStateFlow()

    val searchQuery = MutableStateFlow("")

    private val _cartItems = MutableStateFlow<List<CartIngredientItem>>(emptyList())
    val cartItems: StateFlow<List<CartIngredientItem>> = _cartItems.asStateFlow()

    private val _isAddLoading = MutableStateFlow(false)
    val isAddLoading: StateFlow<Boolean> = _isAddLoading.asStateFlow()

    private val _isBottomSheetVisible = MutableStateFlow(false)
    val isBottomSheetVisible: StateFlow<Boolean> = _isBottomSheetVisible.asStateFlow()

    private val _showUpdateDialog = MutableStateFlow(false)
    val showUpdateDialog: StateFlow<Boolean> = _showUpdateDialog.asStateFlow()

    private val _itemToUpdate = MutableStateFlow<MyPantryItemResponse?>(null)
    val itemToUpdate: StateFlow<MyPantryItemResponse?> = _itemToUpdate.asStateFlow()

    private val _showShoppingListDialog = MutableStateFlow(false)
    val showShoppingListDialog: StateFlow<Boolean> = _showShoppingListDialog.asStateFlow()

    private val _itemToAddToShoppingList = MutableStateFlow<MyPantryItemResponse?>(null)
    val itemToAddToShoppingList: StateFlow<MyPantryItemResponse?> = _itemToAddToShoppingList.asStateFlow()

    private val _showDeleteConfirmDialog = MutableStateFlow(false)
    val showDeleteConfirmDialog: StateFlow<Boolean> = _showDeleteConfirmDialog.asStateFlow()

    private val _itemToDelete = MutableStateFlow<MyPantryItemResponse?>(null)
    val itemToDelete: StateFlow<MyPantryItemResponse?> = _itemToDelete.asStateFlow()

    private val _operationLoading = MutableStateFlow(false)
    val operationLoading: StateFlow<Boolean> = _operationLoading.asStateFlow()

    init {
        fetchMyPantry()
        fetchPopularIngredients()
        fetchIngredientCategories()
        observeSearchQuery()
    }

    private fun fetchIngredientCategories() {
        viewModelScope.launch {
            pantryRepository.getIngredientCategories().fold(
                onSuccess = { categories ->
                    _ingredientCategories.value = categories
                },
                onFailure = {
                    // Fail silently or handle error
                }
            )
        }
    }

    fun fetchMyPantry() {
        viewModelScope.launch {
            _uiState.value = PantryUiState.Loading
            pantryRepository.getMyPantry().fold(
                onSuccess = { items ->
                    val grouped = items.groupBy { it.categoryName }
                    _uiState.value = PantryUiState.Success(grouped)
                },
                onFailure = { e ->
                    _uiState.value = PantryUiState.Error(e.message ?: "Bilinmeyen bir hata oluştu")
                }
            )
        }
    }

    private fun fetchPopularIngredients() {
        viewModelScope.launch {
            pantryRepository.getPopularIngredientsByCategory().onSuccess {
                _popularCategories.value = it
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQuery.debounce(500).collect { query ->
                if (query.length >= 2) {
                    pantryRepository.searchIngredients(query).onSuccess {
                        _searchResults.value = it
                    }
                } else {
                    _searchResults.value = emptyList()
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setBottomSheetVisible(visible: Boolean) {
        _isBottomSheetVisible.value = visible
        if (!visible) {
            updateSearchQuery("")
            _cartItems.value = emptyList()
        }
    }

    fun openUpdateDialog(item: MyPantryItemResponse) {
        _itemToUpdate.value = item
        _showUpdateDialog.value = true
    }

    fun closeUpdateDialog() {
        _showUpdateDialog.value = false
        _itemToUpdate.value = null
    }

    fun openShoppingListDialog(item: MyPantryItemResponse) {
        _itemToAddToShoppingList.value = item
        _showShoppingListDialog.value = true
    }

    fun closeShoppingListDialog() {
        _showShoppingListDialog.value = false
        _itemToAddToShoppingList.value = null
    }

    fun openDeleteConfirmDialog(item: MyPantryItemResponse) {
        _itemToDelete.value = item
        _showDeleteConfirmDialog.value = true
    }

    fun closeDeleteConfirmDialog() {
        _showDeleteConfirmDialog.value = false
        _itemToDelete.value = null
    }

    fun updateIngredientQuantity(
        id: String,
        newQuantity: Double,
        newUnit: Int? = null,
        expirationDate: String? = null,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _operationLoading.value = true
            val request = com.onur.oleai_android.core.network.dto.UpdateUserIngredientRequest(
                newQuantity = newQuantity,
                newUnit = newUnit,
                expirationDate = expirationDate
            )
            pantryRepository.updateIngredientQuantity(id, request).fold(
                onSuccess = {
                    fetchMyPantry()
                    closeUpdateDialog()
                    onSuccess()
                },
                onFailure = { e ->
                    onError(e.message ?: "Güncelleme sırasında bir hata oluştu")
                }
            )
            _operationLoading.value = false
        }
    }

    fun deleteIngredient(id: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _operationLoading.value = true
            pantryRepository.deleteIngredient(id).fold(
                onSuccess = {
                    fetchMyPantry()
                    closeDeleteConfirmDialog()
                    onSuccess()
                },
                onFailure = { e ->
                    onError(e.message ?: "Silme sırasında bir hata oluştu")
                }
            )
            _operationLoading.value = false
        }
    }

    fun addIngredientToShoppingList(
        ingredientId: String?,
        requestedName: String?,
        suggestedCategoryId: String?,
        quantity: Double,
        unit: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _operationLoading.value = true
            val request = com.onur.oleai_android.core.network.dto.AddShoppingListItemRequest(
                ingredientId = ingredientId,
                requestedName = requestedName,
                suggestedCategoryId = suggestedCategoryId,
                quantity = quantity,
                unit = unit
            )
            pantryRepository.addShoppingListItem(request).fold(
                onSuccess = {
                    closeShoppingListDialog()
                    onSuccess()
                },
                onFailure = { e ->
                    onError(e.message ?: "Listeye eklenirken bir hata oluştu")
                }
            )
            _operationLoading.value = false
        }
    }

    fun addToCart(item: CartIngredientItem) {
        _cartItems.value = _cartItems.value + item
    }

    fun removeFromCart(id: String) {
        _cartItems.value = _cartItems.value.filter { it.id != id }
    }
    
    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun addCartItemsToPantry(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (_cartItems.value.isEmpty()) return
        
        viewModelScope.launch {
            _isAddLoading.value = true
            
            // Map items
            val requests = _cartItems.value.map { item ->
                // Ensure custom ingredients send requestedName and null masterIngredientId
                val isCustom = item.masterIngredientId.isNullOrBlank()
                AddUserIngredientRequest(
                    userId = "00000000-0000-0000-0000-000000000000", // Empty Guid, backend uses token
                    ingredientId = if (isCustom) null else item.masterIngredientId,
                    requestedName = if (isCustom) item.displayName else null,
                    suggestedCategoryId = item.suggestedCategoryId,
                    quantity = item.quantity,
                    unit = item.unit,
                    expirationDate = item.expirationDate,
                    estimatedShelfLifeInDays = item.estimatedShelfLifeInDays
                )
            }
            
            pantryRepository.addMultipleIngredients(
                AddMultipleUserIngredientsCommand(
                    request = AddMultipleUserIngredientsRequest(ingredients = requests)
                )
            ).fold(
                onSuccess = {
                    clearCart()
                    fetchMyPantry()
                    onSuccess()
                },
                onFailure = { error ->
                    onError(error.message ?: "Bilinmeyen Hata")
                }
            )
            _isAddLoading.value = false
        }
    }
}
