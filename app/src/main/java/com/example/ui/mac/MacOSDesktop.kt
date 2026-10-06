package com.example.ui.mac

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.vpn.ActiveWindow
import com.example.vpn.ConnectionState
import com.example.vpn.WarpVpnManager

@Composable
fun MacOSDesktop(
    onToggleVpn: () -> Unit
) {
    val connectionState by WarpVpnManager.stateFlow.collectAsState()

    var activeWindow by remember { mutableStateOf(ActiveWindow.WARP_APP) }
    var isWindowMaximized by remember { mutableStateOf(false) }
    var showControlCenter by remember { mutableStateOf(false) }
    var showAboutMac by remember { mutableStateOf(false) }
    var isPersian by remember { mutableStateOf(true) } // Persian by default as requested!

    // Back handler for closing windows or control center
    BackHandler(enabled = activeWindow != ActiveWindow.NONE || showControlCenter || showAboutMac) {
        when {
            showControlCenter -> showControlCenter = false
            showAboutMac -> showAboutMac = false
            activeWindow != ActiveWindow.WARP_APP -> activeWindow = ActiveWindow.WARP_APP
            else -> activeWindow = ActiveWindow.NONE
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // macOS Dynamic Wallpaper Background
        Image(
            painter = painterResource(id = R.drawable.macos_wallpaper),
            contentDescription = "macOS Wallpaper",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle dark vignette overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.22f))
        )

        // Main Desktop Layout
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Menu Bar
            MacOSMenuBar(
                activeAppName = when (activeWindow) {
                    ActiveWindow.WARP_APP -> "WARP"
                    ActiveWindow.SETTINGS -> "Settings"
                    ActiveWindow.TERMINAL -> "Terminal"
                    ActiveWindow.ACTIVITY_MONITOR -> "Activity Monitor"
                    ActiveWindow.CLEAN_IP_SCANNER -> "Clean IP Scanner"
                    ActiveWindow.NONE -> "Finder"
                },
                isPersian = isPersian,
                connectionState = connectionState,
                onToggleLanguage = { isPersian = !isPersian },
                onOpenAboutMac = { showAboutMac = true },
                onOpenSettings = { activeWindow = ActiveWindow.SETTINGS },
                onOpenTerminal = { activeWindow = ActiveWindow.TERMINAL },
                onOpenActivityMonitor = { activeWindow = ActiveWindow.ACTIVITY_MONITOR },
                onToggleControlCenter = { showControlCenter = !showControlCenter },
                onToggleVpn = onToggleVpn
            )

            // Desktop Workspace Area (Shortcuts + Windows)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                // Desktop Desktop App Shortcuts (visible when window is minimized or not covering)
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 10.dp, start = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    DesktopShortcut(
                        title = "WARP.app",
                        onClick = { activeWindow = ActiveWindow.WARP_APP }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.warp_icon),
                            contentDescription = "WARP",
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }

                    DesktopShortcut(
                        title = "Settings.app",
                        onClick = { activeWindow = ActiveWindow.SETTINGS }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF8E8E93), Color(0xFF48484A)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    }

                    DesktopShortcut(
                        title = "Terminal.app",
                        onClick = { activeWindow = ActiveWindow.TERMINAL }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF2C2C2E), Color(0xFF1C1C1E)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Terminal, contentDescription = "Terminal", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    }

                    DesktopShortcut(
                        title = "Clean IP.app",
                        onClick = { activeWindow = ActiveWindow.CLEAN_IP_SCANNER }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF5856D6), Color(0xFF3634A3)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Speed, contentDescription = "Clean IP", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    }
                }

                // Active Window Container
                androidx.compose.animation.AnimatedVisibility(
                    visible = activeWindow != ActiveWindow.NONE,
                    enter = fadeIn() + scaleIn(initialScale = 0.92f),
                    exit = fadeOut() + scaleOut(targetScale = 0.92f),
                    modifier = Modifier.fillMaxSize()
                ) {
                    val windowTitle = when (activeWindow) {
                        ActiveWindow.WARP_APP -> if (isPersian) "کلودفلر وارپ — Cloudflare WARP" else "Cloudflare WARP"
                        ActiveWindow.SETTINGS -> if (isPersian) "تنظیمات سیستم — System Settings" else "System Settings"
                        ActiveWindow.TERMINAL -> if (isPersian) "ترمینال وارپ — warp@macbook-pro" else "Terminal — warp@macbook-pro"
                        ActiveWindow.ACTIVITY_MONITOR -> if (isPersian) "مانیتورینگ شبکه — Activity Monitor" else "Activity Monitor"
                        ActiveWindow.CLEAN_IP_SCANNER -> if (isPersian) "اسکنر سرورهای وارپ — Clean IP Scanner" else "Clean IP Scanner"
                        ActiveWindow.NONE -> ""
                    }

                    MacOSWindowFrame(
                        title = windowTitle,
                        isMaximized = isWindowMaximized,
                        onClose = { activeWindow = ActiveWindow.NONE },
                        onMinimize = { activeWindow = ActiveWindow.NONE },
                        onToggleMaximize = { isWindowMaximized = !isWindowMaximized },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 60.dp) // Leave space for dock
                    ) {
                        when (activeWindow) {
                            ActiveWindow.WARP_APP -> WarpMainWindow(
                                isPersian = isPersian,
                                onToggleConnection = onToggleVpn,
                                onOpenSettings = { activeWindow = ActiveWindow.SETTINGS },
                                onOpenTerminal = { activeWindow = ActiveWindow.TERMINAL },
                                onOpenCleanIpScanner = { activeWindow = ActiveWindow.CLEAN_IP_SCANNER }
                            )
                            ActiveWindow.SETTINGS -> SettingsWindow(isPersian = isPersian)
                            ActiveWindow.TERMINAL -> TerminalWindow(isPersian = isPersian)
                            ActiveWindow.ACTIVITY_MONITOR -> ActivityMonitorWindow(isPersian = isPersian)
                            ActiveWindow.CLEAN_IP_SCANNER -> CleanIpScannerWindow(isPersian = isPersian)
                            ActiveWindow.NONE -> { /* Minimized */ }
                        }
                    }
                }
            }

            // Bottom macOS Dock
            MacOSDock(
                activeWindow = activeWindow,
                onOpenWarp = { activeWindow = ActiveWindow.WARP_APP },
                onOpenSettings = { activeWindow = ActiveWindow.SETTINGS },
                onOpenTerminal = { activeWindow = ActiveWindow.TERMINAL },
                onOpenActivityMonitor = { activeWindow = ActiveWindow.ACTIVITY_MONITOR },
                onOpenCleanIpScanner = { activeWindow = ActiveWindow.CLEAN_IP_SCANNER },
                onToggleControlCenter = { showControlCenter = !showControlCenter },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        // iOS Control Center Sheet Overlay
        AnimatedVisibility(
            visible = showControlCenter,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            IOSControlCenterOverlay(
                isPersian = isPersian,
                onDismiss = { showControlCenter = false },
                onToggleVpn = onToggleVpn
            )
        }

        // About Mac Modal
        AnimatedVisibility(
            visible = showAboutMac,
            enter = fadeIn() + scaleIn(initialScale = 0.9f),
            exit = fadeOut() + scaleOut(targetScale = 0.9f)
        ) {
            AboutMacDialog(
                isPersian = isPersian,
                onDismiss = { showAboutMac = false }
            )
        }
    }
}

@Composable
private fun DesktopShortcut(
    title: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(4.dp)
    ) {
        icon()
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black.copy(alpha = 0.45f))
                .padding(horizontal = 4.dp, vertical = 1.dp)
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
