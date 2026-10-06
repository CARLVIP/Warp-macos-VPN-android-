package com.example.ui.mac

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.MacCardBg
import com.example.ui.theme.MacTextPrimary
import com.example.ui.theme.MacTextSecondary
import com.example.ui.theme.WarpCyan
import com.example.ui.theme.WarpOrange
import com.example.ui.theme.WarpOrangeDark
import com.example.ui.theme.WarpOrangeLight
import com.example.ui.theme.WarpPurple
import com.example.vpn.ConnectionState
import com.example.vpn.WarpMetrics
import com.example.vpn.WarpProtocol
import com.example.vpn.WarpSettingsConfig
import com.example.vpn.WarpVpnManager
import kotlinx.coroutines.delay

@Composable
fun WarpMainWindow(
    isPersian: Boolean,
    onToggleConnection: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenCleanIpScanner: () -> Unit
) {
    val connectionState by WarpVpnManager.stateFlow.collectAsState()
    val metrics by WarpVpnManager.metricsFlow.collectAsState()
    val config by WarpVpnManager.configFlow.collectAsState()

    val scrollState = rememberScrollState()

    // Pulse animation for connected button
    val infiniteTransition = rememberInfiniteTransition(label = "warp_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (connectionState.isConnected) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Formatted uptime
    var uptimeFormatted by remember { mutableStateOf("00:00:00") }
    LaunchedEffect(connectionState) {
        if (connectionState is ConnectionState.Connected) {
            val startTime = (connectionState as ConnectionState.Connected).connectedTimeMillis
            while (true) {
                val elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000
                val hours = elapsedSeconds / 3600
                val minutes = (elapsedSeconds % 3600) / 60
                val seconds = elapsedSeconds % 60
                uptimeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
                delay(1000)
            }
        } else {
            uptimeFormatted = "00:00:00"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header Banner
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.linearGradient(listOf(WarpOrange, WarpOrangeDark))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = "Cloudflare",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column {
                    Text(
                        text = "1.1.1.1",
                        color = MacTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (config.isWarpPlusActive) "WARP+ TURBO" else "WARP macOS",
                        color = if (config.isWarpPlusActive) WarpCyan else WarpOrange,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Endpoint Badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .clickable(onClick = onOpenCleanIpScanner)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(text = config.endpoint.flag, fontSize = 12.sp)
                Text(
                    text = "${config.endpoint.location.substringBefore(",")} (${config.endpoint.latencyMs ?: 28}ms)",
                    color = MacTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Center Hero: The Iconic Glowing WARP Switch Button
        Box(
            modifier = Modifier
                .size(150.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onToggleConnection
                )
                .testTag("warp_main_toggle_button"),
            contentAlignment = Alignment.Center
        ) {
            // Ambient outer glow ring
            if (connectionState.isConnected) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(pulseScale)
                        .rotate(ringRotation)
                ) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                WarpOrange.copy(alpha = 0.7f),
                                WarpCyan.copy(alpha = 0.7f),
                                WarpOrange.copy(alpha = 0.7f)
                            )
                        ),
                        style = Stroke(width = 4.dp.toPx())
                    )
                }
            }

            // Main Glass Button Core
            val buttonGradient = when (connectionState) {
                is ConnectionState.Connected -> Brush.linearGradient(
                    listOf(WarpOrange, WarpOrangeDark, Color(0xFFC45700))
                )
                is ConnectionState.Connecting -> Brush.linearGradient(
                    listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
                )
                else -> Brush.linearGradient(
                    listOf(Color(0xFF2C2C36), Color(0xFF1E1E26))
                )
            }

            Box(
                modifier = Modifier
                    .size(126.dp)
                    .shadow(
                        elevation = if (connectionState.isConnected) 24.dp else 8.dp,
                        shape = CircleShape,
                        spotColor = if (connectionState.isConnected) WarpOrange else Color.Black
                    )
                    .clip(CircleShape)
                    .background(buttonGradient)
                    .border(
                        width = 2.dp,
                        color = if (connectionState.isConnected) WarpOrangeLight.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (connectionState.isConnecting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(36.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "WARP Toggle",
                            tint = if (connectionState.isConnected) Color.White else Color.White.copy(alpha = 0.45f),
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = "WARP",
                            color = if (connectionState.isConnected) Color.White else Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Status Heading and Subtitle
        val statusTitle = when (connectionState) {
            is ConnectionState.Connected -> if (isPersian) "متصل به وارپ" else "Connected"
            is ConnectionState.Connecting -> if (isPersian) "در حال اتصال..." else "Connecting..."
            is ConnectionState.Disconnecting -> if (isPersian) "در حال قطع اتصال..." else "Disconnecting..."
            is ConnectionState.Error -> if (isPersian) "خطا در اتصال" else "Connection Failed"
            else -> if (isPersian) "قطع شده" else "Disconnected"
        }

        val statusSubtitle = when (connectionState) {
            is ConnectionState.Connected -> if (isPersian) "اینترنت شما با پروتکل ${config.protocol.badge} ایمن و اختصاصی است" else "Your Internet is private and encrypted"
            is ConnectionState.Connecting -> (connectionState as ConnectionState.Connecting).let {
                if (isPersian) it.progressStageFa else it.progressStage
            }
            is ConnectionState.Error -> (connectionState as ConnectionState.Error).let {
                if (isPersian) it.messageFa else it.message
            }
            else -> if (isPersian) "برای فعال‌سازی و دور زدن محدودیت‌ها روی کلید لمس کنید" else "Tap switch to enable 1.1.1.1 with WARP"
        }

        Text(
            text = statusTitle,
            color = if (connectionState.isConnected) AppleGreen else MacTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = statusSubtitle,
            color = MacTextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Protocol Switcher Pills Row (1-Tap Protocol Switch)
        Text(
            text = if (isPersian) "انتخاب سریع پروتکل وارپ:" else "Quick Protocol Switch:",
            color = MacTextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            WarpProtocol.values().take(4).forEach { proto ->
                val isSelected = config.protocol == proto
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) WarpOrange.copy(alpha = 0.25f)
                            else Color.White.copy(alpha = 0.06f)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) WarpOrange else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            WarpVpnManager.setProtocol(proto)
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = proto.badge,
                            color = if (isSelected) WarpOrange else MacTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = ":${proto.defaultPort}",
                            color = MacTextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Real-Time Network Metrics Dashboard (3 Glass Cards)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Download Speed
            MetricCard(
                modifier = Modifier.weight(1f),
                title = if (isPersian) "دانلود" else "Download",
                value = if (connectionState.isConnected) formatSpeed(metrics.downloadSpeedKbps) else "0.0 KB/s",
                icon = Icons.Default.ArrowDownward,
                iconColor = AppleGreen
            )

            // Upload Speed
            MetricCard(
                modifier = Modifier.weight(1f),
                title = if (isPersian) "آپلود" else "Upload",
                value = if (connectionState.isConnected) formatSpeed(metrics.uploadSpeedKbps) else "0.0 KB/s",
                icon = Icons.Default.ArrowUpward,
                iconColor = AppleBlue
            )

            // Ping Latency
            MetricCard(
                modifier = Modifier.weight(1f),
                title = if (isPersian) "پینگ" else "Latency",
                value = if (connectionState.isConnected) "${metrics.pingLatencyMs} ms" else "${config.endpoint.latencyMs ?: 32} ms",
                icon = Icons.Default.Speed,
                iconColor = WarpOrange
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Connection Details Glass Box (Server Colocation, Session Timer, Data Used)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MacCardBg)
                .border(0.5.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DetailRow(
                    label = if (isPersian) "سرور لبه کلودفلر (Colo):" else "Cloudflare Edge Colocation:",
                    value = "${config.endpoint.flag} ${config.endpoint.location} (${config.endpoint.ip})"
                )
                DetailRow(
                    label = if (isPersian) "آی‌پی مجازی کلاینت:" else "Client Virtual IP:",
                    value = "${metrics.clientVirtualIp} (IPv4 Tunnel)"
                )
                DetailRow(
                    label = if (isPersian) "مدت اتصال:" else "Session Uptime:",
                    value = uptimeFormatted
                )
                DetailRow(
                    label = if (isPersian) "حجم ترافیک مصرفی:" else "Total Transferred:",
                    value = "${formatBytes(metrics.totalDownloadedBytes)} Down / ${formatBytes(metrics.totalUploadedBytes)} Up"
                )
                DetailRow(
                    label = if (isPersian) "وضعیت اکانت:" else "WARP Account:",
                    value = if (config.isWarpPlusActive) "WARP+ Unlimited (Active)" else "WARP Free"
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Utilities Bar (Clean IP, Terminal, Settings)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickButton(
                modifier = Modifier.weight(1f),
                title = if (isPersian) "تست آی‌پی تمیز" else "Scan Clean IP",
                icon = Icons.Default.FlashOn,
                tint = WarpOrange,
                onClick = onOpenCleanIpScanner,
                testTag = "warp_scan_clean_ip_btn"
            )

            QuickButton(
                modifier = Modifier.weight(1f),
                title = if (isPersian) "ترمینال وارپ" else "Terminal Logs",
                icon = Icons.Default.Terminal,
                tint = WarpCyan,
                onClick = onOpenTerminal,
                testTag = "warp_open_terminal_btn"
            )

            QuickButton(
                modifier = Modifier.weight(1f),
                title = if (isPersian) "تنظیمات" else "Settings",
                icon = Icons.Default.Settings,
                tint = AppleBlue,
                onClick = onOpenSettings,
                testTag = "warp_open_settings_btn"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MacCardBg)
            .border(0.5.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = title,
                    color = MacTextSecondary,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = MacTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = MacTextSecondary,
            fontSize = 11.sp
        )
        Text(
            text = value,
            color = MacTextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun QuickButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = tint,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = title,
                color = MacTextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

fun formatSpeed(kbps: Double): String {
    return if (kbps >= 1024.0) {
        String.format("%.1f MB/s", kbps / 1024.0)
    } else {
        String.format("%.0f KB/s", kbps)
    }
}

fun formatBytes(bytes: Long): String {
    val mb = bytes / (1024.0 * 1024.0)
    return if (mb >= 1024.0) {
        String.format("%.2f GB", mb / 1024.0)
    } else {
        String.format("%.1f MB", mb)
    }
}
