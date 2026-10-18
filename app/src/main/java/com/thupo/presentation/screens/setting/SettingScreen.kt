package com.thupo.presentation.screens.setting

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thupo.presentation.screens.intro.components.AuroraGlow
import com.thupo.presentation.screens.intro.components.GlassCard
import com.thupo.presentation.screens.intro.components.NavyDark
import com.thupo.presentation.screens.intro.components.NavyMid
import com.thupo.presentation.screens.setting.components.SatisfactionSliderDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentDensity = LocalDensity.current
    var showSatisfactionDialog by remember { mutableStateOf(false) }
    val backgroundBrush = remember {
        Brush.verticalGradient(listOf(NavyDark, NavyMid, NavyDark))
    }
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cài đặt",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    GlassCard(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable {
                                onNavigateBack()
                            },
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Cài đặt",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { innerPadding ->
        AuroraGlow()
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nhóm 1: Ứng dụng & Hỗ trợ
            Text(
                text = "TƯƠNG TÁC & PHẢN HỒI",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column {
                    SettingItemRow(
                        icon = Icons.Default.Star,
                        title = "Đánh giá ứng dụng",
                        subtitle = "Ủng hộ 5 sao trên Google Play",
                        onClick = { openPlayStore(context) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    SettingItemRow(
                        icon = Icons.Default.Star,
                        title = "Phản hồi ứng dụng",
                        subtitle = "Đánh giá sự hài lòng",
                        onClick = { showSatisfactionDialog = true }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    SettingItemRow(
                        icon = Icons.Default.Share,
                        title = "Chia sẻ ứng dụng",
                        subtitle = "Gửi cho người thân, bạn bè",
                        onClick = { shareApp(context) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    SettingItemRow(
                        icon = Icons.Default.Email,
                        title = "Góp ý & Phản hồi",
                        subtitle = "Gửi email cho đội ngũ phát triển",
                        onClick = { sendFeedbackEmail(context) }
                    )
                }
            }

            // Nhóm 2: Quyền riêng tư & Thông tin
            Text(
                text = "THÔNG TIN & CHÍNH SÁCH",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column {
                    SettingItemRow(
                        icon = Icons.Default.Security,
                        title = "Chính sách bảo mật",
                        subtitle = "Điều khoản và quyền riêng tư",
                        onClick = { openPrivacyPolicy(context) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    SettingItemRow(
                        icon = Icons.Default.Info,
                        title = "Phiên bản",
                        subtitle = "1.0.0 (BigFont Modern Edition)",
                        showArrow = false,
                        onClick = {
                            Toast.makeText(context, "Phiên bản mới nhất", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    if (showSatisfactionDialog) {
        SatisfactionSliderDialog(
            onDismiss = { showSatisfactionDialog = false }
        )
    }
}

@Composable
private fun SettingItemRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    showArrow: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassCard(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }

        if (showArrow) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

private fun openPlayStore(context: Context) {
    val packageName = context.packageName
    try {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    } catch (_: Exception) {
        context.startActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}

private fun shareApp(context: Context) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "Hãy thử ứng dụng Big Font - Phóng to chữ toàn bộ hệ thống điện thoại cực kỳ dễ dàng: https://play.google.com/store/apps/details?id=${context.packageName}"
        )
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Chia sẻ Big Font"))
}

private fun sendFeedbackEmail(context: Context) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:thuanhdo83@gmail.com")
        putExtra(Intent.EXTRA_SUBJECT, "[Phản hồi Big Font] Ý kiến đóng góp")
        putExtra(
            Intent.EXTRA_TEXT,
            "\n\n---\nThiết bị: ${Build.MANUFACTURER} ${Build.MODEL}\nAndroid: ${Build.VERSION.RELEASE}"
        )
    }
    try {
        context.startActivity(intent)
    } catch (_: Exception) {
        Toast.makeText(context, "Không tìm thấy ứng dụng gửi email trên máy", Toast.LENGTH_SHORT).show()
    }
}

private fun openPrivacyPolicy(context: Context) {
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"))
    try {
        context.startActivity(browserIntent)
    } catch (_: Exception) {
        Toast.makeText(context, "Không thể mở trình duyệt", Toast.LENGTH_SHORT).show()
    }
}