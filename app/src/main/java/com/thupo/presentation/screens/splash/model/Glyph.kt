package com.thupo.presentation.screens.splash.model

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path

/** Path của 1 chữ, đã dời về tâm (0,0) để xoay/scale quanh đúng tâm chữ. */
data class Glyph(
    val char: Char,
    val path: Path,
    val size: Size
)