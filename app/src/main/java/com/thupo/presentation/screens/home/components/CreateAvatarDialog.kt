package com.thupo.presentation.screens.home.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.thupo.core.ai.StickerArtStyle
import com.thupo.domain.usecase.AvatarGenerationProgress
import com.thupo.domain.usecase.GenerateAvatarPackUseCase
import com.thupo.presentation.screens.intro.components.GlassCard
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun CreateAvatarDialog(
    useCase: GenerateAvatarPackUseCase,
    onDismiss: () -> Unit,
    onSuccess: (Long) -> Unit
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var packName by remember { mutableStateOf("Chibi Avatar") }
    var selectedStyle by remember { mutableStateOf(StickerArtStyle.CHIBI_3D) }

    var progressState by remember { mutableStateOf<AvatarGenerationProgress?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val isGenerating = progressState != null && progressState !is AvatarGenerationProgress.Failure

    // Setup Camera FileProvider URI
    val tempCameraUri = remember {
        val tempFile = File(context.cacheDir, "camera_selfie.jpg")
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", tempFile)
    }

    // Launcher Camera
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            selectedImageUri = tempCameraUri
        }
    }

    // Launcher Thư viện ảnh
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

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
                                .background(Color(0xFFE056FD).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFFF368E0),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Tạo Avatar Chibi từ Ảnh",
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

                // Nếu đang sinh Avatar -> Hiển thị màn hình tiến trình
                if (progressState != null) {
                    AvatarProgressView(
                        state = progressState!!,
                        errorMessage = errorMessage,
                        onRetry = {
                            progressState = null
                            errorMessage = null
                        }
                    )
                } else {
                    // Chọn hoặc hiển thị ảnh chụp
                    if (selectedImageUri == null) {
                        Text(
                            text = "Chụp selfie hoặc chọn ảnh chụp rõ mặt (hoặc thú cưng):",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Nút Camera
                            Surface(
                                color = Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(90.dp)
                                    .clickable { cameraLauncher.launch(tempCameraUri) }
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color(0xFFF368E0), modifier = Modifier.size(28.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Chụp Selfie", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Medium)
                                }
                            }

                            // Nút Thư viện
                            Surface(
                                color = Color.White.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(90.dp)
                                    .clickable { galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = Color(0xFF6C5CE7), modifier = Modifier.size(28.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Chọn từ Thư viện", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    } else {
                        // Xem trước ảnh đã chọn
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.3f))
                        ) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Nút đổi ảnh
                            Surface(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                                    .clickable { selectedImageUri = null }
                            ) {
                                Text(
                                    text = "Đổi ảnh khác",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Tên bộ Avatar:", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = packName,
                            onValueChange = { packName = it },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFE056FD),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text("Chọn phong cách Chibi:", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
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
                                            color = if (isSelected) Color(0xFFE056FD) else Color.White.copy(alpha = 0.08f),
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

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                val uri = selectedImageUri ?: return@Button
                                scope.launch {
                                    errorMessage = null
                                    useCase.execute(
                                        imageUri = uri,
                                        packName = packName.ifBlank { "Chibi Avatar" },
                                        style = selectedStyle
                                    ).collect { progress ->
                                        progressState = progress
                                        if (progress is AvatarGenerationProgress.Success) {
                                            onSuccess(progress.packId)
                                        } else if (progress is AvatarGenerationProgress.Failure) {
                                            errorMessage = progress.error
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE056FD))
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Biến thành 4 Sticker Avatar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AvatarProgressView(
    state: AvatarGenerationProgress,
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
                color = Color(0xFFE056FD),
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            val statusText = when (state) {
                AvatarGenerationProgress.CroppingFace -> "📐 1/4: Đang phân tích góc mặt & crop ảnh..."
                AvatarGenerationProgress.AnalyzingFeatures -> "🧠 2/4: AI đang trích xuất đặc trưng nhận diện..."
                AvatarGenerationProgress.GeneratingArt -> "🎨 3/4: Đang phác thảo bộ 4 Sticker Chibi Avatar..."
                is AvatarGenerationProgress.ProcessingItem -> "🪄 4/4: Đang bóc nền & bo viền (${state.index}/${state.total})..."
                is AvatarGenerationProgress.Success -> "🎉 Hoàn tất! Đang mở bộ Avatar..."
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
                text = "Quá trình này mất khoảng 6–8 giây",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 11.sp
            )
        }
    }
}
