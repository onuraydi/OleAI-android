package com.onur.oleai_android.features.profile.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onur.oleai_android.features.profile.viewmodel.MyRecipesViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun MyRecipesFilterBottomSheet(
    viewModel: MyRecipesViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    var selectedRating by remember { mutableStateOf(state.minRating) }
    var selectedDifficulty by remember { mutableStateOf(state.difficulty) }
    var selectedTime by remember { mutableStateOf(state.maxTotalTime) }
    var selectedVisibility by remember { mutableStateOf(state.visibility) }
    // Note: Assuming tagId is mapped here; leaving selectedTagId null for now
    var selectedTagId by remember { mutableStateOf(state.tagId) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filtreler",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Kapat")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Rating
            FilterSection(title = "Değerlendirme Puanı") {
                val ratingOptions = listOf(
                    null to "Tümü",
                    4.0 to "4+ Yıldız",
                    3.0 to "3+ Yıldız",
                    2.0 to "2+ Yıldız"
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ratingOptions.forEach { (value, label) ->
                        FilterChipOption(
                            label = label,
                            isSelected = selectedRating == value,
                            onClick = { selectedRating = value }
                        )
                    }
                }
            }

            // Difficulty
            FilterSection(title = "Zorluk") {
                val difficultyOptions = listOf(
                    null to "Tümü",
                    1 to "Kolay",
                    2 to "Orta",
                    3 to "Zor"
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    difficultyOptions.forEach { (value, label) ->
                        FilterChipOption(
                            label = label,
                            isSelected = selectedDifficulty == value,
                            onClick = { selectedDifficulty = value }
                        )
                    }
                }
            }

            // Time
            FilterSection(title = "Hazırlama Süresi") {
                val timeOptions = listOf(
                    null to "Tümü",
                    15 to "< 15 dk",
                    30 to "< 30 dk",
                    60 to "< 60 dk"
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    timeOptions.forEach { (value, label) ->
                        FilterChipOption(
                            label = label,
                            isSelected = selectedTime == value,
                            onClick = { selectedTime = value }
                        )
                    }
                }
            }
            
            // Visibility
            FilterSection(title = "Görünürlük") {
                val visibilityOptions = listOf(
                    null to "Tümü",
                    "Private" to "Sadece Ben",
                    "Public" to "Herkese Açık"
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    visibilityOptions.forEach { (value, label) ->
                        FilterChipOption(
                            label = label,
                            isSelected = selectedVisibility == value,
                            onClick = { selectedVisibility = value }
                        )
                    }
                }
            }

            // Tags / Diets - Just dummy visual for now as no tag list exists
            FilterSection(title = "Diyet / Etiket") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipOption(
                        label = "Tümü",
                        isSelected = selectedTagId == null,
                        onClick = { selectedTagId = null }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        selectedRating = null
                        selectedDifficulty = null
                        selectedTime = null
                        selectedVisibility = null
                        selectedTagId = null
                        
                        viewModel.applyFilters(null, null, null, null, null)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
                ) {
                    Text("Temizle", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        viewModel.applyFilters(
                            minRating = selectedRating,
                            difficulty = selectedDifficulty,
                            maxTotalTime = selectedTime,
                            visibility = selectedVisibility,
                            tagId = selectedTagId
                        )
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8BC34A),
                        contentColor = MaterialTheme.colorScheme.onBackground
                    )
                ) {
                    Text("Uygula", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FilterSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        content()
    }
}

@Composable
private fun FilterChipOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color(0xFF2E3D24) else MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) Color(0xFF8BC34A) else MaterialTheme.colorScheme.outlineVariant,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}
