package com.thupo.presentation.screens.intro

import androidx.compose.runtime.Immutable
import com.thupo.presentation.screens.intro.model.IntroPage
import com.thupo.presentation.screens.intro.model.defaultIntroPages

@Immutable
data class IntroUiState(
    val pages: List<IntroPage> = defaultIntroPages,
    val currentPageIndex: Int = 0
)

sealed interface IntroUiEvent {
    data class PageChanged(val index: Int) : IntroUiEvent
    data object CompleteIntro : IntroUiEvent
}

sealed interface IntroUiEffect {
    data object NavigateToHome : IntroUiEffect
}