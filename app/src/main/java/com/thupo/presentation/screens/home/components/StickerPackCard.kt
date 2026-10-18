package com.thupo.presentation.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.thupo.domain.model.StickerPack
import com.thupo.presentation.screens.intro.components.GlassCard

@Composable
fun StickerPackCard(
    pack: StickerPack,
    onPackClick: () -> Unit,
    onAddToWhatsAppClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(20.dp)
    GlassCard(
        shape = cardShape,
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .clickable { onPackClick() }
    )  {
        Column(modifier = Modifier.padding(14.dp)) {
            // Tên pack và badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pack.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                if (pack.isAnimated) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "GIF",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Grid preview tối đa 4 sticker mẫu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (i in 0 until 4) {
                    val uri = pack.stickers.getOrNull(i)
                    val slotShape = RoundedCornerShape(12.dp)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(slotShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), slotShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uri != null) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                            )
                        } else {
                            Text(text = "➕", fontSize = 14.sp, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Số lượng sticker + Nút Thêm vào WhatsApp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${pack.stickers.size}/30 stickers",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )

                Button(
                    onClick = onAddToWhatsAppClick,
                    enabled = pack.canAddToWhatsApp,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF25D366), // Màu xanh đặc trưng của WhatsApp
                        disabledContainerColor = Color.White.copy(alpha = 0.12f),
                        contentColor = Color.White,
                        disabledContentColor = Color.White.copy(alpha = 0.4f)
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text(
                        text = if (pack.canAddToWhatsApp) "Thêm vào WA" else "Cần ít nhất 3",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
@Preview
@Composable
fun StickerPackCardPreView() {
    StickerPackCard(
        pack = StickerPack(
            id = 1,
            name = "Sticker Chó Cưng",
            stickers = listOf("", "", ""),
            isAnimated = false
        ),
        onPackClick = {},
        onAddToWhatsAppClick = {}
    )
}