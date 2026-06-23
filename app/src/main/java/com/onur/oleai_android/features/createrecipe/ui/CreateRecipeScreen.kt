package com.onur.oleai_android.features.createrecipe.ui

import androidx.compose.material3.MaterialTheme
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.onur.oleai_android.core.network.dto.RecipeCategoryResponse
import com.onur.oleai_android.core.network.dto.TagDto
import com.onur.oleai_android.core.ui.utils.IconMapper
import com.onur.oleai_android.features.createrecipe.viewmodel.CreateRecipeViewModel
import com.onur.oleai_android.features.createrecipe.viewmodel.IngredientItem
import com.onur.oleai_android.features.createrecipe.viewmodel.StepItem
import com.onur.oleai_android.features.pantry.ui.IngredientConfigView
import com.onur.oleai_android.core.network.dto.IngredientSearchResultResponse

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateRecipeScreen(
    viewModel: CreateRecipeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    
    var customIngredientToConfig by remember { mutableStateOf<Pair<String, String>?>(null) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onNavigateToHome()
        }
    }

    if (state.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissError() },
            title = { Text("Hata") },
            text = { Text(state.error ?: "") },
            confirmButton = {
                TextButton(onClick = { viewModel.dismissError() }) {
                    Text("Tamam", color = MaterialTheme.colorScheme.primary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditMode) "Tarifi Düzenle" else "Yeni Tarif Oluştur", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.submitRecipe() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(end = 16.dp),
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(16.dp))
                        } else {
                            Icon(Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (state.isEditMode) "Değişiklikleri Kaydet" else "Tarifi Paylaş", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Title & Description
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    val hasTitleError = state.validationErrors.containsKey("title")
                    OutlinedTextField(
                        value = state.title,
                        onValueChange = { viewModel.updateTitle(it) },
                        placeholder = { Text("Tarif Adı", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = if (hasTitleError) Color.Red.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant) },
                        isError = hasTitleError,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = if (hasTitleError) Color.Red else Color.Transparent,
                            errorBorderColor = Color.Red,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent
                        ),
                        textStyle = LocalTextStyle.current.copy(fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onBackground),
                        modifier = Modifier.fillMaxWidth()
                    )

                    val hasDescError = state.validationErrors.containsKey("description")
                    OutlinedTextField(
                        value = state.description,
                        onValueChange = { viewModel.updateDescription(it) },
                        label = { Text("AÇIKLAMA", fontSize = 10.sp, color = if (hasDescError) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold) },
                        placeholder = { Text("Bu yemeğin hikayesini anlatın... (Örn. 'Büyükannemden bana kalan...')") },
                        isError = hasDescError,
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = if (hasDescError) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                            errorBorderColor = Color.Red,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            errorContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        maxLines = 4
                    )
                }
            }

            // Cover Photo
            item {
                val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
                    viewModel.updateMainImage(uri)
                }
                val hasImageError = state.validationErrors.containsKey("mainImage")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .then(
                            if (hasImageError) Modifier.border(2.dp, Color.Red, RoundedCornerShape(16.dp)) else Modifier
                        )
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.mainImageUri != null || state.existingMainImageUrl != null) {
                        AsyncImage(
                            model = state.mainImageUri ?: state.existingMainImageUrl,
                            contentDescription = "Cover Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Outlined.AddPhotoAlternate, contentDescription = null, tint = if (hasImageError) Color.Red else MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.size(32.dp))
                            Text("Kapak Fotoğrafı Yükle", color = if (hasImageError) Color.Red else MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                            Text("PNG, JPG (Maks 10MB)", color = if (hasImageError) Color.Red.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        }
                    }
                }
                if (hasImageError) {
                    Text(text = state.validationErrors["mainImage"] ?: "", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 16.dp, top = 4.dp))
                }
            }

            // Dropdowns & Inputs
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Difficulty
                    Box(modifier = Modifier.weight(1f)) {
                        var expanded by remember { mutableStateOf(false) }
                        val displayDifficulty = when(state.difficulty) {
                            "Easy" -> "Kolay"
                            "Medium" -> "Orta"
                            "Hard" -> "Zor"
                            else -> state.difficulty
                        }
                        OutlinedTextField(
                            value = displayDifficulty,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("ZORLUK DERECESİ", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1) },
                            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.matchParentSize().background(Color.Transparent).clickable { expanded = true })
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                            listOf("Easy" to "Kolay", "Medium" to "Orta", "Hard" to "Zor").forEach { (key, label) ->
                                DropdownMenuItem(
                                    text = { Text(label, color = MaterialTheme.colorScheme.onBackground) },
                                    onClick = { viewModel.updateDifficulty(key); expanded = false }
                                )
                            }
                        }
                    }

                    // Visibility
                    Box(modifier = Modifier.weight(1f)) {
                        var expanded by remember { mutableStateOf(false) }
                        val displayVisibility = when(state.visibility) {
                            "Public" -> "Herkese Açık"
                            "Private" -> "Sadece Ben"
                            else -> state.visibility
                        }
                        OutlinedTextField(
                            value = displayVisibility,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("GÖRÜNÜRLÜK", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1) },
                            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, null) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                            ),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.matchParentSize().background(Color.Transparent).clickable { expanded = true })
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                            listOf("Public" to "Herkese Açık", "Private" to "Sadece Ben").forEach { (key, label) ->
                                DropdownMenuItem(
                                    text = { Text(label, color = MaterialTheme.colorScheme.onBackground) },
                                    onClick = { viewModel.updateVisibility(key); expanded = false }
                                )
                            }
                        }
                    }
                }
            }

            // Prep, Cook Time & Servings
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    val hasServingsError = state.validationErrors.containsKey("servings")
                    OutlinedTextField(
                        value = state.servings,
                        onValueChange = { viewModel.updateServings(it) },
                        label = { Text("PORSİYON", fontSize = 10.sp, color = if (hasServingsError) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1) },
                        isError = hasServingsError,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary, 
                            unfocusedBorderColor = if (hasServingsError) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant, 
                            errorBorderColor = Color.Red,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            errorContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                        ),
                        shape = RoundedCornerShape(12.dp),
                        suffix = { Text("Kişilik", fontSize = 12.sp, color = if (hasServingsError) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant) },
                        singleLine = true
                    )
                    
                    val hasPrepError = state.validationErrors.containsKey("prepTime")
                    OutlinedTextField(
                        value = state.prepTime,
                        onValueChange = { viewModel.updatePrepTime(it) },
                        label = { Text("HAZIRLIK", fontSize = 10.sp, color = if (hasPrepError) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1) },
                        isError = hasPrepError,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary, 
                            unfocusedBorderColor = if (hasPrepError) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant, 
                            errorBorderColor = Color.Red,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            errorContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                        ),
                        shape = RoundedCornerShape(12.dp),
                        suffix = { Text("Dk", fontSize = 12.sp, color = if (hasPrepError) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant) },
                        singleLine = true
                    )
                    
                    val hasCookError = state.validationErrors.containsKey("cookTime")
                    OutlinedTextField(
                        value = state.cookTime,
                        onValueChange = { viewModel.updateCookTime(it) },
                        label = { Text("PİŞİRME", fontSize = 10.sp, color = if (hasCookError) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1) },
                        isError = hasCookError,
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary, 
                            unfocusedBorderColor = if (hasCookError) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant, 
                            errorBorderColor = Color.Red,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            errorContainerColor = MaterialTheme.colorScheme.surface,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                        ),
                        shape = RoundedCornerShape(12.dp),
                        suffix = { Text("Dk", fontSize = 12.sp, color = if (hasCookError) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant) },
                        singleLine = true
                    )
                }
            }





            // Ingredients Area
            item {
                val hasIngredientsError = state.validationErrors.containsKey("ingredients")
                Card(
                    modifier = Modifier.fillMaxWidth().then(
                        if (hasIngredientsError) Modifier.border(2.dp, Color.Red, RoundedCornerShape(16.dp)) else Modifier
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Outlined.Scale, contentDescription = null, tint = if (hasIngredientsError) Color.Red else MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.size(20.dp))
                                Text("Malzemeler", color = if (hasIngredientsError) Color.Red else MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Text("${state.ingredients.size} ADET", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        if (hasIngredientsError) {
                            Text(text = state.validationErrors["ingredients"] ?: "", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Column Header
                        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp, start = 32.dp)) {
                            Text("MİKTAR", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.2f))
                            Text("BİRİM", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.3f))
                            Text("MALZEME", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f))
                        }

                        // Ingredients List
                        state.ingredients.forEach { ingredient ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surface).padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { viewModel.removeIngredient(ingredient.id) }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Filled.Close, contentDescription = "Sil", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                OutlinedTextField(
                                    value = ingredient.amount,
                                    onValueChange = { newValue ->
                                        val filtered = newValue.filter { char -> char.isDigit() || char == '.' || char == ',' }
                                        viewModel.updateIngredient(ingredient.id, ingredient.copy(amount = filtered))
                                    },
                                    modifier = Modifier.weight(0.2f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                                    ),
                                    singleLine = true
                                )
                                
                                // Unit Dropdown (simplified mapping)
                                Box(modifier = Modifier.weight(0.3f)) {
                                    var expanded by remember { mutableStateOf(false) }
                                    val unitLabel = when(ingredient.unitId) {
                                        1 -> "Adet"
                                        2 -> "Kg"
                                        3 -> "Gr"
                                        4 -> "L"
                                        5 -> "Ml"
                                        6 -> "Paket"
                                        7 -> "Demet"
                                        8 -> "Dilim"
                                        9 -> "Tutam"
                                        else -> "Birim"
                                    }
                                    
                                    val canChangeUnit = ingredient.unitFamilyStr != null
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable(enabled = canChangeUnit) { expanded = true }.padding(vertical = 12.dp)) {
                                        Text(unitLabel, color = if (canChangeUnit) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
                                        Icon(Icons.Filled.ArrowDropDown, null, tint = if (canChangeUnit) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                                        val availableUnits = when(ingredient.unitFamilyStr) {
                                            "Weight" -> listOf(2 to "Kg", 3 to "Gr")
                                            "Volume" -> listOf(4 to "L", 5 to "Ml")
                                            "Count" -> listOf(1 to "Adet", 6 to "Paket", 7 to "Demet", 8 to "Dilim", 9 to "Tutam")
                                            else -> listOf(1 to "Adet", 2 to "Kg", 3 to "Gr", 4 to "L", 5 to "Ml", 6 to "Paket", 7 to "Demet", 8 to "Dilim", 9 to "Tutam")
                                        }
                                        availableUnits.forEach { (id, label) ->
                                            DropdownMenuItem(
                                                text = { Text(label, color = MaterialTheme.colorScheme.onBackground) },
                                                onClick = { viewModel.updateIngredient(ingredient.id, ingredient.copy(unitId = id)); expanded = false }
                                            )
                                        }
                                    }
                                }
                                
                                Box(modifier = Modifier.weight(0.5f)) {
                                    var isDropdownExpanded by remember { mutableStateOf(false) }

                                    OutlinedTextField(
                                        value = ingredient.name,
                                        onValueChange = { 
                                            viewModel.searchIngredient(ingredient.id, it)
                                            isDropdownExpanded = true
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        placeholder = { Text("Malzeme ara...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color.Transparent,
                                            unfocusedBorderColor = Color.Transparent,
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                                        ),
                                        singleLine = true
                                    )

                                    if (state.activeSearchIngredientId == ingredient.id && (state.ingredientSearchResults.isNotEmpty() || ingredient.name.length >= 2)) {
                                        DropdownMenu(
                                            expanded = isDropdownExpanded,
                                            onDismissRequest = { isDropdownExpanded = false },
                                            modifier = Modifier.background(MaterialTheme.colorScheme.surface).fillMaxWidth(0.6f).heightIn(max = 250.dp),
                                            properties = PopupProperties(focusable = false)
                                        ) {
                                            state.ingredientSearchResults.forEach { result ->
                                                DropdownMenuItem(
                                                    text = { 
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(
                                                                imageVector = IconMapper.getIconByName(result.categoryIconName ?: "leaf"),
                                                                contentDescription = null,
                                                                tint = MaterialTheme.colorScheme.primary,
                                                                modifier = Modifier.size(24.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(12.dp))
                                                            Text(result.displayName, color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
                                                        }
                                                    },
                                                    onClick = {
                                                        viewModel.selectIngredientResult(ingredient.id, result)
                                                        isDropdownExpanded = false
                                                    }
                                                )
                                            }
                                            
                                            if (ingredient.name.length >= 2) {
                                                DropdownMenuItem(
                                                    text = { 
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(
                                                                imageVector = Icons.Default.AddCircleOutline,
                                                                contentDescription = null,
                                                                tint = Color(0xFF8DC63F),
                                                                modifier = Modifier.size(24.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(12.dp))
                                                            Text(
                                                                text = "\"${ingredient.name}\" Olarak Ekle",
                                                                color = Color(0xFF8DC63F),
                                                                fontSize = 14.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    },
                                                    onClick = {
                                                        customIngredientToConfig = ingredient.id to ingredient.name
                                                        isDropdownExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        TextButton(onClick = { viewModel.addIngredient() }) {
                            Icon(Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Malzeme Ekle", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Steps Area
            item {
                val hasStepsError = state.validationErrors.containsKey("steps")
                Card(
                    modifier = Modifier.fillMaxWidth().then(
                        if (hasStepsError) Modifier.border(2.dp, Color.Red, RoundedCornerShape(16.dp)) else Modifier
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Outlined.Book, contentDescription = null, tint = if (hasStepsError) Color.Red else MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.size(20.dp))
                                Text("Hazırlanışı", color = if (hasStepsError) Color.Red else MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                            Text("${state.steps.size} ADIM", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        if (hasStepsError) {
                            Text(text = state.validationErrors["steps"] ?: "", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Steps List
                        state.steps.forEachIndexed { index, step ->
                            val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
                                viewModel.updateStep(step.id, step.copy(imageUri = uri))
                            }
                            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), verticalAlignment = Alignment.Top) {
                                // Number Badge
                                Box(
                                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text((index + 1).toString(), color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = step.instruction,
                                        onValueChange = { viewModel.updateStep(step.id, step.copy(instruction = it)) },
                                        placeholder = { Text("Bu adımda ne yapılması gerektiğini açıklayın...") },
                                        modifier = Modifier.fillMaxWidth().height(100.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = Color.Transparent,
                                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        maxLines = 4
                                    )
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                        TextButton(onClick = { launcher.launch("image/*") }) {
                                            Icon(Icons.Outlined.AddPhotoAlternate, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            val hasImage = step.imageUri != null || step.existingImageUrl != null
                                            Text(if (hasImage) "Fotoğraf Seçildi" else "Fotoğraf Ekle (İsteğe Bağlı)", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                        }
                                        
                                        if (state.steps.size > 1) {
                                            IconButton(onClick = { viewModel.removeStep(step.id) }) {
                                                Icon(Icons.Outlined.Delete, contentDescription = "Adımı Sil", tint = Color.Red.copy(alpha = 0.5f))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        TextButton(onClick = { viewModel.addStep() }, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surface)) {
                            Icon(Icons.Filled.Add, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Adım Ekle", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            // Categories
            item {
                val hasCatError = state.validationErrors.containsKey("categories")
                Column(modifier = Modifier.fillMaxWidth().then(if (hasCatError) Modifier.border(2.dp, Color.Red, RoundedCornerShape(12.dp)).padding(8.dp) else Modifier)) {
                    Text("KATEGORİLER", fontSize = 10.sp, color = if (hasCatError) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    if (hasCatError) {
                        Text(text = state.validationErrors["categories"] ?: "", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.categories.forEach { category ->
                            val isSelected = state.selectedCategoryIds.contains(category.id)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
                                    .clickable { viewModel.toggleCategory(category.id) }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(category.name, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            // Tags
            item {
                val hasTagError = state.validationErrors.containsKey("tags")
                Column(modifier = Modifier.fillMaxWidth().then(if (hasTagError) Modifier.border(2.dp, Color.Red, RoundedCornerShape(12.dp)).padding(8.dp) else Modifier)) {
                    Text("ETİKETLER", fontSize = 10.sp, color = if (hasTagError) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    if (hasTagError) {
                        Text(text = state.validationErrors["tags"] ?: "", color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.tags.forEach { tag ->
                            val isSelected = state.selectedTagIds.contains(tag.id)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface)
                                    .clickable { viewModel.toggleTag(tag.id) }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(tag.name, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (customIngredientToConfig != null) {
        val ingredientId = customIngredientToConfig!!.first
        val customName = customIngredientToConfig!!.second
        val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        
        ModalBottomSheet(
            onDismissRequest = { customIngredientToConfig = null },
            sheetState = bottomSheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            val fakeIngredientResponse = IngredientSearchResultResponse(
                masterIngredientId = "", // indicates custom
                displayName = customName,
                categoryName = "Özel Malzeme",
                categoryIconName = "leaf",
                unitFamily = "all"
            )
            
            IngredientConfigView(
                ingredient = fakeIngredientResponse,
                ingredientCategories = state.ingredientCategories,
                onBack = { customIngredientToConfig = null },
                onAddToCart = { quantity, unitId, categoryId, expirationDate, estimatedShelfLifeInDays ->
                    viewModel.addCustomIngredientToRecipe(
                        ingredientId = ingredientId,
                        customName = customName,
                        quantity = quantity,
                        unitId = unitId,
                        categoryId = categoryId,
                        expirationDate = expirationDate,
                        estimatedShelfLifeInDays = estimatedShelfLifeInDays
                    )
                    customIngredientToConfig = null
                }
            )
        }
    }
}
