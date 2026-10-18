package com.thupo.presentation.screens.home

import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thupo.domain.repository.StickerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val stickerRepository: StickerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState())
    val uiState : StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadStickerPacks()
    }
    private fun loadStickerPacks() {
        viewModelScope.launch {
            stickerRepository.getAllPacks().collect { packs ->
                _uiState.update { it.copy(stickerPacks = packs, isLoading = false) }
            }
        }
    }
    fun onEvent(event: HomeUiEvent) {
        when(event) {
            is HomeUiEvent.OnToggleSpeedDial -> {
                _uiState.update { it.copy(isSpeedDialExpanded = event.isExpanded) }
            }
            is HomeUiEvent.OnSelectStickerType -> {
                _uiState.update {
                    it.copy(
                        selectedStickerType = event.type,
                        isSpeedDialExpanded = false,
                        isCreatePackDialogVisible = true
                    )
                }
            }

            HomeUiEvent.OnDismissCreateDialog -> {
                _uiState.update { it.copy(isCreatePackDialogVisible = false) }
            }
            is HomeUiEvent.OnConfirmCreatePack -> {
                createPack(event.packName, event.onSuccess)
            }
            HomeUiEvent.OnSelectMyStickers -> {
                _uiState.update { it.copy(isSpeedDialExpanded = false) }
            }
            HomeUiEvent.OnSelectExplore -> {
                _uiState.update { it.copy(isSpeedDialExpanded = false) }
            }
        }
    }
    private fun createPack(name: String, onSuccess: (Long) -> Unit) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val isAnimated = _uiState.value.selectedStickerType == StickerType.ANIMATED
            val newId = stickerRepository.createPack(name.trim(), isAnimated)
            _uiState.update { it.copy(isCreatePackDialogVisible = false) }
            onSuccess(newId)
        }
    }
}