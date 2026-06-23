package com.onur.oleai_android.features.pantry.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.onur.oleai_android.core.network.dto.MyPantryItemResponse
import com.onur.oleai_android.core.ui.utils.IconMapper
import com.onur.oleai_android.features.pantry.ui.components.PantryItemCard
import com.onur.oleai_android.features.pantry.viewmodel.PantryUiState
import com.onur.oleai_android.features.pantry.viewmodel.PantryViewModel
import com.onur.oleai_android.features.pantry.ui.components.AddPantryItemToShoppingListDialog
import com.onur.oleai_android.features.pantry.ui.components.UpdatePantryItemDialog
import com.onur.oleai_android.features.pantry.ui.components.PantryItemOptionsBottomSheet
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(
    modifier: Modifier = Modifier,
    viewModel: PantryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isBottomSheetVisible by viewModel.isBottomSheetVisible.collectAsState()
    var localSearchQuery by remember { mutableStateOf("") }
    
    val showUpdateDialog by viewModel.showUpdateDialog.collectAsState()
    val itemToUpdate by viewModel.itemToUpdate.collectAsState()
    val showShoppingListDialog by viewModel.showShoppingListDialog.collectAsState()
    val itemToAddToShoppingList by viewModel.itemToAddToShoppingList.collectAsState()
    val showDeleteConfirmDialog by viewModel.showDeleteConfirmDialog.collectAsState()
    val itemToDelete by viewModel.itemToDelete.collectAsState()
    val operationLoading by viewModel.operationLoading.collectAsState()

    var optionsBottomSheetItem by remember { mutableStateOf<MyPantryItemResponse?>(null) }

    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
        // Header
        Text(
            text = "Mutfak Dolabım",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Search Row
        OutlinedTextField(
            value = localSearchQuery,
            onValueChange = { localSearchQuery = it },
            placeholder = {
                Text(
                    text = "Dolabındaki malzemelerde ara...",
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
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Pantry Content
        val isRefreshing = uiState is PantryUiState.Loading
        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.fetchMyPantry() },
            modifier = Modifier.fillMaxSize()
        ) {
            when (uiState) {
            is PantryUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF8DC63F))
                }
            }
            is PantryUiState.Error -> {
                val message = (uiState as PantryUiState.Error).message
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Hata: $message", color = MaterialTheme.colorScheme.error)
                }
            }
            is PantryUiState.Success -> {
                val categorizedItems = (uiState as PantryUiState.Success).pantryItems
                
                val filteredItems = if (localSearchQuery.isEmpty()) {
                    categorizedItems
                } else {
                    categorizedItems.mapValues { (_, items) ->
                        items.filter { it.name.contains(localSearchQuery, ignoreCase = true) }
                    }.filterValues { it.isNotEmpty() }
                }
                
                if (filteredItems.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (localSearchQuery.isNotEmpty()) "Sonuç bulunamadı." else "Dolabınız şu an boş.\nYeni malzeme ekleyerek başlayabilirsiniz.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        filteredItems.forEach { (categoryName, filteredCategoryItems) ->
                            item(key = categoryName) {
                                CategorySection(
                                    categoryName = categoryName,
                                    items = filteredCategoryItems,
                                    onOptionsClick = { optionsBottomSheetItem = it }
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }
        } // closes when
    } // closes PullToRefreshBox
} // closes Column
    
    // FAB
    ExtendedFloatingActionButton(
        onClick = { viewModel.setBottomSheetVisible(true) },
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

    if (isBottomSheetVisible) {
        AddIngredientBottomSheet(
            viewModel = viewModel,
            onDismissRequest = { viewModel.setBottomSheetVisible(false) }
        )
    }

    val currentOptionsItem = optionsBottomSheetItem
    if (currentOptionsItem != null) {
        PantryItemOptionsBottomSheet(
            item = currentOptionsItem,
            onDismissRequest = { optionsBottomSheetItem = null },
            onEditClick = { viewModel.openUpdateDialog(currentOptionsItem) },
            onShoppingCartClick = { viewModel.openShoppingListDialog(currentOptionsItem) },
            onDeleteClick = { viewModel.openDeleteConfirmDialog(currentOptionsItem) }
        )
    }

    if (showUpdateDialog && itemToUpdate != null) {
        UpdatePantryItemDialog(
            item = itemToUpdate!!,
            onDismiss = { viewModel.closeUpdateDialog() },
            onUpdate = { qty, unit, exp ->
                viewModel.updateIngredientQuantity(
                    id = itemToUpdate!!.id,
                    newQuantity = qty,
                    newUnit = unit,
                    expirationDate = exp,
                    onSuccess = { Toast.makeText(context, "Miktar güncellendi", Toast.LENGTH_SHORT).show() },
                    onError = { err -> Toast.makeText(context, err, Toast.LENGTH_SHORT).show() }
                )
            }
        )
    }

    if (showShoppingListDialog && itemToAddToShoppingList != null) {
        AddPantryItemToShoppingListDialog(
            item = itemToAddToShoppingList!!,
            onDismiss = { viewModel.closeShoppingListDialog() },
            onAdd = { qty, unit ->
                val mId = itemToAddToShoppingList!!.masterIngredientId.takeIf { !it.isNullOrBlank() }
                val rName = if (mId == null) itemToAddToShoppingList!!.name else null
                viewModel.addIngredientToShoppingList(
                    ingredientId = mId,
                    requestedName = rName,
                    suggestedCategoryId = null,
                    quantity = qty,
                    unit = unit,
                    onSuccess = { Toast.makeText(context, "Alışveriş listesine eklendi", Toast.LENGTH_SHORT).show() },
                    onError = { err -> Toast.makeText(context, err, Toast.LENGTH_SHORT).show() }
                )
            }
        )
    }

    if (showDeleteConfirmDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { viewModel.closeDeleteConfirmDialog() },
            title = { Text(text = "Silme Onayı") },
            text = { Text(text = "${itemToDelete?.name} ürününü dolabınızdan silmek istediğinize emin misiniz?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteIngredient(
                        id = itemToDelete!!.id,
                        onSuccess = { Toast.makeText(context, "Ürün silindi", Toast.LENGTH_SHORT).show() },
                        onError = { err -> Toast.makeText(context, err, Toast.LENGTH_SHORT).show() }
                    )
                }) {
                    Text("Sil", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeDeleteConfirmDialog() }) {
                    Text("İptal")
                }
            }
        )
    }

    if (operationLoading) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF8DC63F))
        }
    }
} // closes Box
} // closes PantryScreen

@Composable
fun CategorySection(
    categoryName: String,
    items: List<MyPantryItemResponse>,
    onOptionsClick: (MyPantryItemResponse) -> Unit
) {
    var isExpanded by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(true) }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 8.dp)
        ) {
            // Use a leaf icon as default for category header if we don't have the specific icon here easily accessible,
            // though we could extract it from the first item
            val iconName = items.firstOrNull()?.categoryIconName?.takeIf { it.isNotEmpty() } ?: "leaf"
            
            Icon(
                imageVector = IconMapper.getIconByName(iconName),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = categoryName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Count badge
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = items.size.toString(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Daralt" else "Genişlet",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        AnimatedVisibility(visible = isExpanded) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                items(items) { item ->
                    PantryItemCard(
                        item = item,
                        onClick = { onOptionsClick(item) }
                    )
                }
            }
        }
    }
}
