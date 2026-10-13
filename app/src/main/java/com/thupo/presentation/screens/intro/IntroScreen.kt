package com.thupo.presentation.screens.intro


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thupo.presentation.screens.intro.components.*
import com.thupo.presentation.screens.intro.components.heros.FloatingTilesHero
import com.thupo.presentation.screens.intro.components.heros.PackStackHero
import com.thupo.presentation.screens.intro.components.heros.PlatformsHero
import com.thupo.presentation.screens.intro.model.IntroHero
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroScreen(
    viewModel: IntroViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { uiState.pages.size })
    val scope = rememberCoroutineScope()
    val isLast = pagerState.currentPage == uiState.pages.lastIndex

    // Lắng nghe khi ViewModel kích hoạt chuyển màn hình
    LaunchedEffect(Unit) {
        viewModel.effectFlow.collect { effect ->
            when (effect) {
                is IntroUiEffect.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    // Cập nhật trang hiện tại vào ViewModel
    LaunchedEffect(pagerState.currentPage) {
        viewModel.onEvent(IntroUiEvent.PageChanged(pagerState.currentPage))
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(NavyDark, NavyMid, NavyDark)))
    ) {
        AuroraGlow()

        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { index ->
            val page = uiState.pages[index]
            Column(Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .statusBarsPadding(),
                    contentAlignment = Alignment.Center
                ) {
                    when (page.hero) {
                        IntroHero.FLOATING_TILES -> FloatingTilesHero()
                        IntroHero.PACK_STACK -> PackStackHero()
                        IntroHero.PLATFORMS -> PlatformsHero()
                    }
                }
                CurvedBottomPanel(page = page)
            }
        }

        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PagerIndicator(count = uiState.pages.size, current = pagerState.currentPage)
            Spacer(Modifier.height(8.dp))
            IntroButton(
                text = if (isLast) "Bắt đầu" else "Tiếp tục",
                onClick = {
                    if (isLast) {
                        viewModel.onEvent(IntroUiEvent.CompleteIntro)
                    } else {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    }
                }
            )
        }
    }
}