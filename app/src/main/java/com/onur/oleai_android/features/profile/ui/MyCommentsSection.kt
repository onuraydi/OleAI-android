package com.onur.oleai_android.features.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.onur.oleai_android.core.network.dto.MyCommentDto
import com.onur.oleai_android.features.profile.viewmodel.MyCommentsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCommentsSection(
    modifier: Modifier = Modifier,
    viewModel: MyCommentsViewModel = hiltViewModel(),
    listState: LazyListState = rememberLazyListState()
) {
    val state by viewModel.state.collectAsState()
    
    val isScrollToEnd by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsNumber = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            lastVisibleItemIndex > (totalItemsNumber - 2)
        }
    }

    LaunchedEffect(isScrollToEnd) {
        if (isScrollToEnd && !state.isLoading && !state.isLoadingMore && state.hasMore && state.comments.isNotEmpty()) {
            viewModel.loadMoreComments()
        }
    }
    val focusManager = LocalFocusManager.current
    var isSortDropdownOpen by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = "Yorumlarım",
                tint = Color(0xFF8BC34A),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Yorumlarım (${state.totalComments})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Search & Sort Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                placeholder = { Text("Yorumlarda ara...", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Ara", tint = Color.Gray, modifier = Modifier.size(20.dp))
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF8BC34A),
                    unfocusedBorderColor = Color(0xFF2A2D28),
                    focusedContainerColor = Color(0xFF1E211D),
                    unfocusedContainerColor = Color(0xFF1E211D),
                    cursorColor = Color(0xFF8BC34A)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        viewModel.onSearch()
                    }
                )
            )

            // Sort Dropdown
            Box {
                Surface(
                    color = Color(0xFF1E211D),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(50.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .clickable { isSortDropdownOpen = true }
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(state.sortBy, color = Color.LightGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = "Sırala", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                    }
                }
                DropdownMenu(
                    expanded = isSortDropdownOpen,
                    onDismissRequest = { isSortDropdownOpen = false },
                    modifier = Modifier.background(Color(0xFF1E211D))
                ) {
                    val sortOptions = listOf("En Yeni", "En Eski", "En Yüksek Puan", "En Düşük Puan")
                    sortOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = if (option == state.sortBy) Color(0xFF81B8EF) else Color.White) },
                            onClick = {
                                isSortDropdownOpen = false
                                viewModel.applySort(option)
                            }
                        )
                    }
                }
            }
        }

        // Content
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF8BC34A))
            }
        } else if (state.error != null) {
            Text(
                text = state.error ?: "Bir hata oluştu",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        } else if (state.comments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Henüz yorumunuz bulunmuyor.", color = Color.Gray)
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                state.comments.forEach { comment ->
                    CommentCard(
                        comment = comment,
                        onDeleteClick = { viewModel.deleteComment(comment.actualId) },
                        onEditClick = { /* TODO: Yorum Düzenleme Bottom Sheet vb. */ }
                    )
                }
                
                if (state.isLoadingMore) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF8BC34A),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommentCard(
    comment: MyCommentDto,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF161815) // Sleek dark card background
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Left: Recipe Image
            AsyncImage(
                model = comment.recipeImageUrl,
                contentDescription = comment.recipeName,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // Right: Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Title and Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = comment.recipeName ?: "Bilinmeyen Tarif",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Düzenle",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { onEditClick() }
                        )
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Sil",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { onDeleteClick() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = "Tarih",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = formatCommentDate(comment.createdDate),
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Rating Stars
                if (comment.rating != null) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < comment.rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = null,
                                tint = Color(0xFFFF9800), // Orange stars
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Comment Content
                Text(
                    text = comment.content ?: "",
                    color = Color(0xFFDDDDDD),
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

private fun formatCommentDate(dateStr: String?): String {
    if (dateStr.isNullOrEmpty()) return "Bilinmeyen Tarih"
    return try {
        // Simple formatter, e.g., "2026-06-22T13:36:00Z" -> "22 Haziran 2026 13:36"
        val parts = dateStr.split("T")
        if (parts.size >= 2) {
            val dateParts = parts[0].split("-")
            val timeParts = parts[1].split(":")
            if (dateParts.size >= 3 && timeParts.size >= 2) {
                val year = dateParts[0]
                val month = when (dateParts[1]) {
                    "01" -> "Ocak" "02" -> "Şubat" "03" -> "Mart"
                    "04" -> "Nisan" "05" -> "Mayıs" "06" -> "Haziran"
                    "07" -> "Temmuz" "08" -> "Ağustos" "09" -> "Eylül"
                    "10" -> "Ekim" "11" -> "Kasım" "12" -> "Aralık"
                    else -> ""
                }
                val day = dateParts[2].toInt().toString() // remove leading zero
                val hour = timeParts[0]
                val min = timeParts[1]
                "$day $month $year $hour:$min"
            } else {
                dateStr
            }
        } else {
            dateStr
        }
    } catch (e: Exception) {
        dateStr
    }
}
