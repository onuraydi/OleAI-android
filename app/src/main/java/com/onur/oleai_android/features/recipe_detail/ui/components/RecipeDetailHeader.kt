package com.onur.oleai_android.features.recipe_detail.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material.icons.filled.Close

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeDetailHeader(
    images: List<String>,
    onNavigateBack: () -> Unit,
    onEditClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showFullScreenImage by remember { mutableStateOf<Int?>(null) }

    Box(modifier = modifier.fillMaxWidth().height(300.dp)) {
        val pagerState = rememberPagerState(pageCount = { images.size })

        if (images.isNotEmpty() && images.first().isNotBlank()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                AsyncImage(
                    model = images[page],
                    contentDescription = "Recipe Image $page",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { showFullScreenImage = page }
                )
            }
            
            // Pager Indicators
            if (images.size > 1) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(images.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) Color(0xFF90B551) else Color.LightGray.copy(alpha = 0.5f)
                        val width = if (pagerState.currentPage == iteration) 24.dp else 8.dp
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clip(CircleShape)
                                .background(color)
                                .height(8.dp)
                                .width(width)
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray)
            )
        }

        // Back button
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Geri",
                tint = Color.White
            )
        }

        if (onEditClick != null) {
            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Düzenle",
                    tint = Color.White
                )
            }
        }
    }

    if (showFullScreenImage != null) {
        Dialog(
            onDismissRequest = { showFullScreenImage = null },
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                val fullScreenPagerState = rememberPagerState(
                    initialPage = showFullScreenImage ?: 0,
                    pageCount = { images.size }
                )
                
                HorizontalPager(
                    state = fullScreenPagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    AsyncImage(
                        model = images[page],
                        contentDescription = "Full Screen Image $page",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Close button
                IconButton(
                    onClick = { showFullScreenImage = null },
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                        .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Kapat", tint = Color.White)
                }
                
                // Indicators for Full Screen
                if (images.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(images.size) { iteration ->
                            val color = if (fullScreenPagerState.currentPage == iteration) Color(0xFF90B551) else Color.Gray
                            val width = if (fullScreenPagerState.currentPage == iteration) 24.dp else 8.dp
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .height(8.dp)
                                    .width(width)
                            )
                        }
                    }
                }
            }
        }
    }
}
