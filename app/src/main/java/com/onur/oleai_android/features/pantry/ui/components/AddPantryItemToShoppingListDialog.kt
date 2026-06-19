package com.onur.oleai_android.features.pantry.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.onur.oleai_android.core.network.dto.MyPantryItemResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPantryItemToShoppingListDialog(
    item: MyPantryItemResponse,
    onDismiss: () -> Unit,
    onAdd: (Double, Int) -> Unit
) {
    var quantity by remember { mutableStateOf(1.0) }
    
    // Reverse map from string to ID or default to 1 (Adet)
    val unitId = remember(item.unit) {
        when (item.unit.lowercase()) {
            "kilogram", "kg" -> 2
            "gram", "g" -> 3
            "litre", "l" -> 4
            "mililitre", "ml" -> 5
            "paket" -> 6
            "demet" -> 7
            "dilim" -> 8
            "tutam" -> 9
            else -> 1 // Adet
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF141911), // Dark theme background matching screenshot
            contentColor = Color.White,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.name} Listeye Eklenecek",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Decrease Button
                    Button(
                        onClick = { if (quantity > 1) quantity -= 1 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A3625)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Text("-", fontSize = 24.sp, color = Color.White)
                    }
                    
                    Spacer(modifier = Modifier.width(24.dp))
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        var quantityStr by remember(quantity) { mutableStateOf(if (quantity == quantity.toInt().toDouble()) quantity.toInt().toString() else quantity.toString()) }
                        androidx.compose.foundation.text.BasicTextField(
                            value = quantityStr,
                            onValueChange = { newValue ->
                                if (newValue.isEmpty()) {
                                    quantityStr = ""
                                } else if (newValue.toDoubleOrNull() != null) {
                                    quantityStr = newValue
                                    quantity = newValue.toDouble()
                                }
                            },
                            textStyle = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            ),
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(Color.White),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            ),
                            modifier = Modifier.width(IntrinsicSize.Min)
                        )
                        Text(
                            text = item.unit.ifEmpty { "Adet" },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = Color.LightGray
                            )
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(24.dp))
                    
                    // Increase Button
                    Button(
                        onClick = { quantity += 1 },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A3625)),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Text("+", fontSize = 24.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2A3625)),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "İptal",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Button(
                        onClick = { onAdd(quantity, unitId) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)), // Blue button for add to list
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Ekle",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
