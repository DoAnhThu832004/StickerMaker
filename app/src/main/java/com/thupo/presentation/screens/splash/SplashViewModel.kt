package com.thupo.presentation.screens.splash

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thupo.domain.usecase.PrepareInitialDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val prepareInitialDataUseCase: PrepareInitialDataUseCase,
): ViewModel() {
    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private val _effectChannel = Channel<SplashUiEffect>(Channel.BUFFERED)
    val effectFlow = _effectChannel.receiveAsFlow()

    private var isAnimationFinished = false
    private var targetNavigateToIntro: Boolean? = null

    init {
        startPreparation()
    }
    fun onEvent(event: SplashUiEvent) {
        when (event) {
            is SplashUiEvent.AnimationFinished -> {
                isAnimationFinished = true
                checkAndNavigate()
            }
            is SplashUiEvent.Retry -> {
                startPreparation()
            }
        }
    }

    private fun startPreparation() {
        viewModelScope.launch {
            _uiState.value = SplashUiState.Loading

            val result = withContext(Dispatchers.IO) {
                prepareInitialDataUseCase()
            }
            result.fold(
                onSuccess = { navigationToIntro ->
                    targetNavigateToIntro = navigationToIntro
                    checkAndNavigate() // Kiểm tra xem animation đã xong trước đó chưa
                },
                onFailure = { error ->
                    _uiState.value = SplashUiState.Error(error.message ?: "Đã xảy ra lỗi không xác định")
                }
            )
        }
    }
    private fun checkAndNavigate() {
        val toIntro = targetNavigateToIntro
        if (isAnimationFinished && toIntro != null) {
            viewModelScope.launch {
                _effectChannel.send(SplashUiEffect.NavigateNext(toIntro))
            }
        }
    }
}