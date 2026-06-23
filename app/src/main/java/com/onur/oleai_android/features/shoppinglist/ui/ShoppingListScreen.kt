package com.onur.oleai_android.features.shoppinglist.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.onur.oleai_android.core.network.dto.MyShoppingListItemResponse
import com.onur.oleai_android.core.ui.utils.IconMapper
import com.onur.oleai_android.features.shoppinglist.viewmodel.ShoppingListViewModel
import com.onur.oleai_android.features.shoppinglist.ui.components.AddShoppingListItemBottomSheet
import com.onur.oleai_android.features.shoppinglist.ui.components.UpdateShoppingListItemDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    modifier: Modifier = Modifier,
    viewModel: ShoppingListViewModel = hiltViewModel()
) {
    val shoppingListItems by viewModel.shoppingListItems.collectAsState()
    val selectedItemIds by viewModel.selectedItemIds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val sortOption by viewModel.sortOption.collectAsState()
    val filterOption by viewModel.filterOption.collectAsState()
    val isAddBottomSheetVisible by viewModel.isAddBottomSheetVisible.collectAsState()
    val itemToUpdate by viewModel.itemToUpdate.collectAsState()

    var sortExpanded by remember { mutableStateOf(false) }
    var filterExpanded by remember { mutableStateOf(false) }
    var localSearchQuery by remember { mutableStateOf("") }

    val totalItems = shoppingListItems.size
    val completedItems = shoppingListItems.count { it.isPurchased || selectedItemIds.contains(it.id) }

    val searchedList = if (localSearchQuery.isEmpty()) {
        shoppingListItems
    } else {
        shoppingListItems.filter { it.ingredientName.contains(localSearchQuery, ignoreCase = true) }
    }

    val filteredList = searchedList.filter {
        when (filterOption) {
            1 -> !it.isPurchased && !selectedItemIds.contains(it.id) // Eksikler
            else -> true
        }
    }

    val sortedAndFilteredList = when (sortOption) {
        1 -> filteredList.sortedBy { it.ingredientName }
        else -> filteredList.sortedByDescending { it.id } // Varsayılan
    }

    val groupedItems = sortedAndFilteredList.groupBy { it.categoryName }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Alışveriş Listesi",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Text(
                        text = "Haftalık kiler ve mutfak alışverişini planla.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Completion Circle
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { if (totalItems > 0) completedItems.toFloat() / totalItems.toFloat() else 0f },
                        modifier = Modifier.size(56.dp),
                        color = Color(0xFF8DC63F),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 4.dp
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${completedItems}/${totalItems}",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Search Row
            OutlinedTextField(
                value = localSearchQuery,
                onValueChange = { localSearchQuery = it },
                placeholder = {
                    Text(
                        text = "Listede ara...",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Ara",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF8DC63F),
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Actions Row (Sort, Filter)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    // Sort Dropdown
                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { sortExpanded = true }
                                .padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (sortOption == 0) "Varsayılan" else "İsim (A-Z)",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = sortExpanded,
                            onDismissRequest = { sortExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Varsayılan", color = MaterialTheme.colorScheme.onBackground) },
                                onClick = {
                                    viewModel.setSortOption(0)
                                    sortExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("İsim (A-Z)", color = MaterialTheme.colorScheme.onBackground) },
                                onClick = {
                                    viewModel.setSortOption(1)
                                    sortExpanded = false
                                }
                            )
                        }
                    }

                    // Filter Dropdown
                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { filterExpanded = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (filterOption == 0) "Tümü" else "Eksikler",
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = filterExpanded,
                            onDismissRequest = { filterExpanded = false },
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Tümü", color = MaterialTheme.colorScheme.onBackground) },
                                onClick = {
                                    viewModel.setFilterOption(0)
                                    filterExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Eksikler", color = MaterialTheme.colorScheme.onBackground) },
                                onClick = {
                                    viewModel.setFilterOption(1)
                                    filterExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading && shoppingListItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF8DC63F))
                }
            } else if (shoppingListItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Alışveriş listeniz boş.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else if (groupedItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Sonuç bulunamadı.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 100.dp) // padding for buttons
                ) {
                    groupedItems.forEach { (category, items) ->
                        item {
                            CategorySection(
                                categoryName = category ?: "Diğer",
                                itemCount = items.size,
                                items = items,
                                selectedItemIds = selectedItemIds,
                                onToggleSelection = { viewModel.toggleItemSelection(it) },
                                onCardClick = { viewModel.showUpdateDialog(it) }
                            )
                        }
                    }
                }
            }
        }

        // Fixed FAB for Adding item - Placed at the bottom matching PantryScreen
        ExtendedFloatingActionButton(
            onClick = { viewModel.showAddBottomSheet() },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Malzeme Ekle",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Malzeme Ekle", fontWeight = FontWeight.Bold)
        }

        // Fixed Bottom Complete Purchase Button (replaces FAB if items selected)
        if (selectedItemIds.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                Button(
                    onClick = { viewModel.bulkPurchaseSelected() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Alışverişi Tamamla (${selectedItemIds.size})", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (isAddBottomSheetVisible) {
        AddShoppingListItemBottomSheet(
            viewModel = viewModel,
            onDismissRequest = { viewModel.hideAddBottomSheet() }
        )
    }

    if (itemToUpdate != null) {
        UpdateShoppingListItemDialog(
            item = itemToUpdate!!,
            onDismiss = { viewModel.hideUpdateDialog() },
            onUpdate = { quantity, unit ->
                viewModel.updateItem(itemToUpdate!!.id, quantity, unit)
            },
            onDelete = {
                viewModel.deleteItem(itemToUpdate!!.id)
            }
        )
    }
}

@Composable
fun CategorySection(
    categoryName: String,
    itemCount: Int,
    items: List<MyShoppingListItemResponse>,
    selectedItemIds: Set<String>,
    onToggleSelection: (String) -> Unit,
    onCardClick: (MyShoppingListItemResponse) -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categoryName,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = itemCount.toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Daralt/Genişlet",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column {
                items.forEach { item ->
                    ShoppingListItemCard(
                        item = item,
                        isSelected = selectedItemIds.contains(item.id),
                        onToggleSelection = { onToggleSelection(item.id) },
                        onClick = { onCardClick(item) }
                    )
                }
            }
        }
    }
}

@Composable
fun ShoppingListItemCard(
    item: MyShoppingListItemResponse,
    isSelected: Boolean,
    onToggleSelection: () -> Unit,
    onClick: () -> Unit
) {
    val unitStr = item.unit.ifEmpty { "Adet" }
    val opacity = if (item.isPurchased) 0.5f else 1f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onToggleSelection,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                contentDescription = "Seç",
                tint = if (isSelected) Color(0xFF8DC63F) else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(48.dp) // matched with PantryItemCard
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = IconMapper.getIconByName(item.categoryIconName ?: "leaf"),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp).alpha(opacity)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f).alpha(opacity)) {
            Text(
                text = item.ingredientName,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${if (item.quantity % 1 == 0.0) item.quantity.toInt() else item.quantity} ${unitStr}",
                    color = Color(0xFF8DC63F),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                if (item.isPendingApproval) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• Onay Bekliyor",
                        color = Color(0xFFEAB308),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
