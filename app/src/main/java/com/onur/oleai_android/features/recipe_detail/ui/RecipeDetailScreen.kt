package com.onur.oleai_android.features.recipe_detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.onur.oleai_android.features.recipe_detail.viewmodel.CookingFlowState
import com.onur.oleai_android.features.recipe_detail.viewmodel.RecipeDetailViewModel
import com.onur.oleai_android.features.recipe_detail.ui.components.CookingAssistantDialog
import com.onur.oleai_android.features.recipe_detail.ui.components.DepletedIngredientsDialog
import com.onur.oleai_android.features.recipe_detail.ui.components.MissingIngredientsDialog
import com.onur.oleai_android.features.recipe_detail.ui.components.RecipeDetailHeader
import com.onur.oleai_android.features.recipe_detail.ui.components.RecipeDetailInfo
import com.onur.oleai_android.features.recipe_detail.ui.components.RecipeDetailIngredients
import com.onur.oleai_android.features.recipe_detail.ui.components.RecipeDetailSteps
import com.onur.oleai_android.features.recipe_detail.ui.components.RecipeDetailComments

import android.annotation.SuppressLint
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val commentsPagingItems = viewModel.commentsPagingData.collectAsLazyPagingItems()
    var isCommentsExpanded by remember { mutableStateOf(false) }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color(0xFF121212)
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF90B551))
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Hata: ${uiState.error}", color = Color.Red)
            }
        } else {
            val recipe = uiState.recipeDetail
            if (recipe != null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState
                    ) {
                    item {
                        val mainImages = recipe.images?.map { it.imageUrl } ?: emptyList()
                        val stepImages = recipe.steps?.mapNotNull { it.imageUrl }?.filter { it.isNotBlank() } ?: emptyList()
                        val allImages = (mainImages + stepImages).distinct().ifEmpty { listOf("") }
                        
                        RecipeDetailHeader(
                            images = allImages,
                            onNavigateBack = onNavigateBack,
                            onEditClick = if (recipe.userId == uiState.currentUserId) {
                                { onNavigateToEdit(recipe.id) }
                            } else null
                        )
                    }
                    item {
                        val context = androidx.compose.ui.platform.LocalContext.current
                        RecipeDetailInfo(
                            title = recipe.title,
                            description = recipe.description,
                            author = recipe.author,
                            statistics = recipe.statistics?.copy(
                                totalLikes = uiState.totalLikes,
                                totalDislikes = uiState.totalDislikes,
                                totalSaves = uiState.totalSaves,
                                totalComments = if (uiState.comments.isNotEmpty()) uiState.comments.size else recipe.statistics.totalComments
                            ) ?: com.onur.oleai_android.core.network.dto.RecipeStatisticsResponse(),
                            categories = recipe.categories ?: emptyList(),
                            tags = recipe.tags ?: emptyList(),
                            difficulty = recipe.difficulty ?: "Bilinmiyor",
                            servings = recipe.servings,
                            prepTime = recipe.prepTime ?: 0,
                            cookTime = recipe.cookTime ?: 0,
                            isLiked = uiState.isLiked,
                            isDisliked = uiState.isDisliked,
                            isSaved = uiState.isSaved,
                            onLikeClick = { viewModel.toggleLike() },
                            onDislikeClick = { viewModel.toggleDislike() },
                            onSaveClick = { viewModel.toggleSave() },
                            onShareClick = {
                                val sendIntent = android.content.Intent().apply {
                                    action = android.content.Intent.ACTION_SEND
                                    putExtra(android.content.Intent.EXTRA_TEXT, "Harika bir tarif buldum: ${recipe.title}\n\nSen de denemelisin!")
                                    type = "text/plain"
                                }
                                val shareIntent = android.content.Intent.createChooser(sendIntent, null)
                                context.startActivity(shareIntent)
                            },
                            onCommentClick = {
                                isCommentsExpanded = true
                                coroutineScope.launch {
                                    listState.animateScrollToItem(4)
                                }
                            }
                        )
                    }
                    item {
                        RecipeDetailIngredients(
                            ingredients = recipe.ingredients ?: emptyList(),
                            servings = uiState.currentServings,
                            baseServings = recipe.servings,
                            isMetric = uiState.isMetric,
                            onServingsChange = { viewModel.updateServings(it) },
                            onMetricToggle = { viewModel.toggleMetric() }
                        )
                    }
                    item {
                        RecipeDetailSteps(
                            steps = recipe.steps ?: emptyList()
                        )
                    }
                    item {
                        RecipeDetailComments(
                            totalComments = recipe.statistics?.totalComments ?: 0,
                            isExpanded = isCommentsExpanded,
                            onToggleExpand = { isCommentsExpanded = !isCommentsExpanded },
                            isEditing = uiState.editingComment != null,
                            editingComment = uiState.editingComment,
                            isSubmitting = uiState.isSubmittingComment,
                            onSubmitComment = { content, rating ->
                                viewModel.submitComment(content, rating) {
                                    commentsPagingItems.refresh()
                                }
                            },
                            onCancelEdit = { viewModel.cancelEditing() }
                        )
                    }
                    
                    if (isCommentsExpanded) {
                        items(
                            count = commentsPagingItems.itemCount,
                            key = { index -> commentsPagingItems[index]?.id ?: index }
                        ) { index ->
                            val comment = commentsPagingItems[index]
                            if (comment != null) {
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .fillMaxWidth()
                                        .background(Color(0xFF1E1E1E))
                                ) {
                                    com.onur.oleai_android.features.recipe_detail.ui.components.CommentItem(
                                        comment = comment,
                                        currentUserId = uiState.currentUserId,
                                        replyingToCommentId = uiState.replyingToComment?.id,
                                        onEditClick = { c -> viewModel.startEditingComment(c) },
                                        onDeleteClick = { c -> viewModel.confirmDeleteComment(c) },
                                        onReplyClick = { c -> viewModel.startReply(c) },
                                        onSubmitReply = { content ->
                                            viewModel.submitReply(content) {
                                                commentsPagingItems.refresh()
                                            }
                                        },
                                        onCancelReply = { viewModel.cancelReply() },
                                        isSubmittingReply = uiState.isSubmittingComment,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        item {
                            Box(modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth()
                                .background(Color(0xFF1E1E1E), RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                            ) {
                                if (commentsPagingItems.loadState.append is androidx.paging.LoadState.Loading) {
                                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = Color(0xFF90B551))
                                    }
                                } else {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(100.dp)) // Extra padding before the button
                    }
                }
                
                // Bottom Fixed Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF111111).copy(alpha = 0.8f),
                                    Color(0xFF111111),
                                    Color(0xFF111111)
                                )
                            )
                        )
                        .padding(top = 32.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { viewModel.startCookingFlow() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90B551))
                    ) {
                        Text("Tarifi Yap", color = Color.Black, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                    }
                }
            } // Close Box wrapper
            }
        }
    }

    // Delete comment confirm dialog
    uiState.showDeleteConfirmFor?.let { commentToDelete ->
        com.onur.oleai_android.features.recipe_detail.ui.components.DeleteCommentDialog(
            comment = commentToDelete,
            onConfirm = {
                viewModel.deleteComment(commentToDelete) {
                    commentsPagingItems.refresh()
                }
            },
            onDismiss = { viewModel.cancelDeleteComment() }
        )
    }

    // Handle Cooking Flow Dialogs
    when (uiState.cookingFlowState) {
        CookingFlowState.CheckingMissing -> {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF90B551))
            }
        }
        CookingFlowState.MissingIngredientsDialog -> {
            MissingIngredientsDialog(
                missingIngredients = uiState.missingIngredients,
                onDismiss = { viewModel.cancelCookingFlow() },
                onContinue = { ignoredIds -> viewModel.handleMissingIngredientsAction(ignoredIds) },
                onAddToList = { ingredients -> viewModel.addIngredientsToShoppingList(ingredients) },
                onAddToPantry = { ingredients -> viewModel.addIngredientsToPantry(uiState.recipeDetail?.userId ?: "", ingredients) }
            )
        }
        CookingFlowState.StartingCooking -> {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF90B551))
            }
        }
        CookingFlowState.CookingAssistant -> {
            CookingAssistantDialog(
                recipeTitle = uiState.recipeDetail?.title ?: "",
                steps = uiState.recipeDetail?.steps ?: emptyList(),
                onDismiss = { viewModel.cancelCookingFlow() },
                onFinish = { viewModel.finishCookingAssistant() }
            )
        }
        CookingFlowState.FinishedDialog -> {
            DepletedIngredientsDialog(
                depletedIngredients = uiState.depletedIngredients,
                onDismiss = { viewModel.cancelCookingFlow() },
                onAddToList = { ingredients ->
                    viewModel.addIngredientsToShoppingList(ingredients)
                    viewModel.cancelCookingFlow()
                }
            )
        }
        else -> {}
    }
}
