package com.onur.oleai_android.features.onboarding.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.onur.oleai_android.R
import com.onur.oleai_android.features.profile.viewmodel.UserViewModel
import kotlinx.coroutines.launch

data class OnboardingPage(
    val imageRes: Int,
    val badgeText: String,
    val title: String,
    val description: String
)

val onboardingPages = listOf(
    OnboardingPage(
        imageRes = R.drawable.img_onboarding_1,
        badgeText = "YAPAY ZEKA",
        title = "AI Mutfak Sihri",
        description = "Mutfağındaki malzemelere göre sana özel akıllı tarif önerileri al. OleAI, senin için en uygun lezzetleri saniyeler içinde keşfeder."
    ),
    OnboardingPage(
        imageRes = R.drawable.img_onboarding_2,
        badgeText = "DOLABIM",
        title = "Akıllı Dolabım",
        description = "Elindeki malzemeleri dijital dolabına ekle, son kullanma tarihlerini takip et ve israfı önle. Dolabında ne varsa, tarifinde o var!"
    ),
    OnboardingPage(
        imageRes = R.drawable.img_onboarding_3,
        badgeText = "TOPLULUK",
        title = "Paylaş ve Keşfet",
        description = "Yemek tutkunları topluluğuna katıl. Kendi tariflerini paylaş, popüler trendleri takip et ve dünya mutfaklarından ilham al."
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToHome: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()
    val onboardingCompleted by userViewModel.onboardingCompleted.collectAsState()
    val error by userViewModel.error.collectAsState()
    val context = LocalContext.current

    // Hata mesajını göster
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            userViewModel.clearState()
        }
    }

    // Eğer onboarding başarıyla güncellendiyse anasayfaya yönlendir
    LaunchedEffect(onboardingCompleted) {
        if (onboardingCompleted) {
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F0F)) // Koyu arkaplan
    ) {

        // Orta Kısım: Horizontal Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val item = onboardingPages[page]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Görsel ve Badge
                Box(
                    contentAlignment = Alignment.BottomEnd,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    Image(
                        painter = painterResource(id = item.imageRes),
                        contentDescription = item.title,
                        modifier = Modifier
                            .size(240.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    
                    // Badge
                    Box(
                        modifier = Modifier
                            .offset(y = 12.dp)
                            .background(Color(0xFF1E2319), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = item.badgeText,
                            color = Color(0xFF8DBE55),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Başlık
                Text(
                    text = item.title,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Açıklama
                Text(
                    text = item.description,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
            }
        }

        // Alt Kısım: Pager Indicator ve Buton
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Pager Dots
            Row(
                modifier = Modifier.padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(onboardingPages.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color(0xFF8DBE55) else Color.DarkGray
                    val width = if (pagerState.currentPage == iteration) 16.dp else 8.dp
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .height(8.dp)
                            .width(width)
                    )
                }
            }

            // Buton
            Button(
                onClick = {
                    if (pagerState.currentPage < onboardingPages.size - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        userViewModel.completeOnboarding()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8DBE55)),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage == onboardingPages.size - 1) "Hadi Başlayalım!" else "Sıradaki",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                if (pagerState.currentPage < onboardingPages.size - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "İleri",
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Üst Kısım: Logo ve Geç Butonu (En üstte çizilmesi için Box'ın en sonuna eklendi)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .statusBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "OleAI",
                color = Color(0xFF8DBE55),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            TextButton(
                onClick = { userViewModel.completeOnboarding() },
                modifier = Modifier
            ) {
                Text(
                    text = "Geç",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}
