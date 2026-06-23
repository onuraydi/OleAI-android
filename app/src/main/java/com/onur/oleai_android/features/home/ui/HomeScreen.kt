package com.onur.oleai_android.features.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Kitchen
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.onur.oleai_android.R
import com.onur.oleai_android.features.home.ui.components.CategoryDistributionCard
import com.onur.oleai_android.features.home.ui.components.FreshnessIndexCard
import com.onur.oleai_android.features.home.ui.components.HeroRecipeCard
import com.onur.oleai_android.features.home.ui.components.RecipeCard
import com.onur.oleai_android.features.home.ui.components.ShoppingProgressCard
import com.onur.oleai_android.features.home.viewmodel.HomeUiState
import com.onur.oleai_android.features.home.viewmodel.HomeViewModel
import com.onur.oleai_android.features.discover.ui.DiscoverScreen
import com.onur.oleai_android.features.pantry.ui.PantryScreen

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val navItems = remember {
        listOf(
            BottomNavItem("Ana Sayfa", Icons.Filled.Home, Icons.Outlined.Home),
            BottomNavItem("Keşfet", Icons.Filled.Explore, Icons.Outlined.Explore),
            BottomNavItem("AI Asistan", Icons.Filled.AutoAwesome, Icons.Filled.AutoAwesome),
            BottomNavItem("Listem", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart),
            BottomNavItem("Dolabım", Icons.Filled.Kitchen, Icons.Outlined.Kitchen)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "OleAI",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = Color(0xFFEF4444),
                                    contentColor = Color.White
                                ) {
                                    Text("2", fontSize = 10.sp)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Bildirimler",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                    IconButton(onClick = { selectedTab = 5 }) {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = "Profil",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                navItems.forEachIndexed { index, item ->
                    val isAiTab = index == 2

                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            if (isAiTab) {
                                // AI tab gets a special highlighted icon
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (selectedTab == index)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.selectedIcon,
                                        contentDescription = item.label,
                                        tint = if (selectedTab == index)
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = if (selectedTab == index) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            }
                        },
                        label = {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = if (isAiTab)
                                Color.Transparent
                            else
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> HomeContent(
                viewModel = viewModel,
                uiState = uiState,
                modifier = Modifier.padding(innerPadding)
            )
            1 -> DiscoverScreen(
                modifier = Modifier.padding(innerPadding)
            )
            2 -> PlaceholderTab(
                title = "AI Asistan",
                subtitle = "Yapay zeka ile tarif önerisi al,\nmalzemelerine göre yemek bul",
                icon = Icons.Filled.AutoAwesome,
                modifier = Modifier.padding(innerPadding)
            )
            3 -> com.onur.oleai_android.features.shoppinglist.ui.ShoppingListScreen(
                modifier = Modifier.padding(innerPadding)
            )
            4 -> PantryScreen(
                modifier = Modifier.padding(innerPadding)
            )
            5 -> com.onur.oleai_android.features.profile.ui.ProfileScreen(
                modifier = Modifier
                    .padding(innerPadding)
                    .consumeWindowInsets(innerPadding),
                onLogoutClick = onLogout
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    viewModel: HomeViewModel,
    uiState: HomeUiState,
    modifier: Modifier = Modifier
) {
    val pagingRecipes = viewModel.personalizedRecipesPagingData.collectAsLazyPagingItems()
    
    // Manage pull to refresh loading state
    val isRefreshing = uiState.isRefreshing || pagingRecipes.loadState.refresh is LoadState.Loading

    if (uiState.isLoading && !isRefreshing) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    if (uiState.error != null && !isRefreshing) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = uiState.error ?: "Bir hata oluştu", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { 
            viewModel.refreshHomeData()
            pagingRecipes.refresh()
        },
        modifier = modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Text(
                        text = "Hoş geldin, ${uiState.firstName} 👋",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Bugün mutfakta neler yapıyoruz?",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Quick Actions
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item { QuickActionChip(icon = Icons.Default.AutoAwesome, text = "Bana Tarif Öner", isHighlighted = true) }
                        item { QuickActionChip(icon = Icons.Default.Add, text = "Yeni Ekle", isHighlighted = false) }
                        item { QuickActionChip(icon = Icons.Default.Bookmark, text = "Kaydedilenler", isHighlighted = false) }
                        item { QuickActionChip(icon = Icons.AutoMirrored.Filled.MenuBook, text = "Tarif Defterim", isHighlighted = false) }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // Trending Recipes
            if (uiState.trendingRecipes.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column {
                        Text(
                            text = "Günün Popüler Tarifleri",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Topluluğumuzun bugün en çok sevdiği lezzetler",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        val pagerState = rememberPagerState(pageCount = { uiState.trendingRecipes.size })
                        HorizontalPager(
                            state = pagerState,
                            contentPadding = PaddingValues(end = 32.dp),
                            pageSpacing = 16.dp
                        ) { page ->
                            HeroRecipeCard(
                                recipe = uiState.trendingRecipes[page],
                                onClick = { /* Navigate to Recipe Detail */ }
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }

            // Dashboard Stats
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Text(
                        text = "Mutfak Özeti",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        CategoryDistributionCard(
                            total = uiState.totalPantryItems,
                            topCategories = uiState.topCategories
                        )
                        FreshnessIndexCard(
                            fresh = uiState.freshItems,
                            expiring = uiState.expiringItems,
                            expired = uiState.expiredItems
                        )
                        ShoppingProgressCard(
                            total = uiState.totalShoppingItems,
                            purchased = uiState.purchasedItems
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // Personalized Recipes Header & Filters
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Text(
                        text = "Sana Özel Öneriler",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Sevdiğin lezzetler ve diyetine uygun en yeni tarifler",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (uiState.categories.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FilterChip(
                                    text = "Tümü",
                                    isSelected = uiState.selectedCategoryId == null,
                                    onClick = { viewModel.onCategorySelected(null) }
                                )
                            }
                            items(uiState.categories) { category ->
                                FilterChip(
                                    text = category.name,
                                    isSelected = uiState.selectedCategoryId == category.id,
                                    onClick = { viewModel.onCategorySelected(category.id) }
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // Recipe Grid
            if (pagingRecipes.loadState.refresh is LoadState.Loading && pagingRecipes.itemCount == 0) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
            } else if (pagingRecipes.itemCount == 0 && pagingRecipes.loadState.refresh !is LoadState.Loading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Şu an sana özel bir tarif bulamadık.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 32.dp)
                    )
                }
            } else {
                items(pagingRecipes.itemCount) { index ->
                    val recipe = pagingRecipes[index]
                    if (recipe != null) {
                        RecipeCard(
                            recipe = recipe,
                            onClick = { /* Navigate */ },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                
                if (pagingRecipes.loadState.append is LoadState.Loading) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaceholderTab(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun QuickActionChip(icon: ImageVector, text: String, isHighlighted: Boolean) {
    val bgColor = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isHighlighted) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(bgColor)
            .clickable { /* action */ }
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, style = MaterialTheme.typography.titleSmall, color = contentColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 10.dp)
    ) {
        Text(text = text, color = textColor, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
    }
}
