package com.thupo.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thupo.domain.model.StickerPack
import com.thupo.presentation.screens.home.components.CreatePackDialog
import com.thupo.presentation.screens.home.components.RadialFloatingActionButton
import com.thupo.presentation.screens.home.components.StickerPackCard
import com.thupo.presentation.screens.home.components.StickerTitle
import com.thupo.presentation.screens.intro.components.AuroraGlow
import com.thupo.presentation.screens.intro.components.NavyDark
import com.thupo.presentation.screens.intro.components.NavyMid

private const val CONTENT_TYPE_STICKER_PACK = "sticker_pack_item"
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToPackDetail: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onNavigateToPackDetail = onNavigateToPackDetail,
        modifier = modifier
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    uiState: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    onNavigateToPackDetail: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundBrush = remember {
        Brush.verticalGradient(listOf(NavyDark, NavyMid, NavyDark))
    }
    val scrimInteractionSource = remember { MutableInteractionSource() }
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    StickerTitle(
                        text = "STICKER MAKER",
                        textSizeDp = 19.dp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            RadialFloatingActionButton(
                modifier = Modifier.offset(y = 70.dp),
                isExpanded = uiState.isSpeedDialExpanded,
                onToggle = { onEvent(HomeUiEvent.OnToggleSpeedDial(it)) },
                onSelectAnimated = { onEvent(HomeUiEvent.OnSelectStickerType(StickerType.ANIMATED)) },
                onSelectStatic = { onEvent(HomeUiEvent.OnSelectStickerType(StickerType.REGULAR)) },
                onSelectMyStickers = { onEvent(HomeUiEvent.OnSelectMyStickers) },
                onSelectExplore = { onEvent(HomeUiEvent.OnSelectExplore) }
            )
        },
    ) { innerPadding ->
        AuroraGlow()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.stickerPacks.isEmpty() && !uiState.isLoading) {
                EmptyStickerView(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = uiState.stickerPacks,
                        key = { it.id },
                        contentType = { CONTENT_TYPE_STICKER_PACK }
                    ) { pack ->
                        val context = LocalContext.current
                        val onPackClick = remember(pack.id, onNavigateToPackDetail) {
                            { onNavigateToPackDetail(pack.id) }
                        }
                        val onAddToWhatsAppClick = remember(pack, context) {
                            { com.thupo.core.whatsapp.WhatsAppStickerHelper.addToWhatsApp(context, pack) }
                        }

                        StickerPackCard(
                            pack = pack,
                            onPackClick = onPackClick,
                            onAddToWhatsAppClick = onAddToWhatsAppClick
                        )
                    }
                }
            }
            if (uiState.isSpeedDialExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f))
                        .clickable(
                            interactionSource = scrimInteractionSource,
                            indication = null
                        ) {
                            onEvent(HomeUiEvent.OnToggleSpeedDial(false))
                        }
                )
            }
        }
    }

    if (uiState.isCreatePackDialogVisible) {
        CreatePackDialog(
            onDismiss = { onEvent(HomeUiEvent.OnDismissCreateDialog) },
            onConfirm = { name ->
                onEvent(HomeUiEvent.OnConfirmCreatePack(name) { newPackId ->
                    onNavigateToPackDetail(newPackId)
                })
            }
        )
    }
}

@Composable
private fun EmptyStickerView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
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
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val samplePacks = remember {
        listOf(
            StickerPack(id = 1, name = "Sticker Chó Cưng", stickers = listOf("", "", ""), isAnimated = false),
            StickerPack(id = 2, name = "Meme Vui Vẻ", stickers = listOf("", ""), isAnimated = true)
        )
    }
    MaterialTheme {
        HomeContent(
            uiState = HomeUiState(stickerPacks = samplePacks),
            onEvent = {},
            onNavigateToPackDetail = {}
        )
    }
}