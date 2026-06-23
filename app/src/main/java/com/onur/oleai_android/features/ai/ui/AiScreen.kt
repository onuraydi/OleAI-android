package com.onur.oleai_android.features.ai.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.onur.oleai_android.features.ai.viewmodel.AiViewModel
import com.onur.oleai_android.features.home.ui.components.RecipeCard

@Composable
fun AiScreen(
    onNavigateToRecipeDetail: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AiViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfileSummary()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212)) // Dark background
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Bir Sonraki Öğününü",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Keşfet",
                color = Color(0xFF7CB342), // Green color from design
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Nasıl bir deneyim aradığını seç ve OleAI sana özel kusursuz bir lezzet serüveni hazırlasın.",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            // Profil Özeti
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E1E1E))
                    .padding(16.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("KİŞİSEL BAĞLAM", color = Color(0xFF689F38), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("OleAI Aktif", color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Profil Özeti", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Zeka motoru sana özel öneriler sunarken şu parametreleri anlık olarak kullanır:", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF252525)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF1A237E)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF5C6BC0))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Dolap Stoğu", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${uiState.pantryItemCount} malzeme tanımlı", color = Color.Gray, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFF252525)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF1B5E20)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF66BB6A))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Diyetler", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(uiState.dietsText, color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Motor Ayarları
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E1E1E))
                    .padding(16.dp)
            ) {
                Text("GELİŞMİŞ SEÇENEKLER", color = Color(0xFF689F38), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Motor Ayarları", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Yapay zeka önerilerini oluştururken kullanılacak ek filtreleri buradan açıp kapatabilirsiniz.", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))
                
                AiToggleRow(title = "İsrafı Önle", checked = uiState.preventWaste, onCheckedChange = { viewModel.togglePreventWaste(it) })
                AiToggleRow(title = "Alerji Koruması", checked = uiState.allergyProtection, onCheckedChange = { viewModel.toggleAllergyProtection(it) })
                AiToggleRow(title = "Diyet Kuralları", checked = uiState.strictDietRules, onCheckedChange = { viewModel.toggleStrictDietRules(it) })
                AiToggleRow(title = "Sevilmeyenler", checked = uiState.excludeDisliked, onCheckedChange = { viewModel.toggleExcludeDisliked(it) })
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        item {
            // Analiz Modu
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analiz Modu", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))

            var expanded by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E1E))
                    .clickable { expanded = true }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = uiState.analysisMode.label, color = Color.White, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    com.onur.oleai_android.features.ai.viewmodel.availableAnalysisModes.forEach { mode ->
                        DropdownMenuItem(
                            text = { Text(mode.label) },
                            onClick = { viewModel.updateAnalysisMode(mode.id); expanded = false }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Bilgi Kutusu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E1E1E))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("${uiState.analysisMode.label} MODU", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(uiState.analysisMode.description, color = Color.Gray, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Button(
                onClick = { viewModel.getRecommendations() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF689F38)), // Green
                shape = RoundedCornerShape(16.dp)
            ) {
                if (uiState.isLoadingRecommendations) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Önerileri Getir", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        if (uiState.recommendationsFetched) {
            item {
                Text("Senin İçin Seçtiklerimiz", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Text("Zeka Motoru profilini analiz etti ve en uygun ${uiState.recommendations.size} tarifi buldu.", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))

                if (!uiState.hasPerfectMatch) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFF333333), RoundedCornerShape(12.dp))
                            .background(Color(0xFF1A1A1A))
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF689F38), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Daha Fazla Etkileşime İhtiyacımız Var", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Sistemimiz şu anda sizi tam olarak tanımadığı veya elinizdeki malzemelerle uygun eşleşme bulamadığı için size platformdaki en popüler trend tarifleri gösteriyor. Size özel kusursuz öneriler sunabilmemiz için bol bol tarif beğenmeniz veya kaydetmeniz gerekiyor!", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // In LazyColumn, we can't directly nest LazyVerticalGrid, so we use items() to make pairs for a 2-column look
            val pairedList = uiState.recommendations.chunked(2)
            items(pairedList.size) { index ->
                val pair = pairedList[index]
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        AiRecipeCard(response = pair[0], onClick = { onNavigateToRecipeDetail(pair[0].recipe.id) })
                    }
                    if (pair.size > 1) {
                        Box(modifier = Modifier.weight(1f)) {
                            AiRecipeCard(response = pair[1], onClick = { onNavigateToRecipeDetail(pair[1].recipe.id) })
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun AiToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF689F38),
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFF424242)
            )
        )
    }
}

@Composable
fun AiRecipeCard(response: com.onur.oleai_android.core.network.dto.AiRecommendationResponse, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E1E1E))
            .clickable { onClick() }
    ) {
        Column {
            // We just show a placeholder box for image since RecipeCard does standard stuff
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color.DarkGray)
            ) {
                // Top Right Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFFFA000))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    val percentage = (response.matchScore * 100).toInt()
                    Text("%${percentage} Eşleşme", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(response.recipe.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(response.recipe.description ?: "", color = Color.Gray, fontSize = 10.sp, maxLines = 2)
            }
        }
    }
}
