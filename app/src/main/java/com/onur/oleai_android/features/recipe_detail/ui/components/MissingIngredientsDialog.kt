package com.onur.oleai_android.features.recipe_detail.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.onur.oleai_android.R
import com.onur.oleai_android.core.network.dto.AddShoppingListItemRequest
import com.onur.oleai_android.core.network.dto.AddUserIngredientRequest
import com.onur.oleai_android.features.recipe_detail.viewmodel.MissingIngredientItem

enum class MissingIngredientAction {
    IGNORE, ADD_TO_LIST, ADD_TO_PANTRY
}

data class MissingIngredientState(
    val ingredientId: String,
    val name: String,
    val originalUnit: String,
    val unitId: Int,
    var selectedAction: MissingIngredientAction = MissingIngredientAction.IGNORE,
    var editableQuantity: String
)

@Composable
fun MissingIngredientsDialog(
    missingIngredients: List<MissingIngredientItem>,
    onDismiss: () -> Unit,
    onContinue: (ignoredIds: List<String>) -> Unit,
    onAddToList: (List<AddShoppingListItemRequest>) -> Unit,
    onAddToPantry: (List<AddUserIngredientRequest>) -> Unit
) {
    val stateList = remember {
        missingIngredients.map {
            MissingIngredientState(
                ingredientId = it.ingredientId,
                name = it.name,
                originalUnit = it.unit,
                unitId = it.unitId,
                editableQuantity = if (it.quantity % 1.0 == 0.0) it.quantity.toInt().toString() else it.quantity.toString()
            )
        }.toMutableStateList()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6000000))
                .padding(16.dp)
                .imePadding(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF191A14), shape = RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0xFF2C2C2C), shape = RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF332B00), shape = RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFE5A800),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Eksik Malzemeler",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Dolabınızda bu ürünler bulunmuyor",
                                color = Color(0xFFAAAAAA),
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color(0xFF2C2C2C), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Aşağıdaki malzemeler dijital dolabınızda bulunamadı. Lütfen eksik ürünler için bir aksiyon seçin:",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                ) {
                    items(stateList.size) { index ->
                        val state = stateList[index]
                        MissingIngredientItemRow(
                            state = state,
                            onActionSelected = { action ->
                                stateList[index] = stateList[index].copy(selectedAction = action)
                            },
                            onQuantityChanged = { newQuantity ->
                                stateList[index] = stateList[index].copy(editableQuantity = newQuantity)
                            }
                        )
                        if (index < stateList.size - 1) {
                            Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = Color(0xFF2C2C2C), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        border = border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Text("İptal", color = MaterialTheme.colorScheme.onBackground)
                    }

                    Button(
                        onClick = {
                            val ignoredIds = stateList.filter { 
                                it.selectedAction == MissingIngredientAction.IGNORE || 
                                it.selectedAction == MissingIngredientAction.ADD_TO_LIST 
                            }.map { it.ingredientId }
                            
                            val listItems = stateList.filter { it.selectedAction == MissingIngredientAction.ADD_TO_LIST }.map {
                                AddShoppingListItemRequest(
                                    ingredientId = it.ingredientId,
                                    quantity = it.editableQuantity.toDoubleOrNull() ?: 1.0,
                                    unit = it.unitId
                                )
                            }

                            val pantryItems = stateList.filter { it.selectedAction == MissingIngredientAction.ADD_TO_PANTRY }.map {
                                AddUserIngredientRequest(
                                    userId = "", // Filled by viewModel or interceptor
                                    ingredientId = it.ingredientId,
                                    quantity = it.editableQuantity.toDoubleOrNull() ?: 1.0,
                                    unit = it.unitId
                                )
                            }

                            if (listItems.isNotEmpty()) onAddToList(listItems)
                            if (pantryItems.isNotEmpty()) onAddToPantry(pantryItems)
                            
                            onContinue(ignoredIds)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90B551)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.RestaurantMenu, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Devam Et", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MissingIngredientItemRow(
    state: MissingIngredientState,
    onActionSelected: (MissingIngredientAction) -> Unit,
    onQuantityChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.name, 
                color = MaterialTheme.colorScheme.onBackground, 
                fontWeight = FontWeight.SemiBold, 
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Quantity editor right next to title
            if (state.selectedAction == MissingIngredientAction.IGNORE) {
                Text(text = "${state.editableQuantity} ${state.originalUnit}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BasicTextField(
                        value = state.editableQuantity,
                        onValueChange = onQuantityChanged,
                        textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        cursorBrush = SolidColor(Color(0xFF90B551)),
                        modifier = Modifier
                            .width(55.dp)
                            .background(Color(0xFF222222), RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            .padding(vertical = 6.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = state.originalUnit, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Modern Segmented Control for Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(Color(0xFF161616), RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SegmentedButton(
                text = "Yoksay",
                isSelected = state.selectedAction == MissingIngredientAction.IGNORE,
                onClick = { onActionSelected(MissingIngredientAction.IGNORE) },
                selectedColor = Color(0xFFE5A800),
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            SegmentedButton(
                text = "Alışveriş\nListesine Ekle",
                isSelected = state.selectedAction == MissingIngredientAction.ADD_TO_LIST,
                onClick = { onActionSelected(MissingIngredientAction.ADD_TO_LIST) },
                selectedColor = Color(0xFF3B82F6),
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            SegmentedButton(
                text = "Dolaba\nEkle",
                isSelected = state.selectedAction == MissingIngredientAction.ADD_TO_PANTRY,
                onClick = { onActionSelected(MissingIngredientAction.ADD_TO_PANTRY) },
                selectedColor = Color(0xFF10B981),
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }
    }
}

@Composable
fun SegmentedButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    selectedColor: Color,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) selectedColor.copy(alpha = 0.15f) else Color.Transparent
    val contentColor = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (isSelected) selectedColor.copy(alpha = 0.4f) else Color.Transparent

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text, 
            color = contentColor, 
            fontSize = 11.sp, 
            lineHeight = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 2,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
    }
}

fun border(width: androidx.compose.ui.unit.Dp, color: Color, shape: androidx.compose.ui.graphics.Shape): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(width, color)
}
