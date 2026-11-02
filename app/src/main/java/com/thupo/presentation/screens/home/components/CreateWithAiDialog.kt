package com.thupo.presentation.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.thupo.core.ai.StickerArtStyle
import com.thupo.domain.usecase.AiGenerationProgress
import com.thupo.domain.usecase.GenerateAiPackUseCase
import com.thupo.presentation.screens.intro.components.GlassCard
import kotlinx.coroutines.launch

@Composable
fun CreateWithAiDialog(
    useCase: GenerateAiPackUseCase,
    onDismiss: () -> Unit,
    onSuccess: (Long) -> Unit
) {
    var prompt by remember { mutableStateOf("") }
    var packName by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf(StickerArtStyle.CHIBI_3D) }
    var customApiKey by remember { mutableStateOf("") }
    var showApiKeyInput by remember { mutableStateOf(false) }

    var progressState by remember { mutableStateOf<AiGenerationProgress?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val isGenerating = progressState != null && progressState !is AiGenerationProgress.Failure

    Dialog(onDismissRequest = { if (!isGenerating) onDismiss() }) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF6C5CE7).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFB7A4FF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Tạo Sticker bằng AI",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    if (!isGenerating) {
                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng", tint = Color.White.copy(alpha = 0.6f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nếu đang trong quá trình sinh ảnh -> Hiển thị màn hình tiến trình
                if (progressState != null) {
                    GenerationProgressView(
                        state = progressState!!,
                        errorMessage = errorMessage,
                        onRetry = {
                            progressState = null
                            errorMessage = null
                        }
                    )
                } else {
                    // Form nhập liệu
                    Text(
                        text = "Mô tả ý tưởng nhân vật của bạn:",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = prompt,
                        onValueChange = {
                            prompt = it
                            if (packName.isBlank() && it.isNotBlank()) {
                                packName = "AI: ${it.take(15).trim()}"
                            }
                        },
                        placeholder = {
                            Text("Ví dụ: Chú mèo béo mặc áo phi hành gia", color = Color.White.copy(alpha = 0.35f), fontSize = 13.sp)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF6C5CE7),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Tên bộ Sticker:",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = packName,
                        onValueChange = { packName = it },
                        placeholder = {
                            Text("Ví dụ: Mèo phi hành gia", color = Color.White.copy(alpha = 0.35f), fontSize = 13.sp)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF6C5CE7),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Phong cách nghệ thuật:",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Lưới Style Chips
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        StickerArtStyle.entries.chunked(2).forEach { rowStyles ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowStyles.forEach { style ->
                                    val isSelected = selectedStyle == style
                                    Surface(
                                        color = if (isSelected) Color(0xFF6C5CE7) else Color.White.copy(alpha = 0.08f),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedStyle = style }
                                    ) {
                                        Text(
                                            text = style.displayName,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tùy chọn cấu hình API Key (nếu chưa có trong local.properties)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showApiKeyInput = !showApiKeyInput },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (showApiKeyInput) "Ẩn tùy chỉnh API Key" else "Tùy chỉnh Gemini API Key",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }

                    AnimatedVisibility(visible = showApiKeyInput) {
                        Column {
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = customApiKey,
                                onValueChange = { customApiKey = it },
                                placeholder = { Text("Dán API Key (AIzaSy...)", color = Color.White.copy(alpha = 0.35f), fontSize = 12.sp) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = Color(0xFF6C5CE7),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Nút bấm Tạo ngay
                    Button(
                        onClick = {
                            if (prompt.isNotBlank()) {
                                scope.launch {
                                    errorMessage = null
                                    useCase.execute(
                                        packName = packName.ifBlank { "AI Pack" },
                                        userIdea = prompt,
                                        style = selectedStyle,
                                        rows = 2,
                                        cols = 2,
                                        customApiKey = customApiKey.takeIf { it.isNotBlank() }
                                    ).collect { progress ->
                                        progressState = progress
                                        if (progress is AiGenerationProgress.Success) {
                                            onSuccess(progress.packId)
                                        } else if (progress is AiGenerationProgress.Failure) {
                                            errorMessage = progress.error
                                        }
                                    }
                                }
                            }
                        },
                        enabled = prompt.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6C5CE7),
                            disabledContainerColor = Color.White.copy(alpha = 0.1f)
                        )
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tạo 4 Sticker bằng AI", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun GenerationProgressView(
    state: AiGenerationProgress,
    errorMessage: String?,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (errorMessage != null) {
            Text(text = "❌ Có lỗi xảy ra", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Thử lại")
            }
        } else {
            CircularProgressIndicator(
                color = Color(0xFF6C5CE7),
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            val statusText = when (state) {
                AiGenerationProgress.ConnectingApi -> "✨ 1/4: Đang gửi mô tả tới Google Imagen 3..."
                AiGenerationProgress.SlicingGrid -> "📐 2/4: Đang cắt ảnh Sprite Sheet..."
                is AiGenerationProgress.ProcessingItem -> "🪄 3/4: Đang bóc nền & bo viền (${state.index}/${state.total})..."
                is AiGenerationProgress.Success -> "🎉 Hoàn tất! Đang mở bộ sticker..."
                else -> "Đang khởi tạo..."
            }

            Text(
                text = statusText,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Quá trình này mất khoảng 5–8 giây",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }
    }
}
