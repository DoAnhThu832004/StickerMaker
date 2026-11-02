package com.thupo.presentation.screens.pack_detail

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.thupo.core.whatsapp.WhatsAppStickerHelper
import com.thupo.presentation.screens.intro.components.AuroraGlow
import com.thupo.presentation.screens.intro.components.NavyDark
import com.thupo.presentation.screens.intro.components.NavyMid
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PackDetailScreen(
    viewModel: PackDetailViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val pack by viewModel.pack.collectAsStateWithLifecycle()
    val isProcessing by viewModel.isProcessing.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Launcher chọn ảnh từ máy để thêm vào pack
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.addSticker(it.toString()) }
    }

    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var editingSticker by remember { mutableStateOf<com.thupo.domain.model.StickerItem?>(null) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NavyDark, NavyMid, NavyDark))),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = pack?.name ?: "Chi tiết bộ sticker",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Xóa bộ",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            pack?.let { currentPack ->
                Surface(
                    color = NavyDark.copy(alpha = 0.95f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .navigationBarsPadding()
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    WhatsAppStickerHelper.addToWhatsApp(context, currentPack)
                                }
                            },
                            enabled = currentPack.canAddToWhatsApp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF25D366),
                                disabledContainerColor = Color.White.copy(alpha = 0.12f),
                                contentColor = Color.White,
                                disabledContentColor = Color.White.copy(alpha = 0.4f)
                            )
                        ) {
                            Text(
                                text = if (currentPack.canAddToWhatsApp) "Thêm vào WhatsApp"
                                else "Cần ít nhất 3 sticker (${currentPack.stickers.size}/3)",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        AuroraGlow()
        pack?.let { currentPack ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Ô nút Thêm Sticker (+)
                item {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Thêm Sticker",
                                tint = Color(0xFFB7A4FF),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Thêm ảnh",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // Danh sách các sticker đã thêm
                items(currentPack.stickers, key = { it.filePath }) { item ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                    ) {
                        AsyncImage(
                            model = item.filePath,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { viewModel.removeSticker(item.filePath) },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(32.dp)
                                .padding(4.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Xóa",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        // Badge Emoji ở góc dưới bên trái
                        Surface(
                            color = Color.Black.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(topEnd = 8.dp, bottomStart = 14.dp),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .clickable { editingSticker = item }
                        ) {
                            Text(
                                text = item.emojis.joinToString(" "),
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Xác nhận xóa") },
            text = { Text("Bạn có chắc chắn muốn xóa bộ sticker này không?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmation = false
                        viewModel.deletePack(onBack)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Hủy")
                }
            }
        )
    }
    // Hiển thị vòng xoay Loading khi AI đang tách nền
    if (isProcessing) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { /* Không cho người dùng bấm ra ngoài để tắt trong lúc đang xử lý */ }
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFF1E293B).copy(alpha = 0.9f)), // Màu tối mờ sang trọng
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF6C5CE7), // Màu tím gradient của app
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(42.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "✨ Đang tạo sticker...",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // Dialog chọn nhanh Emoji
    editingSticker?.let { sticker ->
        var currentEmojis by remember(sticker) { mutableStateOf(sticker.emojis) }
        val quickSuggestions = listOf("😂", "❤️", "😭", "😡", "👍", "🎉", "🔥", "✨", "🐱", "🐶")

        AlertDialog(
            onDismissRequest = { editingSticker = null },
            title = { Text("Chỉnh sửa Emoji", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "WhatsApp sẽ gợi ý sticker này khi bạn gõ các emoji bên dưới (tối đa 3):",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Đang chọn: ${if (currentEmojis.isEmpty()) "(Trống)" else currentEmojis.joinToString("  ")}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6C5CE7)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Chọn nhanh:", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickSuggestions.take(5).forEach { emoji ->
                            AssistChip(
                                onClick = {
                                    if (currentEmojis.size < 3 && !currentEmojis.contains(emoji)) {
                                        currentEmojis = currentEmojis + emoji
                                    }
                                },
                                label = { Text(emoji, fontSize = 16.sp) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        quickSuggestions.drop(5).forEach { emoji ->
                            AssistChip(
                                onClick = {
                                    if (currentEmojis.size < 3 && !currentEmojis.contains(emoji)) {
                                        currentEmojis = currentEmojis + emoji
                                    }
                                },
                                label = { Text(emoji, fontSize = 16.sp) }
                            )
                        }
                    }

                    if (currentEmojis.isNotEmpty()) {
                        TextButton(
                            onClick = { currentEmojis = emptyList() },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Xóa hết", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val finalEmojis = if (currentEmojis.isEmpty()) listOf("✨") else currentEmojis
                    viewModel.updateEmojis(sticker.filePath, finalEmojis)
                    editingSticker = null
                }) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingSticker = null }) {
                    Text("Hủy")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PackDetailScreenPreview() {
    MaterialTheme {
        Scaffold { padding ->
            Box(modifier = Modifier.padding(padding))
        }
    }
}
