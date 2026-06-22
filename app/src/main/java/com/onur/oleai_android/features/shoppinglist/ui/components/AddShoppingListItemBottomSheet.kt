package com.onur.oleai_android.features.shoppinglist.ui.components

import android.widget.Toast

import androidx.compose.animation.AnimatedVisibility
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onur.oleai_android.core.network.dto.CategorizedPopularIngredientsResponse
import com.onur.oleai_android.core.network.dto.IngredientSearchResultResponse
import com.onur.oleai_android.core.ui.utils.IconMapper
import com.onur.oleai_android.features.shoppinglist.viewmodel.CartShoppingListItem
import com.onur.oleai_android.features.shoppinglist.viewmodel.ShoppingListViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddShoppingListItemBottomSheet(
    viewModel: ShoppingListViewModel,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    
    val popularCategories by viewModel.popularCategories.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val isAddLoading by viewModel.isLoading.collectAsState()
    
    var selectedIngredient by remember { mutableStateOf<IngredientSearchResultResponse?>(null) }
    var showCartView by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val view = LocalView.current
    SideEffect {
        val window = (view.parent as? DialogWindowProvider)?.window
        if (window != null) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.searchQuery.value = ""
            onDismissRequest()
        },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        if (showCartView) {
            CartReviewView(
                cartItems = cartItems,
                onBack = { showCartView = false },
                onRemoveItem = { viewModel.removeFromCart(it) },
                onConfirm = {
                    if (!isAddLoading) {
                        viewModel.submitCart() 
                        Toast.makeText(context, "Ekleme işlemi başarılı", Toast.LENGTH_SHORT).show() 
                        coroutineScope.launch { 
                            sheetState.hide() 
                            if (!sheetState.isVisible) { 
                                showCartView = false 
                                viewModel.searchQuery.value = "" 
                                onDismissRequest() 
                            } 
                        }
                    }
                },
                isAddLoading = isAddLoading
            )
        } else if (selectedIngredient != null) {
            // Configuration View
            val ingredientCategories by viewModel.ingredientCategories.collectAsState()
            IngredientConfigView(
                ingredient = selectedIngredient!!,
                ingredientCategories = ingredientCategories,
                onBack = { selectedIngredient = null },
                onAddToCart = { quantity, unit, suggestedCategoryId ->
                    viewModel.addToCart(
                        CartShoppingListItem(
                            masterIngredientId = selectedIngredient!!.masterIngredientId.takeIf { it.isNotBlank() },
                            displayName = selectedIngredient!!.displayName,
                            categoryName = selectedIngredient!!.categoryName,
                            categoryIconName = selectedIngredient!!.categoryIconName,
                            quantity = quantity,
                            unit = unit,
                            suggestedCategoryId = suggestedCategoryId
                        )
                    )
                    selectedIngredient = null
                }
            )
        } else {
            // Main View (Search & Popular)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.9f)
                    .padding(horizontal = 16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Listeye Malzeme Ekle",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "Arayın, detaylandırın ve listenize ekleyin.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat")
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Elma, Un, Süt...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8DC63F),
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Content
                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isNotEmpty()) {
                        // Search Results
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(searchResults) { item ->
                                SearchResultItem(
                                    item = item,
                                    isInCart = cartItems.any { it.masterIngredientId == item.masterIngredientId },
                                    onClick = { selectedIngredient = item }
                                )
                            }
                            
                            val exactMatch = searchResults.any { it.displayName.equals(searchQuery, ignoreCase = true) }
                            if (!exactMatch && searchQuery.isNotBlank()) {
                                item {
                                    CustomIngredientSearchResultItem(
                                        searchQuery = searchQuery,
                                        onClick = {
                                            selectedIngredient = IngredientSearchResultResponse(
                                                masterIngredientId = "", // indicates custom
                                                displayName = searchQuery,
                                                categoryName = "Özel Malzeme",
                                                categoryIconName = "leaf",
                                                unitFamily = "all" // allow all units
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        // Popular Categories
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(popularCategories) { category ->
                                PopularCategorySection(
                                    category = category,
                                    cartItems = cartItems,
                                    onItemClick = { selectedIngredient = it }
                                )
                            }
                        }
                    }
                }
                
                // Cart Bottom Bar
                AnimatedVisibility(visible = cartItems.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF4A6532)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (!isAddLoading) {
                                        showCartView = true
                                    }
                                }
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isAddLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "${cartItems.size} Ürünü Listeye Ekle",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PopularCategorySection(
    category: CategorizedPopularIngredientsResponse,
    cartItems: List<CartShoppingListItem>,
    onItemClick: (IngredientSearchResultResponse) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = IconMapper.getIconByName(category.categoryIconName ?: "leaf"),
                contentDescription = null,
                tint = Color(0xFF8DC63F),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = category.categoryName ?: "Kategori",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(category.ingredients) { item ->
                val isInCart = cartItems.any { it.masterIngredientId == item.masterIngredientId }
                IngredientChip(
                    text = item.displayName,
                    isInCart = isInCart,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

@Composable
fun IngredientChip(
    text: String,
    isInCart: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isInCart) Color(0xFF4A6532).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
        border = if (isInCart) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF8DC63F)) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isInCart) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color(0xFF8DC63F),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isInCart) Color(0xFF8DC63F) else Color.White,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun SearchResultItem(
    item: IngredientSearchResultResponse,
    isInCart: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = IconMapper.getIconByName(item.categoryIconName ?: "leaf"),
                contentDescription = null,
                tint = Color(0xFF8DC63F),
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.displayName,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
            )
            if (item.categoryName != null) {
                Text(
                    text = item.categoryName,
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
            }
        }
        
        if (isInCart) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color(0xFF8DC63F)
            )
        }
    }
}

@Composable
fun CustomIngredientSearchResultItem(
    searchQuery: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4A6532))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2A3625)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color(0xFF8DC63F),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "\"$searchQuery\" Bulunamadı",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8DC63F)
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Bunu özel malzeme olarak listenize ekleyebilirsiniz.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

fun getUnitsForFamily(familyStr: String): List<Pair<Int, String>> {
    return when (familyStr.lowercase()) {
        "weight" -> listOf(3 to "Gram", 2 to "Kilogram")
        "volume" -> listOf(5 to "Mililitre", 4 to "Litre")
        "count" -> listOf(1 to "Adet", 6 to "Paket", 7 to "Demet", 8 to "Dilim", 9 to "Tutam")
        "all" -> listOf(1 to "Adet", 2 to "Kilogram", 3 to "Gram", 4 to "Litre", 5 to "Mililitre", 6 to "Paket", 7 to "Demet", 8 to "Dilim", 9 to "Tutam", 99 to "Diğer")
        else -> listOf(1 to "Adet", 3 to "Gram", 5 to "Mililitre")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientConfigView(
    ingredient: IngredientSearchResultResponse,
    ingredientCategories: List<com.onur.oleai_android.core.network.dto.IngredientCategoryResponse>,
    onBack: () -> Unit,
    onAddToCart: (Double, Int, String?) -> Unit
) {
    var quantityStr by remember { mutableStateOf("1") }
    
    val availableUnits = remember(ingredient.unitFamily) { getUnitsForFamily(ingredient.unitFamily) }
    var selectedUnit by remember(ingredient.unitFamily) { mutableStateOf(availableUnits.first()) }
    var unitDropdownExpanded by remember { mutableStateOf(false) }
    
    val isCustom = ingredient.masterIngredientId.isBlank()
    var selectedCategory by remember { mutableStateOf<com.onur.oleai_android.core.network.dto.IngredientCategoryResponse?>(null) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Close, contentDescription = "İptal")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2A3625)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = IconMapper.getIconByName(ingredient.categoryIconName ?: "leaf"),
                        contentDescription = null,
                        tint = Color(0xFF8DC63F)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = ingredient.displayName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = if (isCustom) "Özel Malzeme" else (ingredient.categoryName ?: "Bilinmeyen Kategori"),
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (isCustom) {
            Text(
                text = "KATEGORİ (OPSİYONEL)",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
            )
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "-- Kategori Seçiniz --",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8DC63F),
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    ingredientCategories.forEach { categoryOption ->
                        DropdownMenuItem(
                            text = { Text(categoryOption.name) },
                            onClick = {
                                selectedCategory = categoryOption
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "MİKTAR",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = { quantityStr = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF8DC63F),
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "BİRİM",
                    style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                )
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = unitDropdownExpanded,
                    onExpandedChange = { unitDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedUnit.second,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF8DC63F),
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = unitDropdownExpanded,
                        onDismissRequest = { unitDropdownExpanded = false }
                    ) {
                        availableUnits.forEach { unitOption ->
                            DropdownMenuItem(
                                text = { Text(unitOption.second) },
                                onClick = {
                                    selectedUnit = unitOption
                                    unitDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                val qty = quantityStr.toDoubleOrNull() ?: 1.0
                onAddToCart(qty, selectedUnit.first, selectedCategory?.id)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Listeye Ekle", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CartReviewView(
    cartItems: List<com.onur.oleai_android.features.shoppinglist.viewmodel.CartShoppingListItem>,
    onBack: () -> Unit,
    onRemoveItem: (String) -> Unit,
    onConfirm: () -> Unit,
    isAddLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sepeti Onayla",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Items List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cartItems) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = IconMapper.getIconByName(item.categoryIconName ?: "leaf"),
                            contentDescription = null,
                            tint = Color(0xFF8DC63F),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.displayName,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = "${item.quantity} " + getUnitName(item.unit),
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                    }
                    
                    IconButton(onClick = { onRemoveItem(item.id) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kaldır",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = onConfirm,
            enabled = cartItems.isNotEmpty() && !isAddLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4A6532),
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isAddLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "${cartItems.size} Malzemeyi Onayla ve Ekle", fontWeight = FontWeight.Bold)
            }
        }
    }
}

fun getUnitName(unitId: Int): String {
    return when (unitId) {
        1 -> "Adet"
        2 -> "Kilogram"
        3 -> "Gram"
        4 -> "Litre"
        5 -> "Mililitre"
        6 -> "Paket"
        7 -> "Demet"
        8 -> "Dilim"
        9 -> "Tutam"
        else -> "Birim"
    }
}
