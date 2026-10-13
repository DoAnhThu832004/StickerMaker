package com.thupo.presentation.screens.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thupo.domain.usecase.CompleteIntroUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val completeIntroUseCase: CompleteIntroUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(IntroUiState())
    val uiState: StateFlow<IntroUiState> = _uiState.asStateFlow()

    private val _effectChannel = Channel<IntroUiEffect>(Channel.BUFFERED)
    val effectFlow = _effectChannel.receiveAsFlow()

    fun onEvent(event: IntroUiEvent) {
        when (event) {
            is IntroUiEvent.PageChanged -> {
                _uiState.update { it.copy(currentPageIndex = event.index) }
            }
            is IntroUiEvent.CompleteIntro -> {
                viewModelScope.launch {
                    // Gọi UseCase lưu cờ vào DataStore/SharedPreferences
                    completeIntroUseCase()
                    // Gửi Effect chuyển màn hình về Home
                    _effectChannel.send(IntroUiEffect.NavigateToHome)
                }
            }
        }
    }
}