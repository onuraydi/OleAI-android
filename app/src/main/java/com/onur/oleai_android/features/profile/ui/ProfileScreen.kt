package com.onur.oleai_android.features.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.onur.oleai_android.core.network.dto.UserProfileResponse
import com.onur.oleai_android.core.network.dto.GetUserStatisticsResponse
import com.onur.oleai_android.features.profile.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: UserViewModel = hiltViewModel(),
    onNavigateToRecipeDetail: (String) -> Unit = {},
    onNavigateToCreateRecipe: () -> Unit = {},
    onEditProfileClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {}
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val userStats by viewModel.userStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchUserProfile()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading && userProfile == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF8BC34A))
            }
            return@Box
        }

        var selectedTab by remember { mutableStateOf(0) }
        val listState = androidx.compose.foundation.lazy.rememberLazyListState()

        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = { viewModel.fetchUserProfile() },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
            item {
                ProfileHeader(
                    userProfile = userProfile,
                    onEditProfileClick = { selectedTab = 3 }
                )
            }

            item {
                ProfileTabs(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    onEditProfileClick = { selectedTab = 3 }
                )
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // Genel Bakış
                    item {
                        ActivityStatsSection(userStats = userStats)
                    }
                    item {
                        PreferencesSection(
                            userProfile = userProfile,
                            onEditClick = { selectedTab = 4 }
                        )
                    }
                }
                1 -> {
                    // Tariflerim
                    item {
                        MyRecipesSection(
                            listState = listState,
                            onRecipeClick = onNavigateToRecipeDetail,
                            onCreateRecipeClick = onNavigateToCreateRecipe
                        )
                    }
                }
                2 -> {
                    // Yorumlarım
                    item {
                        MyCommentsSection(listState = listState)
                    }
                }
                3 -> {
                    // Profili Düzenle
                    item {
                        EditProfileSection(userProfile = userProfile)
                    }
                }
                4 -> {
                    // Beslenme
                    item {
                        userProfile?.let {
                            NutritionSection(userProfile = it)
                        } ?: run {
                            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFF7CB342))
                            }
                        }
                    }
                }
                5 -> {
                    // Ayarlar
                    item {
                        userProfile?.let { profile ->
                            val context = androidx.compose.ui.platform.LocalContext.current
                            SettingsSection(
                                userProfile = profile,
                                onChangePassword = { request ->
                                    viewModel.changePassword(
                                        request = request,
                                        onSuccess = { message ->
                                            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show()
                                        },
                                        onError = { errorMsg ->
                                            android.widget.Toast.makeText(context, errorMsg, android.widget.Toast.LENGTH_LONG).show()
                                        }
                                    )
                                },
                                onLogoutClick = {
                                    viewModel.logout(onLogoutSuccess = onLogoutClick)
                                }
                            )
                        } ?: run {
                            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFF7CB342))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
        }
    }
}

@Composable
fun ProfileHeader(
    userProfile: UserProfileResponse?,
    onEditProfileClick: () -> Unit
) {
    val name = userProfile?.let { "${it.firstName} ${it.lastName}" } ?: ""
    val username = userProfile?.username?.let { "@$it" } ?: ""
    val email = userProfile?.email ?: ""
    
    val memberSince = remember(userProfile?.createdDate) {
        val dateStr = userProfile?.createdDate
        if (dateStr.isNullOrEmpty()) {
            ""
        } else {
            try {
                // Assuming format like 2026-06-18T...
                val parts = dateStr.split("-", "T")
                if (parts.size >= 2) {
                    val year = parts[0]
                    val month = when (parts[1]) {
                        "01" -> "Ocak" "02" -> "Şubat" "03" -> "Mart"
                        "04" -> "Nisan" "05" -> "Mayıs" "06" -> "Haziran"
                        "07" -> "Temmuz" "08" -> "Ağustos" "09" -> "Eylül"
                        "10" -> "Ekim" "11" -> "Kasım" "12" -> "Aralık"
                        else -> ""
                    }
                    "$month $year'dan beri üye"
                } else {
                    ""
                }
            } catch (e: Exception) {
                ""
            }
        }
    }
    
    val isVerified = userProfile?.isEmailVerified == true

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Avatar Container Box
        Box(contentAlignment = Alignment.Center) {
            // Avatar Box
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFB388FF), Color(0xFF6200EA))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!userProfile?.profileImageUrl.isNullOrEmpty()) {
                    coil.compose.AsyncImage(
                        model = userProfile?.profileImageUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Text(
                        text = name.firstOrNull()?.toString() ?: "S",
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            

        }

        // User Info Column
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Name and Username
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = name,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = username,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            // Email & Date
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (email.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Outlined.Email, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Text(email, color = Color.Gray, fontSize = 11.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    }
                }
                if (memberSince.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Outlined.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Text(memberSince, color = Color.Gray, fontSize = 11.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                    }
                }
            }
            
            // Verified Badge & Edit Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Verified Badge
                if (isVerified) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF00C853).copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF00C853), modifier = Modifier.size(10.dp))
                        Text("Doğrulandı", color = Color(0xFF00C853), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Edit Profile Button
                OutlinedButton(
                    onClick = onEditProfileClick,
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    modifier = Modifier.height(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(Color.DarkGray))
                ) {
                    Text("Düzenle", fontWeight = FontWeight.SemiBold, fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
fun ProfileTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onEditProfileClick: () -> Unit = {}
) {
    val tabs = listOf(
        Pair("Genel Bakış", Icons.Outlined.BarChart),
        Pair("Tariflerim", Icons.Outlined.MenuBook),
        Pair("Yorumlarım", Icons.Outlined.ChatBubbleOutline),
        Pair("Profili Düzenle", Icons.Outlined.PersonOutline),
        Pair("Beslenme", Icons.Outlined.Eco),
        Pair("Ayarlar", Icons.Outlined.Settings)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF1E211D))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEachIndexed { index, tab ->
            val isSelected = selectedTab == index
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) Color(0xFF7CB342) else Color.Transparent)
                    .clickable {
                        onTabSelected(index)
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = tab.second,
                    contentDescription = tab.first,
                    tint = if (isSelected) Color.Black else Color.LightGray,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = tab.first,
                    color = if (isSelected) Color.Black else Color.LightGray,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ActivityStatsSection(userStats: GetUserStatisticsResponse?) {
    val stats = listOf(
        Triple("Oluşturulan Tarif", userStats?.totalRecipesCreated ?: 0, Icons.Outlined.Book),
        Triple("Kaydedilen", userStats?.totalRecipesSaved ?: 0, Icons.Outlined.BookmarkBorder),
        Triple("Kilerdeki Malzeme", userStats?.totalPantryItems ?: 0, Icons.Outlined.Kitchen),
        Triple("Beğenilen", userStats?.totalRecipesLiked ?: 0, Icons.Outlined.FavoriteBorder)
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
            Icon(Icons.Filled.BarChart, contentDescription = null, tint = Color(0xFF7CB342), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Aktivite İstatistikleri", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(stats[0], modifier = Modifier.weight(1f))
                StatCard(stats[1], modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard(stats[2], modifier = Modifier.weight(1f))
                StatCard(stats[3], modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun StatCard(stat: Triple<String, Int, ImageVector>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1E1A))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = stat.third,
                contentDescription = null,
                tint = Color(0xFF7CB342),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stat.second.toString(),
                color = Color(0xFF7CB342),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = stat.first,
                color = Color.Gray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PreferencesSection(
    userProfile: UserProfileResponse?,
    onEditClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        PreferenceCard(
            title = "Beslenme Tercihleri",
            icon = Icons.Outlined.Eco,
            items = userProfile?.dietaryPreferences?.map { it.name } ?: emptyList(),
            emptyMessage = "Tercih bulunmuyor.",
            onEditClick = onEditClick
        )
        PreferenceCard(
            title = "Alerjiler",
            icon = Icons.Outlined.WarningAmber,
            items = userProfile?.allergies?.map { it.name } ?: emptyList(),
            emptyMessage = "Alerji bulunmuyor.",
            onEditClick = onEditClick
        )
        PreferenceCard(
            title = "Sevilmeyen Malzemeler",
            icon = Icons.Outlined.ThumbDownOffAlt,
            items = userProfile?.dislikedIngredients?.map { it.name } ?: emptyList(),
            emptyMessage = "Sevilmeyen bir malzeme bulunmuyor.",
            onEditClick = onEditClick
        )
    }
}

@Composable
fun PreferenceCard(
    title: String,
    icon: ImageVector,
    items: List<String>,
    emptyMessage: String = "",
    onEditClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B1E1A))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
                
                Surface(
                    color = Color.DarkGray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.clickable { onEditClick() }
                ) {
                    Text(
                        text = "Düzenle",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (items.isEmpty()) {
                Text(
                    text = emptyMessage,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items.forEach { item ->
                        Surface(
                            color = Color(0xFF2C332A),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = item,
                                color = Color(0xFF7CB342),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
