package com.onur.oleai_android.features.recipe_detail.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.onur.oleai_android.core.network.dto.RecipeCommentResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailComments(
    totalComments: Int,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    // Comment form bindings
    isEditing: Boolean = false,
    editingComment: RecipeCommentResponse? = null,
    isSubmitting: Boolean = false,
    onSubmitComment: (content: String, rating: Int?) -> Unit = { _, _ -> },
    onCancelEdit: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var newCommentText by remember(editingComment) {
        mutableStateOf(editingComment?.content ?: "")
    }
    var selectedRating by remember(editingComment) {
        mutableStateOf(editingComment?.rating ?: 0)
    }

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .fillMaxWidth()
            .background(
                Color(0xFF1E1E1E),
                if (isExpanded) RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp) else RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpand() }
                .padding(vertical = 8.dp)
        ) {
            Icon(Icons.Filled.ChatBubbleOutline, contentDescription = "Yorumlar", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Yorumlar", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("$totalComments yorum yapıldı", color = Color.Gray, fontSize = 12.sp)
            }
            Icon(
                imageVector = if (isExpanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Daralt" else "Genişlet",
                tint = Color.Gray
            )
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFF2A2A2A), thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Edit mode banner
            if (isEditing && editingComment != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2A2A2A), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = null, tint = Color(0xFF90B551), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Yorumu düzenliyorsunuz", color = Color(0xFF90B551), fontSize = 12.sp, modifier = Modifier.weight(1f))
                    IconButton(onClick = onCancelEdit, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Filled.Close, contentDescription = "İptal", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Comment Input
            Column(modifier = Modifier.fillMaxWidth()) {
                // Star Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 10.dp)
                ) {
                    repeat(5) { index ->
                        val starIndex = index + 1
                        Icon(
                            imageVector = if (starIndex <= selectedRating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Yıldız $starIndex",
                            tint = if (starIndex <= selectedRating) Color(0xFFFFB800) else Color(0xFF555555),
                            modifier = Modifier
                                .size(32.dp)
                                .clickable { selectedRating = if (selectedRating == starIndex) 0 else starIndex }
                        )
                        Spacer(Modifier.width(4.dp))
                    }
                    if (selectedRating > 0) {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = when (selectedRating) {
                                1 -> "Kötü"
                                2 -> "İdare eder"
                                3 -> "Fena değil"
                                4 -> "Güzel"
                                5 -> "Mükemmel!"
                                else -> ""
                            },
                            color = Color(0xFFFFB800),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Text input
                TextField(
                    value = newCommentText,
                    onValueChange = { newCommentText = it },
                    placeholder = { Text("Düşüncelerini paylaş veya soru sor...", color = Color.Gray, fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 90.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF121212),
                        unfocusedContainerColor = Color(0xFF121212),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF90B551),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        if (newCommentText.isNotBlank()) {
                            onSubmitComment(newCommentText.trim(), if (selectedRating > 0) selectedRating else null)
                            newCommentText = ""
                            selectedRating = 0
                        }
                    },
                    enabled = newCommentText.isNotBlank() && !isSubmitting,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF90B551),
                        disabledContainerColor = Color(0xFF3A4A2A)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Filled.Send, contentDescription = "Gönder", modifier = Modifier.size(16.dp), tint = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (isEditing) "Güncelle" else "Yorum Yap",
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentItem(
    comment: RecipeCommentResponse,
    currentUserId: String,
    replyingToCommentId: String? = null,
    onEditClick: (RecipeCommentResponse) -> Unit = {},
    onDeleteClick: (RecipeCommentResponse) -> Unit = {},
    onReplyClick: (RecipeCommentResponse) -> Unit = {},
    onSubmitReply: (content: String) -> Unit = {},
    onCancelReply: () -> Unit = {},
    isSubmittingReply: Boolean = false,
    isRoot: Boolean = true,
    modifier: Modifier = Modifier
) {
    val isOwner = comment.userId == currentUserId
    val isReplying = replyingToCommentId == comment.id
    var replyText by remember(isReplying) { mutableStateOf("") }

    Row(verticalAlignment = Alignment.Top, modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF2A2A2A), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                comment.username.firstOrNull()?.toString()?.uppercase() ?: "?",
                color = Color(0xFF90B551),
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(comment.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        if (comment.rating != null && comment.rating > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Row(
                                modifier = Modifier
                                    .background(Color(0xFF2A2A2A), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Star, contentDescription = "Star", tint = Color(0xFFFFB800), modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(comment.rating.toString(), color = Color(0xFFFFB800), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Text(comment.createdDate, color = Color.Gray, fontSize = 10.sp)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(comment.content, color = Color.LightGray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // Action row below content
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isOwner) {
                    IconButton(
                        onClick = { onEditClick(comment) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Düzenle", tint = Color(0xFF90B551), modifier = Modifier.size(15.dp))
                    }
                    IconButton(
                        onClick = { onDeleteClick(comment) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = "Sil", tint = Color(0xFFE53935), modifier = Modifier.size(15.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                }
                if (isRoot) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (isReplying) Color(0xFF90B551).copy(alpha = 0.2f) else Color(0xFF2A2A2A),
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { if (isReplying) onCancelReply() else onReplyClick(comment) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Reply,
                                contentDescription = "Cevapla",
                                tint = if (isReplying) Color(0xFF90B551) else Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (isReplying) "İptal" else "Cevapla",
                                color = if (isReplying) Color(0xFF90B551) else Color.White,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Inline reply form
            if (isRoot && isReplying) {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF161616), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    TextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        placeholder = {
                            Text(
                                "${comment.username} kullanıcısına cevap ver...",
                                color = Color(0xFF555555),
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color(0xFF90B551),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        maxLines = 4
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onCancelReply) {
                            Text("İptal", color = Color.Gray, fontSize = 12.sp)
                        }
                        Spacer(Modifier.width(4.dp))
                        Button(
                            onClick = {
                                if (replyText.isNotBlank()) {
                                    onSubmitReply(replyText.trim())
                                    replyText = ""
                                }
                            },
                            enabled = replyText.isNotBlank() && !isSubmittingReply,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF90B551),
                                disabledContainerColor = Color(0xFF3A4A2A)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            if (isSubmittingReply) {
                                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Filled.Reply, contentDescription = "Gönder", modifier = Modifier.size(14.dp), tint = Color.Black)
                            }
                            Spacer(Modifier.width(4.dp))
                            Text("Cevapla", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            // Nested replies
            if (isRoot && comment.replies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                val allReplies = androidx.compose.runtime.remember(comment.replies) {
                    val list = mutableListOf<RecipeCommentResponse>()
                    fun traverse(c: RecipeCommentResponse) {
                        list.add(c)
                        c.replies.forEach { traverse(it) }
                    }
                    comment.replies.forEach { traverse(it) }
                    list
                }
                
                allReplies.forEach { reply ->
                    CommentItem(
                        comment = reply,
                        currentUserId = currentUserId,
                        replyingToCommentId = replyingToCommentId,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick,
                        onReplyClick = onReplyClick,
                        onSubmitReply = onSubmitReply,
                        onCancelReply = onCancelReply,
                        isSubmittingReply = isSubmittingReply,
                        isRoot = false,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DeleteCommentDialog(
    comment: RecipeCommentResponse,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E1E),
        title = {
            Text("Yorumu Sil", color = Color.White, fontWeight = FontWeight.Bold)
        },
        text = {
            Text(
                "Bu yorumu silmek istediğinizden emin misiniz? Bu işlem geri alınamaz.",
                color = Color.Gray,
                fontSize = 14.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Sil", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal", color = Color.Gray)
            }
        }
    )
}
