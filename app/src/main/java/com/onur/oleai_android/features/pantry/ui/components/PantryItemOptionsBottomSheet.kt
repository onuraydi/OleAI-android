package com.onur.oleai_android.features.pantry.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.onur.oleai_android.core.network.dto.MyPantryItemResponse
import com.onur.oleai_android.core.ui.utils.IconMapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryItemOptionsBottomSheet(
    item: MyPantryItemResponse,
    onDismissRequest: () -> Unit,
    onEditClick: () -> Unit,
    onShoppingCartClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Item Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = IconMapper.getIconByName(item.categoryIconName.ifEmpty { "leaf" }),
                    contentDescription = null,
                    tint = Color(0xFF8DC63F),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "${item.quantity.toInt()} ${item.unit}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider(color = Color.DarkGray, thickness = 1.dp)
            Spacer(modifier = Modifier.height(16.dp))

            // Options
            OptionRow(
                icon = Icons.Default.Edit,
                text = "Düzenle",
                tint = Color(0xFF8DC63F),
                onClick = {
                    onDismissRequest()
                    onEditClick()
                }
            )

            OptionRow(
                icon = Icons.Default.ShoppingCart,
                text = "Alışveriş Listesine Ekle",
                tint = Color(0xFF3B82F6),
                onClick = {
                    onDismissRequest()
                    onShoppingCartClick()
                }
            )

            OptionRow(
                icon = Icons.Default.Delete,
                text = "Sil",
                tint = Color(0xFFEF4444),
                onClick = {
                    onDismissRequest()
                    onDeleteClick()
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun OptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        )
    }
}
