package com.thupo.presentation.screens.pack_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thupo.domain.model.StickerPack
import com.thupo.domain.repository.StickerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PackDetailViewModel @Inject constructor(
    private val repository: StickerRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val packId: Long = when (val id = savedStateHandle.get<Any>("packId")) {
        is Long -> id
        is String -> id.toLong()
        is Number -> id.toLong()
        else -> error("packId is required")
    }

    val pack: StateFlow<StickerPack?> = repository.getPackById(packId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun addSticker(uri: String) {
        viewModelScope.launch {
            repository.addStickerToPack(packId, uri)
        }
    }

    fun removeSticker(uri: String) {
        viewModelScope.launch {
            repository.removeStickerFromPack(packId, uri)
        }
    }

    fun deletePack(onDeleted: () -> Unit) {
        val currentPack = pack.value ?: return
        viewModelScope.launch {
            repository.deletePack(currentPack)
            onDeleted()
        }
    }
}
