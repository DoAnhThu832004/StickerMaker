package com.thupo.presentation.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.thupo.presentation.screens.intro.components.AuroraGlow
import com.thupo.presentation.screens.splash.components.StickerTextSplash

@Composable
fun SplashDestinationTransition(
    modifier: Modifier = Modifier,
    isIntroDestination: Boolean = true,
    onFinished: () -> Unit = {},
    destinationContent: @Composable () -> Unit,
) {
    var exitProgress by remember { mutableFloatStateOf(0f) }
    var splashVisible by remember { mutableStateOf(true) }

    Box(modifier = modifier.fillMaxSize().clipToBounds()) {

        if (isIntroDestination) {
            AuroraGlow()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val p = exitProgress
                    // Bắt đầu hiện rõ dần từ 25% tiến trình exit trở đi
                    alpha = ((p - 0.25f) / 0.75f).coerceIn(0f, 1f)
                    // Thu nhỏ nhẹ từ 1.1x về 1.0x tạo chiều sâu không gian 3D
                    val s = 1.1f - 0.1f * p
                    scaleX = s
                    scaleY = s
                }
        ) {
            destinationContent()
        }

        if (splashVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = if (isIntroDestination) 1f else (1f - exitProgress * 1.6f).coerceIn(0f, 1f)
                    }
            ) {
                StickerTextSplash(
                    onExitProgressChanged = { progress ->
                        exitProgress = progress
                    },
                    onAnimationEnd = {
                        splashVisible = false
                        onFinished()
                    }
                )
            }
        }
    }
}
