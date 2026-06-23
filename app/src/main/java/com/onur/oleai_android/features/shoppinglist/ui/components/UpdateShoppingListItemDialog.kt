package com.onur.oleai_android.features.shoppinglist.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.onur.oleai_android.core.network.dto.MyShoppingListItemResponse

@Composable
fun UpdateShoppingListItemDialog(
    item: MyShoppingListItemResponse,
    onDismiss: () -> Unit,
    onUpdate: (Double, Int) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var quantity by remember { mutableStateOf(item.quantity) }

    val unitId = remember(item.unit) {
        when (item.unit.lowercase()) {
            "adet" -> 1
            "dilim" -> 2
            "gram", "g" -> 3
            "kilogram", "kg" -> 4
            "paket" -> 5
            "mililitre", "ml" -> 6
            "litre", "l" -> 7
            "bardak" -> 8
            "kutu" -> 9
            "tatlı k." -> 10
            "çay k." -> 11
            "yemek k." -> 12
            "tutam" -> 13
            "diş" -> 14
            "kavanoz" -> 15
            "şişe" -> 16
            "bağ" -> 17
            "dal" -> 18
            "diğer" -> 99
            else -> 1
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Listedeki Miktarı Güncelle",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.ingredientName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = { if (quantity > 1.0) quantity -= 1.0 else if (quantity > 0.0) quantity = 0.0 },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Text("-", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        BasicTextField(
                            value = if (quantity % 1 == 0.0) quantity.toInt().toString() else quantity.toString(),
                            onValueChange = { newValue ->
                                val cleanString = newValue.replace(",", ".")
                                if (cleanString.isEmpty()) {
                                    quantity = 0.0
                                } else {
                                    val parsed = cleanString.toDoubleOrNull()
                                    if (parsed != null && parsed >= 0.0) {
                                        quantity = parsed
                                    }
                                }
                            },
                            textStyle = MaterialTheme.typography.displaySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            ),
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.onBackground),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                            ),
                            modifier = Modifier.width(IntrinsicSize.Min)
                        )
                        Text(
                            text = item.unit.ifEmpty { "Adet" },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    Button(
                        onClick = { quantity += 1.0 },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Text("+", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                if (onDelete != null) {
                    TextButton(
                        onClick = onDelete,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Listeden Çıkar", color = Color(0xFFEF4444))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("İptal", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onUpdate(quantity, unitId) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Kaydet", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
