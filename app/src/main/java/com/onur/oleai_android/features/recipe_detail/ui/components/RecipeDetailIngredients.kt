package com.onur.oleai_android.features.recipe_detail.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onur.oleai_android.core.network.dto.RecipeIngredientResponse

@Composable
fun RecipeDetailIngredients(
    ingredients: List<RecipeIngredientResponse>,
    servings: Int,
    baseServings: Int,
    isMetric: Boolean,
    onServingsChange: (Int) -> Unit,
    onMetricToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Malzemeler",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Metric/Imperial Toggle
            Row(
                modifier = Modifier
                    .background(Color(0xFF121212), RoundedCornerShape(12.dp))
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                        .background(if (isMetric) Color(0xFF2A2A2A) else Color.Transparent)
                        .clickable { if (!isMetric) onMetricToggle() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "Metrik", color = if (isMetric) Color.White else Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                        .background(if (!isMetric) Color(0xFF2A2A2A) else Color.Transparent)
                        .clickable { if (isMetric) onMetricToggle() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "İngiliz", color = if (!isMetric) Color.White else Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Servings Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Porsiyon",
                color = Color.LightGray,
                fontSize = 14.sp
            )
            
            // Servings Control
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFF121212), RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "Decrease",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onServingsChange(servings - 1) }
                )
                Text(
                    text = servings.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Increase",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onServingsChange(servings + 1) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ingredients List
        val checkedStates = remember { mutableStateMapOf<String, Boolean>() }

        ingredients.forEachIndexed { index, ingredient ->
            val ingredientKey = ingredient.id ?: index.toString()
            val isChecked = checkedStates[ingredientKey] == true

            val ratio = if (baseServings > 0) servings.toFloat() / baseServings.toFloat() else 1f
            // Using quantity or amount
            val amountRaw = ingredient.quantity ?: ingredient.amount ?: 0.0
            val calculatedAmount = amountRaw * ratio
            
            val unitStr = ingredient.unit?.takeIf { it.isNotBlank() && it.lowercase() != "null" } 
                ?: mapUnitIdToText(ingredient.unitId ?: ingredient.ingredient?.unitId)
            
            val formattedAmount = if (calculatedAmount % 1.0 == 0.0) {
                if (calculatedAmount.toInt() == 0 && unitStr.isEmpty()) "" else calculatedAmount.toInt().toString()
            } else {
                String.format("%.1f", calculatedAmount)
            }
            
            val amountText = if (unitStr.isEmpty()) formattedAmount else "$formattedAmount $unitStr".trim()
            
            val textDecoration = if (isChecked) TextDecoration.LineThrough else null
            val textColor = if (isChecked) Color.DarkGray else Color.White
            val nameColor = if (isChecked) Color.DarkGray else Color.LightGray

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        checkedStates[ingredientKey] = !isChecked
                    }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circle bullet / Checkbox
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(if (isChecked) Color(0xFF4CAF50) else Color.Transparent)
                        .border(
                            width = 2.dp,
                            color = if (isChecked) Color(0xFF4CAF50) else Color(0xFF2A2A2A),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isChecked) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Checked",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = amountText,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textDecoration = textDecoration
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = ingredient.ingredientName ?: "Bilinmeyen Malzeme",
                    color = nameColor,
                    fontSize = 14.sp,
                    textDecoration = textDecoration
                )
            }
        }
    }
}

fun mapUnitIdToText(unitId: Int?): String {
    return when (unitId) {
        1 -> "Adet"
        2 -> "kg"
        3 -> "g"
        4 -> "Litre"
        5 -> "ml"
        6 -> "Paket"
        7 -> "Demet"
        8 -> "Dilim"
        9 -> "Tutam"
        99 -> "Diğer"
        else -> ""
    }
}
