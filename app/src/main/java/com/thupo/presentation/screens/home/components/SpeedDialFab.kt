// Lucian UX/UI Prompt #011 · MIT License (c) 2026 Seokwoooo · https://lucianlabs.dev/uxui-prompt/#011
package com.thupo.presentation.screens.home.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.thupo.presentation.screens.home.StickerType

/**
 * SpeedDialFab replaced by RadialFloatingActionButton following Lucian UX/UI Prompt #011.
 * Retains full backward compatibility with existing parameters.
 */
@Composable
fun SpeedDialFab(
    isExpanded: Boolean,
    onToggle: (Boolean) -> Unit,
    onSelectType: (StickerType) -> Unit,
    modifier: Modifier = Modifier,
    onSelectMyStickers: () -> Unit = {},
    onSelectExplore: () -> Unit = {}
) {
    RadialFloatingActionButton(
        isExpanded = isExpanded,
        onToggle = onToggle,
        modifier = modifier,
        onSelectAnimated = { onSelectType(StickerType.ANIMATED) },
        onSelectStatic = { onSelectType(StickerType.REGULAR) },
        onSelectMyStickers = onSelectMyStickers,
        onSelectExplore = onSelectExplore
    )
}