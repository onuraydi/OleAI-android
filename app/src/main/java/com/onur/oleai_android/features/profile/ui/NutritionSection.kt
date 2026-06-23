package com.onur.oleai_android.features.profile.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.NoMeals
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.hilt.navigation.compose.hiltViewModel
import com.onur.oleai_android.core.network.dto.AllergyRestrictionListResponse
import com.onur.oleai_android.core.network.dto.DietaryPreferenceListResponse
import com.onur.oleai_android.core.network.dto.DislikedIngredientItemResponse
import com.onur.oleai_android.features.profile.viewmodel.NutritionEvent
import com.onur.oleai_android.features.profile.viewmodel.NutritionViewModel

import com.onur.oleai_android.core.network.dto.UserProfileResponse
import com.onur.oleai_android.core.ui.utils.IconMapper
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NutritionSection(
    userProfile: UserProfileResponse,
    modifier: Modifier = Modifier,
    viewModel: NutritionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(userProfile) {
        viewModel.initData(userProfile)
    }

    // Show Toast on error or success
    val context = LocalContext.current
    LaunchedEffect(state.error, state.successMessage) {
        state.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onEvent(NutritionEvent.DismissMessage)
        }
        state.successMessage?.let {
            Toast.makeText(context, "Başarıyla kaydedildi!", Toast.LENGTH_SHORT).show()
            viewModel.onEvent(NutritionEvent.DismissMessage)
        }
    }

    if (state.isLoading) {
        Box(modifier = modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dietary Preferences
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Outlined.Eco, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text("Beslenme Tercihleri", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("Beslenme tarzını seç. Seçimlerin tarif önerilerini kişiselleştirir.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.dietaryPreferences.forEach { pref ->
                                val isSelected = state.selectedDietaryPreferenceIds.contains(pref.id)
                                SelectionChip(
                                    text = pref.name,
                                    isSelected = isSelected,
                                    onClick = { viewModel.onEvent(NutritionEvent.ToggleDietaryPreference(pref.id)) }
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.onEvent(NutritionEvent.SaveDietaryPreferences) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.align(Alignment.End),
                            enabled = !state.isDietaryPreferencesSaving
                        ) {
                            if (state.isDietaryPreferencesSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Tercihleri Kaydet", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            // Allergies
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Outlined.WarningAmber, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text("Alerji ve Gıda Kısıtlamaları", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("Alerjilerini ve gıda kısıtlamalarını işaretle. Bu bilgiler tariflerden otomatik olarak filtrelenir.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.allergies.forEach { allergy ->
                                val isSelected = state.selectedAllergyIds.contains(allergy.id)
                                SelectionChip(
                                    text = allergy.name,
                                    isSelected = isSelected,
                                    onClick = { viewModel.onEvent(NutritionEvent.ToggleAllergy(allergy.id)) }
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.onEvent(NutritionEvent.SaveAllergies) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.align(Alignment.End),
                            enabled = !state.isAllergiesSaving
                        ) {
                            if (state.isAllergiesSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Alerjileri Kaydet", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            // Disliked Ingredients
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Outlined.ThumbDown, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text("Sevilmeyen Malzemeler", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Text("Hoşlanmadığın malzemeleri seç. Yapay zeka tarif önerirken bu malzemeleri kullanmamaya özen gösterir.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)

                        // Selected Chips
                        if (state.selectedDislikedIngredients.isNotEmpty()) {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                state.selectedDislikedIngredients.forEach { item ->
                                    DislikedIngredientChip(
                                        text = item.name,
                                        onRemove = { viewModel.onEvent(NutritionEvent.RemoveDislikedIngredient(item.id)) }
                                    )
                                }
                            }
                        }

                        // Search Bar
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedTextField(
                                value = state.searchQuery,
                                onValueChange = { 
                                    viewModel.onEvent(NutritionEvent.SearchQueryChanged(it))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                placeholder = { Text("Malzeme ara... (örn: Soğan)", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                                trailingIcon = {
                                    if (state.isSearching) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.primary, strokeWidth = 2.dp)
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color(0xFF2E332A),
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            if (state.searchResults.isNotEmpty()) {
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        state.searchResults.forEach { result ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        viewModel.onEvent(NutritionEvent.AddDislikedIngredient(result))
                                                        viewModel.onEvent(NutritionEvent.SearchQueryChanged("")) // Temizle
                                                    }
                                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                val imageVector = remember(result.categoryIconName) {
                                                    if (!result.categoryIconName.isNullOrEmpty()) {
                                                        try {
                                                            IconMapper.getIconByName(result.categoryIconName)
                                                        } catch (e: Exception) {
                                                            null
                                                        }
                                                    } else null
                                                }
                                                
                                                Icon(
                                                    imageVector = imageVector ?: Icons.Outlined.Eco,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                
                                                Column {
                                                    Text(result.displayName, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                    if (!result.categoryName.isNullOrEmpty()) {
                                                        Text(result.categoryName, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Button(
                            onClick = { viewModel.onEvent(NutritionEvent.SaveDislikedIngredients) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.align(Alignment.End),
                            enabled = !state.isDislikedIngredientsSaving
                        ) {
                            if (state.isDislikedIngredientsSaving) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Malzemeleri Kaydet", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
        }
    }
}

@Composable
fun SelectionChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground

    Row(
        modifier = Modifier
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        if (isSelected) {
            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun DislikedIngredientChip(
    text: String,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .border(1.dp, Color(0xFF2E332A), RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text, color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp)
        Icon(
            Icons.Default.Close,
            contentDescription = "Remove",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(16.dp)
                .clickable { onRemove() }
        )
    }
}
