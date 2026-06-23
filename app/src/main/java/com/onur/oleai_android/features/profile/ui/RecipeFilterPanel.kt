package com.onur.oleai_android.features.profile.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecipeFilterPanel(
    onApply: () -> Unit,
    onClear: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Değerlendirme Puanı
                FilterColumn(
                    title = "Değerlendirme Puanı",
                    options = listOf("Tümü", "4+ Yıldız", "3+ Yıldız", "2+ Yıldız"),
                    selectedOption = "Tümü"
                )
                
                // Zorluk
                FilterColumn(
                    title = "Zorluk",
                    options = listOf("Tümü", "Kolay", "Orta", "Zor"),
                    selectedOption = "Tümü"
                )
                
                // Hazırlama Süresi
                FilterColumn(
                    title = "Hazırlama Süresi",
                    options = listOf("Tümü", "< 15 dk", "< 30 dk", "< 60 dk"),
                    selectedOption = "Tümü"
                )
                
                // Görünürlük
                FilterColumn(
                    title = "Görünürlük",
                    options = listOf("Tümü", "Sadece Ben", "Herkese Açık"),
                    selectedOption = "Tümü"
                )
                
                // Diyet / Etiket
                Column {
                    Text("Diyet / Etiket", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .height(150.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val tags = listOf("Tümü", "Acılı", "Airfryer", "Anne Yemeği", "Bağışıklık Güçlendirici", "Vegan", "Vejetaryen")
                        tags.forEach { tag ->
                            FilterChip(text = tag, isSelected = tag == "Tümü", onClick = {})
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Bottom Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Temizle",
                    color = MaterialTheme.colorScheme.outlineVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { onClear() }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
                
                Button(
                    onClick = onApply,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Filtreleri Uygula", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterColumn(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit = {}
) {
    Column {
        Text(title, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(12.dp))
        FlowRow(
            modifier = Modifier.width(140.dp), // Force wrapping
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    text = option,
                    isSelected = option == selectedOption,
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}

@Composable
private fun FilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF2A3A1F) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}
