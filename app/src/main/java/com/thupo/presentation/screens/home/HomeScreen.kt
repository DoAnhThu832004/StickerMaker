package com.thupo.presentation.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.thupo.domain.model.StickerPack
import com.thupo.presentation.screens.home.components.CreatePackDialog
import com.thupo.presentation.screens.home.components.SpeedDialFab
import com.thupo.presentation.screens.home.components.StickerPackCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToPackDetail: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sticker Maker", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButton = {
            SpeedDialFab(
                isExpanded = uiState.isSpeedDialExpanded,
                onToggle = { viewModel.onEvent(HomeUiEvent.OnToggleSpeedDial(it)) },
                onSelectType = { viewModel.onEvent(HomeUiEvent.OnSelectStickerType(it)) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.stickerPacks.isEmpty() && !uiState.isLoading) {
                // Trạng thái trống (Empty state)
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "📦", style = MaterialTheme.typography.displayLarge)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Chưa có bộ sticker nào",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Nhấn nút (+) bên dưới để bắt đầu tạo sticker đầu tiên!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            } else {
                // Danh sách Pack tối ưu với LazyVerticalGrid + item key
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = uiState.stickerPacks,
                        key = { it.id } // Item key ổn định giúp giảm recomposition
                    ) { pack ->
                        StickerPackCard(
                            pack = pack,
                            onPackClick = { onNavigateToPackDetail(pack.id) },
                            onAddToWhatsAppClick = { /* Luồng export WA ở phần 6 */ }
                        )
                    }
                }
            }
        }
    }

    if (uiState.isCreatePackDialogVisible) {
        CreatePackDialog(
            onDismiss = { viewModel.onEvent(HomeUiEvent.OnDismissCreateDialog) },
            onConfirm = { name ->
                viewModel.onEvent(HomeUiEvent.OnConfirmCreatePack(name) { newPackId ->
                    onNavigateToPackDetail(newPackId)
                })
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val samplePacks = listOf(
        StickerPack(
            id = 1,
            name = "Sticker Chó Cưng",
            stickers = listOf("", "", ""),
            isAnimated = false
        ),
        StickerPack(id = 2, name = "Meme Vui Vẻ", stickers = listOf("", ""), isAnimated = true)
    )
    MaterialTheme {
        Scaffold { padding ->
            Box(modifier = Modifier.padding(padding)) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(samplePacks) {
                        StickerPackCard(pack = it, onPackClick = {}, onAddToWhatsAppClick = {})
                    }
                }
            }
        }
    }
}