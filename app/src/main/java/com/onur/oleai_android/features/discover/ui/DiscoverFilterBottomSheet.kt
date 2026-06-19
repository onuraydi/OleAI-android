package com.onur.oleai_android.features.discover.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.onur.oleai_android.features.discover.viewmodel.DiscoverViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DiscoverFilterBottomSheet(
    viewModel: DiscoverViewModel,
    onDismiss: () -> Unit
) {
    val filterState by viewModel.filterState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var selectedRating by remember { mutableStateOf(filterState.minRating) }
    var selectedDifficulty by remember { mutableStateOf(filterState.difficulty) }
    var selectedTime by remember { mutableStateOf(filterState.maxTotalTime) }
    var selectedTagId by remember { mutableStateOf(filterState.tagId) }

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

            // Tags / Diets
            if (uiState.tags.isNotEmpty()) {
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
                        uiState.tags.forEach { tag ->
                            FilterChipOption(
                                label = tag.name,
                                isSelected = selectedTagId == tag.id,
                                onClick = { selectedTagId = tag.id }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextButton(
                    onClick = {
                        selectedRating = null
                        selectedDifficulty = null
                        selectedTime = null
                        selectedTagId = null
                        viewModel.clearFilters()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Temizle", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                Button(
                    onClick = {
                        viewModel.applyFilters(
                            maxTotalTime = selectedTime,
                            minRating = selectedRating,
                            difficulty = selectedDifficulty,
                            tagId = selectedTagId,
                            sortBy = filterState.sortBy
                        )
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Filtreleri Uygula")
                }
            }
        }
    }
}

@Composable
fun FilterSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
fun FilterChipOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                    else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
        )
    }
}
