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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.onur.oleai_android.core.network.dto.AddShoppingListItemRequest
import com.onur.oleai_android.features.recipe_detail.viewmodel.MissingIngredientItem

data class DepletedIngredientState(
    val ingredientId: String,
    val name: String,
    var isSelected: Boolean = true,
    var editableQuantity: String,
    var selectedUnitId: Int = 1, // Defaulting to 1, should probably map from string to unit id, but we keep it simple here
    var displayUnit: String
)

@Composable
fun DepletedIngredientsDialog(
    depletedIngredients: List<MissingIngredientItem>,
    onDismiss: () -> Unit,
    onAddToList: (List<AddShoppingListItemRequest>) -> Unit
) {
    val stateList = remember {
        depletedIngredients.map {
            DepletedIngredientState(
                ingredientId = it.ingredientId,
                name = it.name,
                editableQuantity = if (it.quantity % 1.0 == 0.0) it.quantity.toInt().toString() else it.quantity.toString(),
                displayUnit = it.unit,
                selectedUnitId = it.unitId
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
                                .background(Color(0xFF0D3D2B), shape = RoundedCornerShape(20.dp)), // Dark green circle
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Biten Malzemeler",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Alışveriş listenize ekleyelim mi?",
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
                    text = "Bu tarifi pişirdikten sonra dolabınızdaki bazı malzemelerin stokları sıfırlandı. Miktarlarını dilediğiniz gibi düzenleyerek alışveriş listenize ekleyebilirsiniz.",
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
                        DepletedIngredientItemRow(
                            state = state,
                            onToggleSelect = {
                                stateList[index] = stateList[index].copy(isSelected = !state.isSelected)
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
                        Text("Atla", color = MaterialTheme.colorScheme.onBackground)
                    }

                    Button(
                        onClick = {
                            val itemsToAdd = stateList.filter { it.isSelected }.map {
                                AddShoppingListItemRequest(
                                    ingredientId = it.ingredientId,
                                    quantity = it.editableQuantity.toDoubleOrNull() ?: 1.0,
                                    unit = it.selectedUnitId
                                )
                            }
                            if (itemsToAdd.isNotEmpty()) {
                                onAddToList(itemsToAdd)
                            } else {
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90B551)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Listeye Ekle", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DepletedIngredientItemRow(
    state: DepletedIngredientState,
    onToggleSelect: () -> Unit,
    onQuantityChanged: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        if (state.isSelected) Color(0xFF90B551) else Color(0xFF222222),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .border(
                        1.dp,
                        if (state.isSelected) Color(0xFF90B551) else Color(0xFF444444),
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onToggleSelect() },
                contentAlignment = Alignment.Center
            ) {
                if (state.isSelected) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = state.name,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (state.isSelected) {
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
                if (state.displayUnit.isNotBlank()) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = state.displayUnit, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        } else {
            Text(text = "${state.editableQuantity} ${state.displayUnit}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        }
    }
}
