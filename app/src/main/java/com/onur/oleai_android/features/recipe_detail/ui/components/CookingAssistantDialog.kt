package com.onur.oleai_android.features.recipe_detail.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.RestaurantMenu
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.onur.oleai_android.R
import com.onur.oleai_android.core.network.dto.RecipeStepResponse

@Composable
fun CookingAssistantDialog(
    recipeTitle: String,
    steps: List<RecipeStepResponse>,
    onDismiss: () -> Unit,
    onFinish: () -> Unit
) {
    var currentStepIndex by remember { mutableIntStateOf(0) }
    val step = steps.getOrNull(currentStepIndex)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xE6000000)) // Semi-transparent black background
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E1E), shape = RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0xFF2C2C2C), shape = RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFF2C2C2C), shape = RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.RestaurantMenu,
                                contentDescription = null,
                                tint = Color(0xFF90B551),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Pişirme Asistanı",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = recipeTitle,
                                color = Color(0xFFAAAAAA),
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF2C2C2C), shape = RoundedCornerShape(16.dp))
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Kapat",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color(0xFF90B551), thickness = 2.dp)
                Spacer(modifier = Modifier.height(32.dp))

                // Content
                if (step != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF2C2C2C), shape = RoundedCornerShape(16.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Adım ${currentStepIndex + 1} / ${steps.size}",
                                color = Color(0xFF90B551),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = step.instruction,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        if (!step.imageUrl.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))
                            AsyncImage(
                                model = step.imageUrl,
                                contentDescription = "Step Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                        }
                    }
                } else {
                    Text("Adım bulunamadı.", color = Color.White)
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { if (currentStepIndex > 0) currentStepIndex-- },
                        enabled = currentStepIndex > 0,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C2C2C),
                            disabledContainerColor = Color(0xFF2C2C2C).copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("< Geri", color = if (currentStepIndex > 0) Color.White else Color.Gray)
                    }

                    Button(
                        onClick = {
                            if (currentStepIndex < steps.size - 1) {
                                currentStepIndex++
                            } else {
                                onFinish()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            if (currentStepIndex < steps.size - 1) "İleri >" else "Bitir",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
