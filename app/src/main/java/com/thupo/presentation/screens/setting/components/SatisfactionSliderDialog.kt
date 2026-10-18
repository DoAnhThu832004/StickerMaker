// Lucian UX/UI Prompt #380 · MIT License (c) 2026 Seokwoooo · https://lucianlabs.dev/uxui-prompt/#380
package com.thupo.presentation.screens.setting.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.thupo.presentation.screens.intro.components.GlassCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

private val LABELS = listOf(
    "Rất tệ",
    "Chưa hài lòng",
    "Tạm ổn",
    "Hài lòng",
    "Rất tuyệt vời!"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SatisfactionSliderDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderValue by remember { mutableFloatStateOf(55f) }
    var isSent by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Chuyển động mượt mà của thanh slider
    val animatedProgress by animateFloatAsState(
        targetValue = sliderValue / 100f,
        animationSpec = spring(
            stiffness = Spring.StiffnessMediumLow,
            dampingRatio = Spring.DampingRatioNoBouncy
        ),
        label = "face_progress"
    )

    // Hiệu ứng gật đầu (Head nod) khi bấm Send
    val nodAnim = remember { Animatable(0f) }
    val rotAnim = remember { Animatable(0f) }

    // Màu sắc chuyển tiếp theo độ hài lòng
    val bgColor = remember(animatedProgress) {
        getSatisfactionColor(animatedProgress)
    }

    // Xác định nhãn cảm xúc (0..4)
    val step = (animatedProgress * 5f).toInt().coerceIn(0, 4)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        GlassCard(
            modifier = modifier
                .widthIn(max = 350.dp)
                .fillMaxWidth(0.92f)
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor.copy(alpha = 0.15f))
                    .padding(22.dp)
            ) {
                // Nút đóng góc trên
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Đóng",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 1. Câu hỏi tiêu đề
                    Text(
                        text = "Mức độ hài lòng của bạn?",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-0.02).sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. Khuôn mặt chuyển cảm xúc (Face)
                    SatisfactionFace(
                        progress = animatedProgress,
                        bgColor = bgColor,
                        nodOffsetY = nodAnim.value,
                        nodRotation = rotAnim.value,
                        modifier = Modifier.size(132.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. Nhãn trạng thái / Thông báo cảm ơn
                    Box(
                        modifier = Modifier
                            .height(30.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = isSent,
                            transitionSpec = {
                                (fadeIn() + slideInVertically { it / 2 }) togetherWith (fadeOut() + slideOutVertically { -it / 2 })
                            },
                            label = "sent_switch"
                        ) { sent ->
                            if (!sent) {
                                AnimatedContent(
                                    targetState = LABELS[step],
                                    transitionSpec = {
                                        slideInVertically { it } togetherWith slideOutVertically { -it }
                                    },
                                    label = "label_roll"
                                ) { labelText ->
                                    Text(
                                        text = labelText,
                                        fontSize = 19.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                Text(
                                    text = "Cảm ơn bạn đã phản hồi! 🎉",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF69DB7C),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Thanh trượt cảm xúc (Slider)
                    Slider(
                        value = sliderValue,
                        onValueChange = { if (!isSent) sliderValue = it },
                        valueRange = 0f..100f,
                        thumb = {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .shadow(6.dp, CircleShape)
                                    .background(Color.White, CircleShape)
                                    .border(3.5.dp, bgColor, CircleShape)
                            )
                        },
                        track = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color(0xFFFF6B6B),
                                                Color(0xFFFFD43B),
                                                Color(0xFF51CF66)
                                            )
                                        )
                                    )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // 5. Nút Gửi (Send)
                    Button(
                        onClick = {
                            if (!isSent) {
                                scope.launch {
                                    isSent = true
                                    // Kích hoạt animation gật đầu
                                    launch {
                                        rotAnim.animateTo(
                                            targetValue = 0f,
                                            animationSpec = keyframes {
                                                durationMillis = 900
                                                0f at 0
                                                -3f at 270
                                                0f at 540
                                                0f at 900
                                            }
                                        )
                                    }
                                    nodAnim.animateTo(
                                        targetValue = 0f,
                                        animationSpec = keyframes {
                                            durationMillis = 900
                                            0f at 0
                                            8f at 270
                                            -4f at 540
                                            6f at 720
                                            0f at 900
                                        }
                                    )
                                    delay(1600)
                                    onDismiss()
                                }
                            }
                        },
                        enabled = !isSent,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF16151B),
                            disabledContainerColor = Color.White.copy(alpha = 0.2f),
                            disabledContentColor = Color.White.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Text(
                            text = if (isSent) "Đã gửi thành công" else "Gửi đánh giá",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Vẽ khuôn mặt cảm xúc theo công thức hình học của Lucian UX/UI Prompt #380
 */
@Composable
private fun SatisfactionFace(
    progress: Float,
    bgColor: Color,
    nodOffsetY: Float,
    nodRotation: Float,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .graphicsLayer {
                translationY = nodOffsetY * density
                rotationZ = nodRotation
            }
    ) {
        val scale = size.width / 132f
        val t = progress.coerceIn(0f, 1f)

        // 1. Đầu hình tròn (Face Head)
        drawCircle(
            color = bgColor,
            radius = 62f * scale,
            center = Offset(66f * scale, 66f * scale)
        )

        // 2. Má hồng (Blushing cheeks)
        val cheekAlpha = ((t - 0.5f) * 1.6f).coerceIn(0f, 1f)
        if (cheekAlpha > 0f) {
            val cheekColor = Color(0xFFFF6B6B).copy(alpha = cheekAlpha)
            // Má trái
            drawOval(
                color = cheekColor,
                topLeft = Offset((34f - 11f) * scale, (82f - 7f) * scale),
                size = Size(22f * scale, 14f * scale)
            )
            // Má phải
            drawOval(
                color = cheekColor,
                topLeft = Offset((98f - 11f) * scale, (82f - 7f) * scale),
                size = Size(22f * scale, 14f * scale)
            )
        }

        // 3. Lông mày (Eyebrows)
        val tilt = if (t < 0.5f) mix(9f, 0f, t / 0.5f) else 0f
        val by = 40f - max(0f, t - 0.5f) * 12f
        val arc = if (t > 0.5f) -(t - 0.5f) * 9f else 0f
        val browStroke = Stroke(width = 5f * scale, cap = StrokeCap.Round)
        val featureColor = Color(0xFF2B2118)

        // Lông mày trái
        val leftBrowPath = Path().apply {
            moveTo(34f * scale, (by - tilt) * scale)
            quadraticTo(45f * scale, (by + arc) * scale, 56f * scale, (by + tilt) * scale)
        }
        drawPath(leftBrowPath, color = featureColor, style = browStroke)

        // Lông mày phải
        val rightBrowPath = Path().apply {
            moveTo(98f * scale, (by - tilt) * scale)
            quadraticTo(87f * scale, (by + arc) * scale, 76f * scale, (by + tilt) * scale)
        }
        drawPath(rightBrowPath, color = featureColor, style = browStroke)

        // 4. Mắt (Eyes)
        val sq = ((t - 0.6f) / 0.2f).coerceIn(0f, 1f)
        val happy = t >= 0.8f

        if (!happy) {
            val rx = mix(6f, 7.5f, sq) * scale
            val ry = mix(8f, 2.2f, sq) * scale
            // Mắt trái
            drawOval(
                color = featureColor,
                topLeft = Offset(46f * scale - rx, 60f * scale - ry),
                size = Size(rx * 2f, ry * 2f)
            )
            // Mắt phải
            drawOval(
                color = featureColor,
                topLeft = Offset(86f * scale - rx, 60f * scale - ry),
                size = Size(rx * 2f, ry * 2f)
            )
        } else {
            // Mắt cười híp hình vòng cung
            val happyEyeStroke = Stroke(width = 5f * scale, cap = StrokeCap.Round)
            val leftHappyEye = Path().apply {
                moveTo(37f * scale, 63f * scale)
                quadraticTo(46f * scale, 52f * scale, 55f * scale, 63f * scale)
            }
            val rightHappyEye = Path().apply {
                moveTo(77f * scale, 63f * scale)
                quadraticTo(86f * scale, 52f * scale, 95f * scale, 63f * scale)
            }
            drawPath(leftHappyEye, color = featureColor, style = happyEyeStroke)
            drawPath(rightHappyEye, color = featureColor, style = happyEyeStroke)
        }

        // 5. Miệng và Lưỡi (Mouth & Tongue)
        val curve = mix(-16f, 14f, (t / 0.68f).coerceIn(0f, 1f))
        val w = mix(20f, 27f, t)
        val y = mix(98f, 90f, t)
        val open = ((t - 0.68f) / 0.28f).coerceIn(0f, 1f)
        val cu = mix(curve, 6f, open)
        val clw = mix(cu, 46f, open)
        val wl = w + open * 3f

        val mouthPath = Path().apply {
            moveTo((66f - wl) * scale, y * scale)
            quadraticTo(66f * scale, (y + cu) * scale, (66f + wl) * scale, y * scale)
            cubicTo(
                (66f + wl * 0.8f) * scale, (y + clw * 0.68f) * scale,
                (66f - wl * 0.8f) * scale, (y + clw * 0.68f) * scale,
                (66f - wl) * scale, y * scale
            )
            close()
        }

        if (open > 0.05f) {
            // Vẽ khoang miệng đen
            drawPath(mouthPath, color = featureColor)

            // Vẽ lưỡi hồng cắt trong khoang miệng
            val tongueAlpha = ((open - 0.3f) * 2.5f).coerceIn(0f, 1f)
            if (tongueAlpha > 0f) {
                clipPath(mouthPath) {
                    val tongueRx = wl * 0.46f * scale
                    val tongueRy = 7f * scale
                    val tongueCy = (y + clw * 0.44f) * scale
                    drawOval(
                        color = Color(0xFFFF8A8A).copy(alpha = tongueAlpha),
                        topLeft = Offset(66f * scale - tongueRx, tongueCy - tongueRy),
                        size = Size(tongueRx * 2f, tongueRy * 2f)
                    )
                }
            }
        }

        // Đường viền miệng
        drawPath(
            path = mouthPath,
            color = featureColor,
            style = Stroke(
                width = 5f * scale,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

private fun mix(a: Float, b: Float, t: Float): Float = a + (b - a) * t

private fun getSatisfactionColor(t: Float): Color {
    val coral = Color(0xFFFF8787)
    val yellow = Color(0xFFFFD43B)
    val green = Color(0xFF69DB7C)
    return if (t < 0.5f) {
        val fraction = (t * 2f).coerceIn(0f, 1f)
        androidx.compose.ui.graphics.lerp(coral, yellow, fraction)
    } else {
        val fraction = ((t - 0.5f) * 2f).coerceIn(0f, 1f)
        androidx.compose.ui.graphics.lerp(yellow, green, fraction)
    }
}

@Preview
@Composable
fun SatisfactionSliderDialogPreview() {
    MaterialTheme {
        SatisfactionSliderDialog(onDismiss = {})
    }
}
