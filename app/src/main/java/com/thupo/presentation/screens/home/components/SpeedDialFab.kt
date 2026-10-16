package com.thupo.presentation.screens.home.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.thupo.presentation.screens.home.StickerType

@Composable
fun SpeedDialFab(
    isExpanded: Boolean,
    onToggle: (Boolean) -> Unit,
    onSelectType: (StickerType) -> Unit,
    modifier: Modifier = Modifier
) {
//    Column(
//        modifier = modifier,
//        horizontalAlignment = Alignment.End,
//        verticalArrangement = Arrangement.spacedBy(12.dp)
//    ) {
//        AnimatedVisibility(
//            visible = isExpanded,
//            enter = fadeIn() + slideInVertically { it / 2 },
//            exit = fadeOut() + slideOutVertically { it / 2 }
//        ) {
//            Column(
//                horizontalAlignment = Alignment.End,
//                verticalArrangement = Arrangement.spacedBy(10.dp)
//            ) {
//                ExtendedFloatingActionButton(
//                    text = { Text("Sticker Động (Video/GIF)") },
//                    icon = { Icon(Icons.Default.Animation, contentDescription = null) },
//                    onClick = { onSelectType(StickerType.ANIMATED) },
//                    containerColor = MaterialTheme.colorScheme.surface,
//                    contentColor = MaterialTheme.colorScheme.primary
//                )
//                ExtendedFloatingActionButton(
//                    text = { Text("Sticker Tĩnh (Ảnh)") },
//                    icon = { Icon(Icons.Default.Image, contentDescription = null) },
//                    onClick = { onSelectType(StickerType.REGULAR) },
//                    containerColor = MaterialTheme.colorScheme.surface,
//                    contentColor = MaterialTheme.colorScheme.primary
//                )
//            }
//        }
//
//        // Nút chính (+)
//        FloatingActionButton(
//            onClick = { onToggle(!isExpanded) },
//            containerColor = MaterialTheme.colorScheme.primary,
//            contentColor = MaterialTheme.colorScheme.onPrimary
//        ) {
//            Icon(
//                imageVector = Icons.Default.Add,
//                contentDescription = "Tạo pack mới",
//                modifier = Modifier.rotate(if (isExpanded) 45f else 0f)
//            )
//        }
//    }
}