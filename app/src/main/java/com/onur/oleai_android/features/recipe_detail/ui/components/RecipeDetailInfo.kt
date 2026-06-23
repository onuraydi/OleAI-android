package com.onur.oleai_android.features.recipe_detail.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onur.oleai_android.core.network.dto.BasicUserInfo
import com.onur.oleai_android.core.network.dto.RecipeCategoryResponse
import com.onur.oleai_android.core.network.dto.RecipeStatisticsResponse
import com.onur.oleai_android.core.network.dto.TagDto

@Composable
fun RecipeDetailInfo(
    title: String,
    description: String?,
    author: BasicUserInfo?,
    statistics: RecipeStatisticsResponse,
    categories: List<RecipeCategoryResponse>,
    tags: List<TagDto>,
    difficulty: String,
    servings: Int,
    prepTime: Int,
    cookTime: Int,
    isLiked: Boolean,
    isDisliked: Boolean,
    isSaved: Boolean,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    onCommentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {
        // Title
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Categories & Tags
        val allChips = categories.map { it.name } + tags.map { "#${it.name}" }
        if (allChips.isNotEmpty()) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(allChips) { chipText ->
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = chipText, color = Color(0xFF90B551), fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Author & Rating Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Author
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF2A2A2A), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = author?.username?.firstOrNull()?.toString()?.uppercase() ?: "?",
                        color = Color(0xFF90B551),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "TARİF SAHİBİ", fontSize = 10.sp, color = Color.Gray)
                    Text(text = author?.username ?: "Bilinmiyor", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // Rating
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFF2A2A2A), RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Filled.Star, contentDescription = "Star", tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${statistics.averageRating} (${statistics.totalRatings} Değerlendirme)",
                    color = Color(0xFFFFB800),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        if (!description.isNullOrEmpty()) {
            var isExpanded by remember { mutableStateOf(false) }
            var showReadMore by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
                    .padding(16.dp)
                    .animateContentSize()
            ) {
                Text(text = "Tarif Hakkında", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description, 
                    color = Color.LightGray, 
                    fontSize = 14.sp,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                    overflow = TextOverflow.Ellipsis,
                    onTextLayout = { textLayoutResult ->
                        if (textLayoutResult.hasVisualOverflow) {
                            showReadMore = true
                        }
                    }
                )
                if (showReadMore || isExpanded) {
                    Text(
                        text = if (isExpanded) "Daha az göster" else "Devamını oku",
                        color = Color(0xFF90B551),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .clickable { isExpanded = !isExpanded }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        val localizedDifficulty = when (difficulty.lowercase()) {
            "easy" -> "Kolay"
            "medium" -> "Orta"
            "hard" -> "Zor"
            else -> difficulty
        }

        // Stats Row (Compact)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatCard(title = "ZORLUK", value = localizedDifficulty)
            StatCard(title = "PORSİYON", value = "$servings")
            StatCard(title = "HAZIRLIK", value = "$prepTime dk")
            StatCard(title = "PİŞİRME", value = "$cookTime dk")
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons Row (No background, compact)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                icon = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                label = statistics.totalLikes.toString(),
                tint = if (isLiked) Color(0xFF90B551) else Color.White,
                onClick = onLikeClick
            )
            ActionButton(
                icon = if (isDisliked) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                label = statistics.totalDislikes.toString(),
                tint = if (isDisliked) Color(0xFF90B551) else Color.White,
                onClick = onDislikeClick
            )
            ActionButton(
                icon = Icons.Filled.ChatBubbleOutline,
                label = statistics.totalComments.toString(),
                tint = Color.White,
                onClick = onCommentClick
            )
            ActionButton(
                icon = if (isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                label = "Kaydet",
                tint = if (isSaved) Color(0xFF90B551) else Color.White,
                onClick = onSaveClick
            )
            ActionButton(
                icon = Icons.Filled.Share,
                label = "Paylaş",
                tint = Color.White,
                onClick = onShareClick
            )
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clip(RoundedCornerShape(8.dp)).padding(8.dp)
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
            Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(24.dp))
        }
        Text(text = label, color = tint, fontSize = 10.sp)
    }
}
