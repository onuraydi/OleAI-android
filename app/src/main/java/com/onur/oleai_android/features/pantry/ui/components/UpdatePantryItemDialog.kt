package com.onur.oleai_android.features.pantry.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdatePantryItemDialog(
    item: MyPantryItemResponse,
    onDismiss: () -> Unit,
    onUpdate: (Double, Int, String?) -> Unit
) {
    var quantity by remember { mutableStateOf(item.quantity) }
    var expirationDate by remember { 
        var dateStr = item.expirationDate ?: ""
        if (dateStr.contains("T")) {
            dateStr = dateStr.substringBefore("T")
        }
        val parts = dateStr.split("-")
        if (parts.size == 3 && parts[0].length == 4) {
            // YYYY-MM-DD -> DDMMYYYY
            dateStr = "${parts[2]}${parts[1]}${parts[0]}"
        }
        // Remove any existing dots if it was DD.MM.YYYY
        dateStr = dateStr.replace(".", "")
        mutableStateOf(dateStr) 
    }
    
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Get start of today in UTC
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.timeInMillis = System.currentTimeMillis()
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                return utcTimeMillis >= calendar.timeInMillis
            }
        }
    )

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

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val sdf = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
                        expirationDate = sdf.format(Date(it))
                    }
                    showDatePicker = false
                }) {
                    Text("Tamam")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("İptal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
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
                        text = "${item.name} Miktarını Güncelle",
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

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Son Kullanma Tarihi (Gün.Ay.Yıl)",
                        style = MaterialTheme.typography.labelMedium.copy(color = Color.LightGray),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = expirationDate,
                        onValueChange = { newValue ->
                            val digits = newValue.filter { it.isDigit() }.take(8)
                            var isValid = true
                            
                            // Day validation
                            if (digits.length >= 1 && digits[0].digitToInt() > 3) isValid = false
                            if (digits.length >= 2) {
                                val day = digits.substring(0, 2).toInt()
                                if (day == 0 || day > 31) isValid = false
                            }
                            // Month validation
                            if (digits.length >= 3 && digits[2].digitToInt() > 1) isValid = false
                            if (digits.length >= 4) {
                                val month = digits.substring(2, 4).toInt()
                                if (month == 0 || month > 12) isValid = false
                            }
                            // Year validation (optional: force starting with 2 for 2000s)
                            if (digits.length >= 5 && digits[4].digitToInt() != 2) isValid = false

                            if (isValid) {
                                expirationDate = digits
                            }
                        },
                        readOnly = false,
                        visualTransformation = com.onur.oleai_android.features.onboarding.ui.DateVisualTransformation(),
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                        ),
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = "Tarih Seç",
                                    tint = Color.LightGray
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFF1E2619),
                            focusedContainerColor = Color(0xFF1E2619),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedTextColor = Color.White,
                            focusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
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
                        onClick = {
                            // If string is empty, null. Else the date string.
                            val finalDate = expirationDate.takeIf { it.isNotBlank() && it.length == 8 }
                            var backendDateStr: String? = null
                            if (finalDate != null) {
                                val day = finalDate.substring(0, 2)
                                val month = finalDate.substring(2, 4)
                                val year = finalDate.substring(4, 8)
                                // Convert DDMMYYYY to YYYY-MM-DD for .NET DateTime parsing
                                backendDateStr = "$year-$month-$day"
                            } else if (expirationDate.isBlank()) {
                                backendDateStr = null
                            } else {
                                // Invalid length, ignore update or maybe we should just send null
                                backendDateStr = null
                            }
                            onUpdate(quantity, unitId, backendDateStr)
                        },
                        enabled = expirationDate.isEmpty() || expirationDate.length == 8,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4A5D23), // Greenish button
                            disabledContainerColor = Color(0xFF2A3625) // Darker green when disabled
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "Kaydet",
                            color = if (expirationDate.isEmpty() || expirationDate.length == 8) Color.White else Color.Gray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
