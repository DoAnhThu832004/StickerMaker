package com.thupo.presentation.screens.splash.model

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D

class LetterAnimState(startBlurPx: Float, startOffsetYPx: Float) {
    val offsetX = Animatable(0f)
    val offsetY = Animatable(startOffsetYPx)
    val rotation = Animatable(0f)
    val scale = Animatable(0.3f)
    val alpha = Animatable(0f)
    val blur = Animatable(startBlurPx)
}