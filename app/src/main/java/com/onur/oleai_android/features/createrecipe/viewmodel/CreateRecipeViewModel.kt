package com.onur.oleai_android.features.createrecipe.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onur.oleai_android.core.network.ApiService
import com.onur.oleai_android.core.network.dto.CreateRecipeIngredientRequest
import com.onur.oleai_android.core.network.dto.CreateRecipeStepRequest
import com.onur.oleai_android.core.network.dto.RecipeCategoryResponse
import com.onur.oleai_android.core.network.dto.TagDto
import com.onur.oleai_android.core.network.dto.IngredientSearchResultResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

data class IngredientItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val ingredientId: String? = null,
    val name: String = "",
    val amount: String = "",
    val unitId: Int = 1,
    val isCustom: Boolean = false,
    val unitFamily: Int? = null,
    val unitFamilyStr: String? = null,
    val suggestedCategoryId: String? = null
)

data class StepItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val instruction: String = "",
    val imageUri: Uri? = null
)

data class CreateRecipeUiState(
    val title: String = "",
    val description: String = "",
    val mainImageUri: Uri? = null,
    val difficulty: String = "Easy", // Easy, Medium, Hard
    val visibility: String = "Public", // Public, Private
    val servings: String = "",
    val prepTime: String = "",
    val cookTime: String = "",
    
    // Categories and Tags
    val categories: List<RecipeCategoryResponse> = emptyList(),
    val tags: List<TagDto> = emptyList(),
    val ingredientCategories: List<com.onur.oleai_android.core.network.dto.IngredientCategoryResponse> = emptyList(),
    val selectedCategoryIds: List<Int> = emptyList(),
    val selectedTagIds: List<Int> = emptyList(),
    
    // Dynamic Lists
    val ingredients: List<IngredientItem> = listOf(IngredientItem()),
    val steps: List<StepItem> = listOf(StepItem()),
    
    // Ingredient Search
    val activeSearchIngredientId: String? = null,
    val ingredientSearchResults: List<IngredientSearchResultResponse> = emptyList(),
    
    // Submission state
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class CreateRecipeViewModel @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateRecipeUiState())
    val uiState: StateFlow<CreateRecipeUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadCategoriesAndTags()
    }

    private fun loadCategoriesAndTags() {
        viewModelScope.launch {
            try {
                val categories = apiService.getRecipeCategories()
                val tags = apiService.getRecipeTags()
                val ingCategories = apiService.getIngredientCategories()
                _uiState.update { it.copy(categories = categories, tags = tags, ingredientCategories = ingCategories) }
            } catch (e: Exception) {
                // Ignore for now
            }
        }
    }

    fun updateTitle(title: String) = _uiState.update { it.copy(title = title) }
    fun updateDescription(desc: String) = _uiState.update { it.copy(description = desc) }
    fun updateMainImage(uri: Uri?) = _uiState.update { it.copy(mainImageUri = uri) }
    fun updateDifficulty(diff: String) = _uiState.update { it.copy(difficulty = diff) }
    fun updateVisibility(vis: String) = _uiState.update { it.copy(visibility = vis) }
    fun updateServings(servings: String) = _uiState.update { it.copy(servings = servings) }
    fun updatePrepTime(time: String) = _uiState.update { it.copy(prepTime = time) }
    fun updateCookTime(time: String) = _uiState.update { it.copy(cookTime = time) }

    fun toggleCategory(id: Int) {
        _uiState.update { state ->
            val newCategories = if (state.selectedCategoryIds.contains(id)) {
                state.selectedCategoryIds - id
            } else {
                state.selectedCategoryIds + id
            }
            state.copy(selectedCategoryIds = newCategories)
        }
    }

    fun toggleTag(id: Int) {
        _uiState.update { state ->
            val newTags = if (state.selectedTagIds.contains(id)) {
                state.selectedTagIds - id
            } else {
                state.selectedTagIds + id
            }
            state.copy(selectedTagIds = newTags)
        }
    }

    // Ingredient Actions
    fun addIngredient() = _uiState.update { it.copy(ingredients = it.ingredients + IngredientItem()) }
    fun removeIngredient(id: String) = _uiState.update { state ->
        val newList = state.ingredients.filter { it.id != id }
        state.copy(ingredients = if (newList.isEmpty()) listOf(IngredientItem()) else newList)
    }
    fun updateIngredient(id: String, updatedItem: IngredientItem) = _uiState.update { state ->
        state.copy(ingredients = state.ingredients.map { if (it.id == id) updatedItem else it })
    }

    fun searchIngredient(id: String, keyword: String) {
        _uiState.update { state ->
            state.copy(
                activeSearchIngredientId = id,
                ingredients = state.ingredients.map { if (it.id == id) it.copy(name = keyword, ingredientId = null, isCustom = false) else it }
            )
        }
        
        searchJob?.cancel()
        if (keyword.length >= 2) {
            searchJob = viewModelScope.launch {
                try {
                    delay(300)
                    val results = apiService.searchIngredients(keyword)
                    _uiState.update { it.copy(ingredientSearchResults = results) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(ingredientSearchResults = emptyList()) }
                }
            }
        } else {
            _uiState.update { it.copy(ingredientSearchResults = emptyList(), activeSearchIngredientId = null) }
        }
    }

    fun selectIngredientResult(ingredientId: String, result: IngredientSearchResultResponse) {
        val unitId = when (result.unitFamily) {
            "Weight" -> 3 // Gr
            "Volume" -> 5 // Ml
            "Count" -> 1 // Adet
            else -> 1
        }
        _uiState.update { state ->
            state.copy(
                activeSearchIngredientId = null,
                ingredientSearchResults = emptyList(),
                ingredients = state.ingredients.map {
                    if (it.id == ingredientId) {
                        it.copy(
                            ingredientId = result.masterIngredientId,
                            name = result.displayName,
                            isCustom = false,
                            unitId = unitId,
                            unitFamilyStr = result.unitFamily
                        )
                    } else it
                }
            )
        }
    }

    fun selectCustomIngredient(ingredientId: String, customName: String) {
        _uiState.update { state ->
            state.copy(
                activeSearchIngredientId = null,
                ingredientSearchResults = emptyList(),
                ingredients = state.ingredients.map {
                    if (it.id == ingredientId) {
                        it.copy(
                            ingredientId = null,
                            name = customName,
                            isCustom = true,
                            unitId = 1,
                            unitFamilyStr = null
                        )
                    } else it
                }
            )
        }
    }

    fun addCustomIngredientToRecipe(
        ingredientId: String,
        customName: String,
        quantity: Double,
        unitId: Int,
        categoryId: String?,
        expirationDate: String?,
        estimatedShelfLifeInDays: Int?
    ) {
        viewModelScope.launch {
            try {
                // Update the recipe list directly without calling pantry/bulk
                // The API request for the custom ingredient will be sent via CreateRecipeIngredientRequest during submitRecipe
                _uiState.update { state ->
                    state.copy(
                        ingredients = state.ingredients.map {
                            if (it.id == ingredientId) {
                                it.copy(
                                    ingredientId = null,
                                    name = customName,
                                    isCustom = true,
                                    amount = quantity.toString().removeSuffix(".0"),
                                    unitId = unitId,
                                    unitFamilyStr = "all",
                                    suggestedCategoryId = categoryId
                                )
                            } else it
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Özel malzeme eklenemedi") }
            }
        }
    }

    // Step Actions
    fun addStep() = _uiState.update { it.copy(steps = it.steps + StepItem()) }
    fun removeStep(id: String) = _uiState.update { state ->
        val newList = state.steps.filter { it.id != id }
        state.copy(steps = if (newList.isEmpty()) listOf(StepItem()) else newList)
    }
    fun updateStep(id: String, updatedItem: StepItem) = _uiState.update { state ->
        state.copy(steps = state.steps.map { if (it.id == id) updatedItem else it })
    }
    
    fun dismissError() = _uiState.update { it.copy(error = null) }

    fun submitRecipe() {
        val state = _uiState.value
        
        // Basic validation
        if (state.title.isBlank()) {
            _uiState.update { it.copy(error = "Tarif adı boş olamaz") }
            return
        }
        if (state.mainImageUri == null) {
            _uiState.update { it.copy(error = "Lütfen kapak fotoğrafı yükleyin") }
            return
        }
        
        val validIngredients = state.ingredients.filter { (it.ingredientId != null || it.isCustom) && it.amount.isNotBlank() }
        val hasIncompleteIngredient = state.ingredients.any { 
            (it.name.isNotBlank() || it.amount.isNotBlank()) &&
            !((it.ingredientId != null || it.isCustom) && it.amount.isNotBlank())
        }
        
        if (hasIncompleteIngredient) {
            _uiState.update { it.copy(error = "Lütfen tüm malzemeleri eksiksiz doldurun. Yeni girdiğiniz malzemeler için listeden 'Yeni malzeme olarak ekle' seçeneğine tıklayarak kategori seçmelisiniz.") }
            return
        }
        
        if (validIngredients.isEmpty()) {
            _uiState.update { it.copy(error = "Lütfen en az bir malzeme ekleyin") }
            return
        }
        
        _uiState.update { it.copy(isLoading = true, error = null) }
        
        viewModelScope.launch {
            try {
                // Prepare Text Parts
                val titlePart = state.title.toRequestBody("text/plain".toMediaTypeOrNull())
                val descPart = state.description.toRequestBody("text/plain".toMediaTypeOrNull())
                val visPart = state.visibility.toRequestBody("text/plain".toMediaTypeOrNull())
                val diffPart = state.difficulty.toRequestBody("text/plain".toMediaTypeOrNull())
                val servingsPart = (state.servings.toIntOrNull() ?: 1).toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val prepTimePart = (state.prepTime.toIntOrNull() ?: 0).toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val cookTimePart = (state.cookTime.toIntOrNull() ?: 0).toString().toRequestBody("text/plain".toMediaTypeOrNull())
                
                val catIdsParts = state.selectedCategoryIds.map {
                    it.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                }
                
                val tagIdsParts = state.selectedTagIds.map {
                    it.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                }

                // Prepare JSON Parts
                val ingredientRequests = state.ingredients.mapNotNull {
                    val amount = it.amount.toDoubleOrNull() ?: return@mapNotNull null
                    CreateRecipeIngredientRequest(
                        ingredientId = it.ingredientId?.takeIf { id -> id.isNotBlank() },
                        requestedName = if (it.isCustom) it.name else null,
                        suggestedCategoryId = if (it.isCustom) it.suggestedCategoryId else null,
                        amount = amount,
                        unitId = it.unitId
                    )
                }
                
                val stepRequests = state.steps.mapIndexed { index, step ->
                    CreateRecipeStepRequest(
                        stepNumber = index + 1,
                        instruction = step.instruction
                    )
                }
                
                @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
                val jsonFormat = Json { explicitNulls = true; encodeDefaults = true }
                val ingredientsJson = jsonFormat.encodeToString(ingredientRequests).toRequestBody("application/json".toMediaTypeOrNull())
                val stepsJson = jsonFormat.encodeToString(stepRequests).toRequestBody("application/json".toMediaTypeOrNull())
                
                // Prepare Main Image
                val mainImageFile = getFileFromUri(state.mainImageUri)
                val mainImageRequestBody = mainImageFile.asRequestBody("image/*".toMediaTypeOrNull())
                val mainImagePart = MultipartBody.Part.createFormData("MainImage", mainImageFile.name, mainImageRequestBody)
                
                // Prepare Step Images
                val stepImages = mutableListOf<MultipartBody.Part>()
                val stepImageNumbers = mutableListOf<RequestBody>()
                
                state.steps.forEachIndexed { index, step ->
                    if (step.imageUri != null) {
                        val file = getFileFromUri(step.imageUri)
                        val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                        val part = MultipartBody.Part.createFormData("StepImages", file.name, reqFile)
                        stepImages.add(part)
                        stepImageNumbers.add((index + 1).toString().toRequestBody("text/plain".toMediaTypeOrNull()))
                    }
                }
                
                // Call API
                apiService.addRecipe(
                    title = titlePart,
                    description = descPart,
                    visibility = visPart,
                    difficulty = diffPart,
                    servings = servingsPart,
                    prepTime = prepTimePart,
                    cookTime = cookTimePart,
                    categoryIds = catIdsParts,
                    tagIds = tagIdsParts,
                    ingredientsJson = ingredientsJson,
                    stepsJson = stepsJson,
                    mainImage = mainImagePart,
                    stepImages = stepImages,
                    stepImageStepNumbers = stepImageNumbers
                )
                
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage ?: "Bir hata oluştu") }
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return tempFile
    }
}
