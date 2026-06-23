package com.onur.oleai_android.features.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.onur.oleai_android.features.home.viewmodel.CategoryStat

@Composable
fun DashboardBaseCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconTint.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryDistributionCard(total: Int, topCategories: List<CategoryStat>) {
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer
    )
    
    DashboardBaseCard(
        title = "Dolap Dağılımı",
        subtitle = "Toplam $total malzeme",
        icon = Icons.Default.PieChart,
        iconTint = MaterialTheme.colorScheme.primary
    ) {
        if (total > 0 && topCategories.isNotEmpty()) {
            // Percentage Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)) // Empty background
            ) {
                var totalWeight = 0
                topCategories.take(4).forEachIndexed { index, stat ->
                    if (stat.percentage > 0) {
                        Box(
                            modifier = Modifier
                                .weight(stat.percentage.toFloat())
                                .fillMaxHeight()
                                .background(colors.getOrElse(index) { MaterialTheme.colorScheme.onSurfaceVariant })
                        )
                        totalWeight += stat.percentage
                    }
                }
                val remaining = 100 - totalWeight
                if (remaining > 0) {
                    Spacer(modifier = Modifier.weight(remaining.toFloat()).fillMaxHeight())
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            // Legends
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                topCategories.take(4).forEachIndexed { index, stat ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(colors.getOrElse(index) { MaterialTheme.colorScheme.onSurfaceVariant })
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${stat.name} %${stat.percentage}", 
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        } else {
            Text(text = "Analiz edilecek veri yok.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun FreshnessIndexCard(fresh: Int, expiring: Int, expired: Int) {
    val total = fresh + expiring + expired
    val freshPct = if (total == 0) 0f else fresh.toFloat() / total
    val expPct = if (total == 0) 0f else expiring.toFloat() / total
    val expiredPct = if (total == 0) 0f else expired.toFloat() / total

    DashboardBaseCard(
        title = "Tazelik Endeksi",
        subtitle = "Gıda güvenliği",
        icon = Icons.Default.Speed,
        iconTint = MaterialTheme.colorScheme.primary
    ) {
        if (total > 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                if (freshPct > 0) Box(modifier = Modifier.weight(freshPct).fillMaxHeight().background(MaterialTheme.colorScheme.primary))
                if (expPct > 0) Box(modifier = Modifier.weight(expPct).fillMaxHeight().background(Color(0xFFF59E0B)))
                if (expiredPct > 0) Box(modifier = Modifier.weight(expiredPct).fillMaxHeight().background(MaterialTheme.colorScheme.error))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = fresh.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    Text(text = "TAZE", style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = expiring.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = Color(0xFFF59E0B))
                    Text(text = "RİSKLİ", style = MaterialTheme.typography.labelSmall)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = expired.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.error)
                    Text(text = "GEÇMİŞ", style = MaterialTheme.typography.labelSmall)
                }
            }
        } else {
            Text(text = "Analiz edilecek veri yok.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ShoppingProgressCard(total: Int, purchased: Int) {
    val pct = if (total == 0) 0 else (purchased * 100) / total
    
    DashboardBaseCard(
        title = "Alışveriş Durumu",
        subtitle = "Liste tamamlama",
        icon = Icons.Default.LocalMall,
        iconTint = MaterialTheme.colorScheme.primary
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "$purchased Ürün Alındı", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${total - purchased} Ürün Bekliyor", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp)) {
                CircularProgressIndicator(
                    progress = { if (total == 0) 0f else purchased.toFloat() / total },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer,
                    strokeWidth = 6.dp
                )
                Text(text = "%$pct", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Black)
            }
        }
    }
}
