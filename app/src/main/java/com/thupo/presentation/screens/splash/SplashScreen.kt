package com.thupo.presentation.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thupo.presentation.screens.intro.AuroraGlow
import com.thupo.presentation.screens.splash.components.StickerTextSplash
import com.thupo.presentation.screens.splash.config.SplashConfig

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToIntro: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.effectFlow.collect { effect ->
            when (effect) {
                is SplashUiEffect.NavigateNext -> {
                    if (effect.navigationToIntro) {
                        onNavigateToIntro()
                    } else {
                        onNavigateToHome()
                    }
                }
            }
        }
    }
    SplashContent(
        uiState = uiState,
        onFishedAnimation = {
            viewModel.onEvent(SplashUiEvent.AnimationFinished)
        },
        onRetry = {
            viewModel.onEvent(SplashUiEvent.Retry)
        }
    )
}
@Composable
fun SplashContent(
    uiState: SplashUiState,
    onFishedAnimation: () -> Unit,
    onRetry: () -> Unit,
) {
    when (val state = uiState) {
        is SplashUiState.Loading -> {
            StickerTextSplash(
                onAnimationEnd = {
                    onFishedAnimation()
                }
            )
        }
        is SplashUiState.Error -> {
            // Màn hình hiển thị lỗi nếu UseCase nạp dữ liệu thất bại
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = state.errorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { onRetry() }) {
                        Text(text = "Thử lại")
                    }
                }
            }
        }
    }
}