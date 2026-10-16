package com.thupo.presentation.screens.home

import com.thupo.domain.model.StickerPack

enum class StickerType { REGULAR, ANIMATED }

data class HomeUiState(
    val stickerPacks: List<StickerPack> = emptyList(),
    val isLoading: Boolean = false,
    val isCreatePackDialogVisible: Boolean = false,
    val isSpeedDialExpanded: Boolean = false,
    val selectedStickerType: StickerType = StickerType.REGULAR
)

sealed interface HomeUiEvent {
    data class OnToggleSpeedDial(val isExpanded: Boolean) : HomeUiEvent
    data class OnSelectStickerType(val type: StickerType) : HomeUiEvent
    data object OnDismissCreateDialog : HomeUiEvent
    data class OnConfirmCreatePack(val packName: String, val onSuccess: (Long) -> Unit) : HomeUiEvent
    data object OnSelectMyStickers : HomeUiEvent
    data object OnSelectExplore : HomeUiEvent
}