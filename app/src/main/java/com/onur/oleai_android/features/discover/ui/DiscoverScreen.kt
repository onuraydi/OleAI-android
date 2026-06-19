package com.onur.oleai_android.features.discover.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.onur.oleai_android.core.network.dto.RecipeCategoryResponse
import com.onur.oleai_android.features.discover.viewmodel.DiscoverViewModel
import com.onur.oleai_android.features.home.ui.components.RecipeCard

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DiscoverScreen(
    viewModel: DiscoverViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val pagedRecipes = viewModel.pagedRecipes.collectAsLazyPagingItems()

    var showFilterSheet by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(filterState.searchTerm) }
    var isSearchActive by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val isImeVisible = WindowInsets.isImeVisible

    LaunchedEffect(isImeVisible) {
        if (!isImeVisible && isSearchActive) {
            focusManager.clearFocus()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header and Search
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Tarifleri Keşfet",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.updateSearchTerm(it)
                },
                placeholder = { Text("Tarif ara...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Ara") },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        isSearchActive = focusState.isFocused
                    },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AnimatedVisibility(
                visible = !isSearchActive,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    // Sort & Filter Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Sort Dropdown
                        Box {
                            OutlinedButton(
                                onClick = { showSortMenu = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Icon(Icons.Default.Sort, contentDescription = "Sırala", modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = getSortLabel(filterState.sortBy),
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                            
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                val sortOptions = listOf(
                                    "popular" to "Popüler",
                                    "newest" to "En Yeni",
                                    "highest_rated" to "En Çok Oylanan",
                                    "fastest" to "En Hızlı"
                                )
                                sortOptions.forEach { (value, label) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            viewModel.applyFilters(
                                                maxTotalTime = filterState.maxTotalTime,
                                                minRating = filterState.minRating,
                                                difficulty = filterState.difficulty,
                                                tagId = filterState.tagId,
                                                sortBy = value
                                            )
                                            showSortMenu = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        // Filter Button
                        FilledTonalButton(
                            onClick = { showFilterSheet = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filtreler", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Filtreler",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Categories Row
                    if (uiState.categories.isNotEmpty()) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                CategoryChip(
                                    name = "Tümü",
                                    isSelected = filterState.categoryId == null,
                                    onClick = { viewModel.updateCategory(null) }
                                )
                            }
                            items(uiState.categories) { category ->
                                CategoryChip(
                                    name = category.name,
                                    isSelected = filterState.categoryId == category.id,
                                    onClick = { viewModel.updateCategory(category.id) }
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Recipe Grid
        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            if (pagedRecipes.loadState.refresh is LoadState.Loading) {
                CircularProgressIndicator()
            } else if (pagedRecipes.itemCount == 0 && pagedRecipes.loadState.refresh !is LoadState.Loading) {
                Text(
                    text = "Aramanıza uygun tarif bulunamadı.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(pagedRecipes.itemCount) { index ->
                        pagedRecipes[index]?.let { recipe ->
                            RecipeCard(
                                recipe = recipe,
                                onClick = { /* TODO: Navigate to detail */ }
                            )
                        }
                    }
                    
                    if (pagedRecipes.loadState.append is LoadState.Loading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        DiscoverFilterBottomSheet(
            viewModel = viewModel,
            onDismiss = { showFilterSheet = false }
        )
    }
}

@Composable
fun CategoryChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary 
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = name,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary 
                    else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

fun getSortLabel(value: String): String {
    return when (value) {
        "popular" -> "Popüler"
        "newest" -> "En Yeni"
        "highest_rated" -> "En Çok Oylanan"
        "fastest" -> "En Hızlı"
        else -> "Sırala"
    }
}
