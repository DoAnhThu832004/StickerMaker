package com.thupo.presentation.screens.home.components

import android.provider.Settings
import androidx.activity.compose.BackHandler
import com.thupo.presentation.screens.intro.components.GlassCard
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thupo.R
import com.thupo.presentation.screens.home.StickerType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Color tokens mapping --ux-* from Lucian UX/UI Prompt #011.
 */
data class RadialFabColors(
    val page: Color,
    val surface: Color,
    val ink: Color,
    val ink2: Color,
    val accent: Color,
    val mint: Color,
    val shadow: Color
)

object RadialFabDefaults {
    val EaseCurve = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)
    val SpringCurve = CubicBezierEasing(0.34f, 1.45f, 0.64f, 1f)

    val LightColors = RadialFabColors(
        page = Color(0xFFF4F2EE),
        surface = Color(0xFFFFFFFF),
        ink = Color(0xFF16151B),
        ink2 = Color(0xFF57535F),
        accent = Color(0xFF6A57F2),
        mint = Color(0xFF119C72),
        shadow = Color(0x24281E50)
    )

    val DarkColors = RadialFabColors(
        page = Color(0xFF07080D),
        surface = Color(0xFF13141C),
        ink = Color(0xFFF4F0E8),
        ink2 = Color(0xADF4F0E8),
        accent = Color(0xFFB7A4FF),
        mint = Color(0xFF7EE8C0),
        shadow = Color(0x73000000)
    )
}

/**
 * Data model for each radial item in Prompt #011.
 */
data class RadialFabItemConfig(
    val index: Int,
    val label: String,
    val doneMessage: String,
    val iconRes: Int,
    val gradientColors: List<Color>,
    val targetX: Dp,
    val targetY: Dp,
    val onClick: () -> Unit
)

@Composable
fun isReducedMotionActive(): Boolean {
    val context = LocalContext.current
    return remember(context) {
        try {
            val resolver = context.contentResolver
            val transitionScale = Settings.Global.getFloat(
                resolver,
                Settings.Global.TRANSITION_ANIMATION_SCALE,
                1f
            )
            val animatorScale = Settings.Global.getFloat(
                resolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f
            )
            transitionScale == 0f || animatorScale == 0f
        } catch (_: Exception) {
            false
        }
    }
}

/**
 * Radial floating action button featuring smooth fan animation, halo glow,
 * pulse breathing effect, and floating status note.
 */
@Composable
fun RadialFloatingActionButton(
    isExpanded: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onSelectAnimated: () -> Unit = {},
    onSelectStatic: () -> Unit = {},
    onSelectMyStickers: () -> Unit = {},
    onSelectExplore: () -> Unit = {},
    colors: RadialFabColors = if (isSystemInDarkTheme()) RadialFabDefaults.DarkColors else RadialFabDefaults.LightColors
) {
    val isReducedMotion = isReducedMotionActive()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Close when system back button is pressed if expanded
    BackHandler(enabled = isExpanded) {
        onToggle(false)
    }

    // State for picked item bounce & status toast
    var pickedIndex by remember { mutableStateOf<Int?>(null) }
    var noteMessage by remember { mutableStateOf("") }
    var isNoteVisible by remember { mutableStateOf(false) }

    val pickedScaleAnim = remember { Animatable(1f) }

    // 4 Items requested by the user:
    // 0: Sticker Động, 1: Sticker Tĩnh, 2: My Sticker, 3: Explore
    val items = remember(onSelectAnimated, onSelectStatic, onSelectMyStickers, onSelectExplore) {
        listOf(
            RadialFabItemConfig(
                index = 0,
                label = "Sticker Động",
                doneMessage = "Tạo Sticker Động (GIF/Video)",
                iconRes = R.drawable.ic_radial_animated,
                gradientColors = listOf(Color(0xFFFF8A65), Color(0xFFFF5A6E)),
                targetX = (-108).dp,
                targetY = (-30).dp,
                onClick = onSelectAnimated
            ),
            RadialFabItemConfig(
                index = 1,
                label = "Sticker Tĩnh",
                doneMessage = "Tạo Sticker Tĩnh từ ảnh",
                iconRes = R.drawable.ic_radial_static,
                gradientColors = listOf(Color(0xFF5B8CFF), Color(0xFF3D6BFF)),
                targetX = (-56).dp,
                targetY = (-98).dp,
                onClick = onSelectStatic
            ),
            RadialFabItemConfig(
                index = 2,
                label = "My Sticker",
                doneMessage = "Mở bộ sưu tập My Sticker",
                iconRes = R.drawable.ic_radial_my_stickers,
                gradientColors = listOf(Color(0xFF22C3A6), Color(0xFF119C72)),
                targetX = 56.dp,
                targetY = (-98).dp,
                onClick = onSelectMyStickers
            ),
            RadialFabItemConfig(
                index = 3,
                label = "Khám phá",
                doneMessage = "Khám phá Sticker có sẵn",
                iconRes = R.drawable.ic_radial_explore,
                gradientColors = listOf(Color(0xFFFFC24B), Color(0xFFF59E0B)),
                targetX = 108.dp,
                targetY = (-30).dp,
                onClick = onSelectExplore
            )
        )
    }

    // Motion variables:
    val dyOffset = 60.dp

    // FAB Translation Y: 600ms spring curve
    val fabOffsetY by animateDpAsState(
        targetValue = if (isExpanded) dyOffset else 0.dp,
        animationSpec = if (isReducedMotion) snap() else tween(600, easing = RadialFabDefaults.SpringCurve),
        label = "fabOffsetY"
    )

    // FAB Rotation: 0f -> 135f with 600ms spring curve
    val fabRotation by animateFloatAsState(
        targetValue = if (isExpanded) 135f else 0f,
        animationSpec = if (isReducedMotion) snap() else tween(600, easing = RadialFabDefaults.SpringCurve),
        label = "fabRotation"
    )

    // Halo animations
    val haloScale by animateFloatAsState(
        targetValue = if (isExpanded) 1.0f else 0.2f,
        animationSpec = if (isReducedMotion) snap() else tween(600, easing = RadialFabDefaults.EaseCurve),
        label = "haloScale"
    )
    val haloAlpha by animateFloatAsState(
        targetValue = if (isExpanded) 1.0f else 0f,
        animationSpec = if (isReducedMotion) snap() else tween(400, easing = RadialFabDefaults.EaseCurve),
        label = "haloAlpha"
    )

    // Breathing pulse for FAB when closed
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val breatheProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = RadialFabDefaults.EaseCurve),
            repeatMode = RepeatMode.Restart
        ),
        label = "breatheProgress"
    )

    // Toast Note animations
    val noteAlpha by animateFloatAsState(
        targetValue = if (isNoteVisible) 1f else 0f,
        animationSpec = if (isReducedMotion) snap() else tween(300, easing = RadialFabDefaults.EaseCurve),
        label = "noteAlpha"
    )
    val noteOffsetY by animateDpAsState(
        targetValue = if (isNoteVisible) 0.dp else 14.dp,
        animationSpec = if (isReducedMotion) snap() else tween(500, easing = RadialFabDefaults.SpringCurve),
        label = "noteOffsetY"
    )
    val noteScale by animateFloatAsState(
        targetValue = if (isNoteVisible) 1f else 0.9f,
        animationSpec = if (isReducedMotion) snap() else tween(500, easing = RadialFabDefaults.SpringCurve),
        label = "noteScale"
    )

    Box(
        modifier = modifier
            .size(width = 320.dp, height = 300.dp),
        contentAlignment = Alignment.Center
    ) {
        // --- 1. Halo Background ---
        if (haloAlpha > 0f) {
            Canvas(
                modifier = Modifier
                    .size(330.dp)
                    .offset(y = fabOffsetY)
                    .scale(haloScale)
                    .alpha(haloAlpha)
            ) {
                drawCircle(
                    brush = Brush.radialGradient(
                        0.0f to colors.accent.copy(alpha = 0.20f),
                        0.7f to colors.accent.copy(alpha = 0.06f),
                        1.0f to Color.Transparent,
                        center = center,
                        radius = size.minDimension / 2f
                    )
                )
            }
        }

        // --- 2. Floating Note (Toast result) with GlassCard ---
        if (noteAlpha > 0f) {
            GlassCard(
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 34.dp)
                    .offset(y = noteOffsetY)
                    .scale(noteScale)
                    .alpha(noteAlpha)
                    .shadow(elevation = 12.dp, shape = CircleShape, spotColor = colors.shadow)
                    .semantics {
                        liveRegion = LiveRegionMode.Polite
                    }
            ) {
                Row(
                    modifier = Modifier
                        .background(colors.ink.copy(alpha = 0.85f))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(colors.mint, CircleShape)
                    )
                    Text(
                        text = noteMessage,
                        color = colors.page,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // --- 3. Fan Items (4 Items) ---
        items.forEach { item ->
            val i = item.index
            val delayMillis = if (isReducedMotion) 0 else if (isExpanded) i * 45 else (3 - i) * 30

            val itemScale by animateFloatAsState(
                targetValue = if (isExpanded) 1.0f else 0.3f,
                animationSpec = if (isReducedMotion) snap() else tween(
                    durationMillis = 500,
                    delayMillis = delayMillis,
                    easing = RadialFabDefaults.SpringCurve
                ),
                label = "itemScale_$i"
            )

            val itemAlpha by animateFloatAsState(
                targetValue = if (isExpanded) 1.0f else 0f,
                animationSpec = if (isReducedMotion) snap() else tween(
                    durationMillis = 250,
                    delayMillis = delayMillis,
                    easing = RadialFabDefaults.EaseCurve
                ),
                label = "itemAlpha_$i"
            )

            val itemOffsetX by animateDpAsState(
                targetValue = if (isExpanded) item.targetX else 0.dp,
                animationSpec = if (isReducedMotion) snap() else tween(
                    durationMillis = 500,
                    delayMillis = delayMillis,
                    easing = RadialFabDefaults.SpringCurve
                ),
                label = "itemOffsetX_$i"
            )

            val itemOffsetY by animateDpAsState(
                targetValue = if (isExpanded) item.targetY + dyOffset else 0.dp,
                animationSpec = if (isReducedMotion) snap() else tween(
                    durationMillis = 600,
                    delayMillis = delayMillis,
                    easing = RadialFabDefaults.SpringCurve
                ),
                label = "itemOffsetY_$i"
            )

            val currentScale = if (pickedIndex == i) itemScale * pickedScaleAnim.value else itemScale

            if (itemAlpha > 0.01f || isExpanded) {
                Box(
                    modifier = Modifier
                        .offset(x = itemOffsetX, y = itemOffsetY)
                        .scale(currentScale)
                        .alpha(itemAlpha),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.wrapContentSize()
                    ) {
                        // Circular button styled with GlassCard
                        GlassCard(
                            shape = CircleShape,
                            modifier = Modifier
                                .size(54.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = CircleShape,
                                    spotColor = Color.Black.copy(alpha = 0.35f)
                                )
                                .clickable(
                                    enabled = isExpanded,
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(color = Color.White)
                                ) {
                                    scope.launch {
                                        pickedIndex = i
                                        if (!isReducedMotion) {
                                            pickedScaleAnim.animateTo(1.25f, tween(250, easing = RadialFabDefaults.EaseCurve))
                                            pickedScaleAnim.animateTo(1.0f, tween(250, easing = RadialFabDefaults.EaseCurve))
                                        }

                                        val closeDelay = if (isReducedMotion) 0L else 260L
                                        delay(closeDelay)

                                        onToggle(false)
                                        noteMessage = item.doneMessage
                                        isNoteVisible = true

                                        item.onClick()

                                        delay(1800L)
                                        isNoteVisible = false
                                        pickedIndex = null
                                    }
                                }
                                .semantics {
                                    role = Role.Button
                                    contentDescription = item.label
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = item.gradientColors,
                                            start = Offset.Zero,
                                            end = Offset(150f, 150f)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = item.iconRes),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Text label with GlassCard capsule badge
                        GlassCard(
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(10.dp),
                                    spotColor = colors.shadow
                                )
                        ) {
                            Text(
                                text = item.label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.ink,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .background(colors.surface.copy(alpha = 0.78f))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- 4. Main FAB Button ---
        Box(
            modifier = Modifier
                .offset(y = fabOffsetY),
            contentAlignment = Alignment.Center
        ) {
            // Idle breathing ring
            if (!isExpanded && !isReducedMotion) {
                Canvas(modifier = Modifier.size(68.dp + 36.dp)) {
                    val radius = (34.dp.toPx()) + (18.dp.toPx() * breatheProgress)
                    val alpha = 0.45f * (1f - breatheProgress)
                    drawCircle(
                        color = Color(0xFF7B61FF).copy(alpha = alpha),
                        radius = radius,
                        center = center
                    )
                }
            }

            // Central FAB styled with GlassCard
            GlassCard(
                shape = CircleShape,
                modifier = Modifier
                    .size(68.dp)
                    .shadow(
                        elevation = 14.dp,
                        shape = CircleShape,
                        spotColor = Color(0xCC5B48FF),
                        ambientColor = Color(0x665B48FF)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(color = Color.White)
                    ) {
                        onToggle(!isExpanded)
                    }
                    .semantics {
                        role = Role.Button
                        contentDescription = if (isExpanded) "Đóng menu tạo sticker" else "Tạo mới sticker"
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF8B6CFF), Color(0xFF5B6CFF)),
                                start = Offset.Zero,
                                end = Offset(200f, 200f)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_radial_plus),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .size(28.dp)
                            .rotate(fabRotation)
                    )
                }
            }
        }
    }
}
