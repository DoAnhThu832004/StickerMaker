package com.thupo.presentation.screens.splash

import androidx.compose.runtime.Immutable

@Immutable
sealed interface SplashUiState {
    data object Loading: SplashUiState
    data class Error(val errorMessage: String): SplashUiState
}
sealed interface SplashUiEffect {
    data class NavigateNext(val navigationToIntro: Boolean): SplashUiEffect
}
sealed interface SplashUiEvent {
    data object AnimationFinished : SplashUiEvent
    data object Retry : SplashUiEvent
}