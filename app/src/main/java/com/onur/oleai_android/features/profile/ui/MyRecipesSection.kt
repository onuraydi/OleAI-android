package com.onur.oleai_android.features.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.onur.oleai_android.features.home.ui.components.RecipeCard
import com.onur.oleai_android.features.profile.viewmodel.MyRecipesTab
import com.onur.oleai_android.features.profile.viewmodel.MyRecipesViewModel

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRecipesSection(
    modifier: Modifier = Modifier,
    viewModel: MyRecipesViewModel = hiltViewModel(),
    onCreateRecipeClick: () -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
    onRecipeClick: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val focusManager = LocalFocusManager.current
    var isFilterOpen by remember { mutableStateOf(false) }
    var isSortDropdownOpen by remember { mutableStateOf(false) }

    val isScrollToEnd by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            lastVisibleItemIndex > (totalItemsNumber - 2)
        }
    }

    LaunchedEffect(isScrollToEnd) {
        if (isScrollToEnd && !state.isLoading && !state.isLoadingMore && state.hasMore && state.recipes.isNotEmpty()) {
            viewModel.loadMoreRecipes()
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.MenuBook,
                contentDescription = null,
                tint = Color(0xFF7CB342),
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "Tarif Defterim",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Sub Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1E211D))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf(
                Triple("Oluşturduklarım", Icons.Outlined.Edit, MyRecipesTab.CREATED),
                Triple("Kaydedilenler", Icons.Outlined.BookmarkBorder, MyRecipesTab.SAVED),
                Triple("Beğenilenler", Icons.Outlined.FavoriteBorder, MyRecipesTab.LIKED),
                Triple("Beğenilmeyenler", Icons.Outlined.ThumbDown, MyRecipesTab.DISLIKED)
            )

            tabs.forEach { (title, icon, tabEnum) ->
                val isSelected = state.selectedTab == tabEnum
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFF7CB342) else Color.Transparent)
                        .clickable { viewModel.onTabSelected(tabEnum) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.Black else Color.LightGray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        color = if (isSelected) Color.Black else Color.LightGray,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }

        // Search & Filter Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Bar
            TextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                placeholder = { Text("Tarif ara...", color = Color.Gray, fontSize = 12.sp) },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        viewModel.onSearch()
                    }
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1E211D),
                    unfocusedContainerColor = Color(0xFF1E211D),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Filter Button
            Surface(
                color = if (isFilterOpen) Color(0xFF7CB342) else Color(0xFF1E211D),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clickable { isFilterOpen = !isFilterOpen }
                        .padding(horizontal = 12.dp)
                ) {
                    Icon(
                        Icons.Outlined.Tune, 
                        contentDescription = "Filtreler", 
                        tint = if (isFilterOpen) Color.Black else Color.LightGray, 
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "Filtreler", 
                        color = if (isFilterOpen) Color.Black else Color.LightGray, 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Sort Dropdown
            Box {
                Surface(
                    color = Color(0xFF1E211D),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clickable { isSortDropdownOpen = true }
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(state.sortBy, color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = "Sırala", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                    }
                }
                DropdownMenu(
                    expanded = isSortDropdownOpen,
                    onDismissRequest = { isSortDropdownOpen = false },
                    modifier = Modifier.background(Color(0xFF1E211D))
                ) {
                    val sortOptions = listOf("En Yeni", "Popüler", "En Çok Oylanan", "En Hızlı")
                    sortOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = if (option == state.sortBy) Color(0xFF81B8EF) else Color.White) },
                            onClick = {
                                isSortDropdownOpen = false
                                viewModel.applySort(option)
                            }
                        )
                    }
                }
            }
        }

        // Filter Expandable Panel - Removed
        // Use BottomSheet instead

        // Tags Row
        if (state.tags.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "Tümü"
                val isAllSelected = state.tagId == null
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isAllSelected) Color(0xFF7CB342) else Color(0xFF1E211D))
                        .clickable { viewModel.onTagSelected(null) }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        "Tümü", 
                        color = if (isAllSelected) Color.Black else Color.LightGray, 
                        fontSize = 12.sp, 
                        fontWeight = if (isAllSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
                
                state.tags.forEach { tag ->
                    val isSelected = state.tagId == tag.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color(0xFF7CB342) else Color(0xFF1E211D))
                            .clickable { viewModel.onTagSelected(tag.id) }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            tag.name, 
                            color = if (isSelected) Color.Black else Color.LightGray, 
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Content Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF161815)) // slightly darker than 1E211D
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(color = Color(0xFF7CB342))
            } else if (state.recipes.isEmpty()) {
                // Empty State
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E211D)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF7CB342),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    
                    Text(
                        text = "Tarif Bulunamadı",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Henüz kendi tarifini oluşturmadın. Yapay zeka ile hemen yaratıcı tarifler oluşturmaya başla!",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    
                    Button(
                        onClick = onCreateRecipeClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7CB342)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("İlk Tarifini Oluştur", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // List of Recipes (Fixed for nested scrolling)
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val recipesChunked = state.recipes.chunked(2)
                    recipesChunked.forEach { rowRecipes ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            for (recipe in rowRecipes) {
                                Box(modifier = Modifier.weight(1f)) {
                                    RecipeCard(
                                        recipe = recipe,
                                        onClick = { onRecipeClick(recipe.id) }
                                    )
                                }
                            }
                            if (rowRecipes.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                    
                    if (state.isLoadingMore) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFF7CB342),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (isFilterOpen) {
        MyRecipesFilterBottomSheet(
            viewModel = viewModel,
            onDismiss = { isFilterOpen = false }
        )
    }
}
