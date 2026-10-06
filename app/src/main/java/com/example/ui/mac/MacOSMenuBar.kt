package com.example.ui.mac

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.MacMenuBarBg
import com.example.ui.theme.MacTextPrimary
import com.example.ui.theme.MacTextSecondary
import com.example.ui.theme.WarpOrange
import com.example.vpn.ConnectionState
import com.example.vpn.WarpProtocol
import com.example.vpn.WarpVpnManager
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MacOSMenuBar(
    activeAppName: String = "WARP",
    isPersian: Boolean = false,
    connectionState: ConnectionState,
    onToggleLanguage: () -> Unit,
    onOpenAboutMac: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenActivityMonitor: () -> Unit,
    onToggleControlCenter: () -> Unit,
    onToggleVpn: () -> Unit
) {
    val context = LocalContext.current
    var showAppleMenu by remember { mutableStateOf(false) }
    var showProtocolsMenu by remember { mutableStateOf(false) }
    var showWarpTrayMenu by remember { mutableStateOf(false) }

    // Live Clock string
    var currentTime by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        val formatter = SimpleDateFormat("EEE h:mm a", Locale.ENGLISH)
        while (true) {
            currentTime = formatter.format(Date())
            delay(10000)
        }
    }

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = statusBarPadding)
                .height(30.dp)
                .background(MacMenuBarBg)
                .border(width = 0.5.dp, color = Color.White.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Items: Apple Logo & App Title & Menus
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Apple Logo 
                    Text(
                        text = "",
                        color = MacTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    showAppleMenu = !showAppleMenu
                                    showProtocolsMenu = false
                                    showWarpTrayMenu = false
                                }
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                            .testTag("apple_menu_button")
                    )

                    // Active App Name (Bold)
                    Text(
                        text = if (isPersian) "کلودفلر وارپ" else "WARP",
                        color = MacTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onOpenSettings
                            )
                            .testTag("active_app_title")
                    )

                    // Protocols Menu
                    Text(
                        text = if (isPersian) "پروتکل‌ها" else "Protocols",
                        color = MacTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = {
                                    showProtocolsMenu = !showProtocolsMenu
                                    showAppleMenu = false
                                    showWarpTrayMenu = false
                                }
                            )
                            .padding(horizontal = 3.dp)
                            .testTag("menu_protocols_button")
                    )

                    // Terminal
                    Text(
                        text = if (isPersian) "ترمینال" else "Terminal",
                        color = MacTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onOpenTerminal
                            )
                            .padding(horizontal = 3.dp)
                    )

                    // Activity
                    Text(
                        text = if (isPersian) "مانیتور شبکه" else "Activity",
                        color = MacTextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onOpenActivityMonitor
                            )
                            .padding(horizontal = 3.dp)
                    )
                }

                // Right Tray Items: Language, WARP Status, Wi-Fi, Battery, Control Center, Clock
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Language Switcher
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .clickable(onClick = onToggleLanguage)
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                            .testTag("language_toggle_button")
                    ) {
                        Text(
                            text = if (isPersian) "فا" else "EN",
                            color = MacTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Cloudflare WARP quick status icon
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(
                                if (connectionState.isConnected) WarpOrange.copy(alpha = 0.25f)
                                else Color.Transparent
                            )
                            .clickable {
                                showWarpTrayMenu = !showWarpTrayMenu
                                showAppleMenu = false
                                showProtocolsMenu = false
                            }
                            .padding(2.dp)
                            .testTag("warp_tray_icon"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "WARP Tray Status",
                            tint = if (connectionState.isConnected) WarpOrange else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // Wi-Fi Icon
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Wi-Fi",
                        tint = MacTextPrimary,
                        modifier = Modifier.size(13.dp)
                    )

                    // Battery Icon & percent
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BatteryFull,
                            contentDescription = "Battery",
                            tint = AppleGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "98%",
                            color = MacTextSecondary,
                            fontSize = 10.sp
                        )
                    }

                    // Control Center Icon (toggles iOS/macOS Control Center)
                    Box(
                        modifier = Modifier
                            .clickable(onClick = onToggleControlCenter)
                            .padding(2.dp)
                            .testTag("control_center_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Control Center",
                            tint = MacTextPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // Clock
                    Text(
                        text = if (currentTime.isNotEmpty()) currentTime else "1:15 PM",
                        color = MacTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Apple Dropdown Menu
        AnimatedVisibility(
            visible = showAppleMenu,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 10.dp, top = 2.dp)
                    .shadow(16.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xE6252530))
                    .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .width(210.dp)
                    .padding(vertical = 4.dp)
            ) {
                Column {
                    MenuRowItem(
                        title = if (isPersian) "درباره وارپ او‌اس (About WARP OS)" else "About This Mac (WARP OS)",
                        onClick = {
                            showAppleMenu = false
                            onOpenAboutMac()
                        }
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 3.dp))
                    MenuRowItem(
                        title = if (isPersian) "تنظیمات سیستم..." else "System Settings...",
                        onClick = {
                            showAppleMenu = false
                            onOpenSettings()
                        }
                    )
                    MenuRowItem(
                        title = if (isPersian) "ترمینال وارپ..." else "Terminal WARP...",
                        onClick = {
                            showAppleMenu = false
                            onOpenTerminal()
                        }
                    )
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 3.dp))
                    MenuRowItem(
                        title = if (isPersian) "راه‌اندازی مجدد تونل" else "Restart VPN Tunnel",
                        onClick = {
                            showAppleMenu = false
                            onToggleVpn()
                        }
                    )
                }
            }
        }

        // Protocols Dropdown Menu
        AnimatedVisibility(
            visible = showProtocolsMenu,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val currentProtocol = WarpVpnManager.configFlow.value.protocol
            Box(
                modifier = Modifier
                    .padding(start = 70.dp, top = 2.dp)
                    .shadow(16.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xE6252530))
                    .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .width(260.dp)
                    .padding(vertical = 4.dp)
            ) {
                Column {
                    WarpProtocol.values().forEach { protocol ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    WarpVpnManager.setProtocol(protocol)
                                    showProtocolsMenu = false
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isPersian) protocol.titleFa else protocol.title,
                                    color = MacTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${protocol.badge} • Port ${protocol.defaultPort}",
                                    color = MacTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                            if (currentProtocol == protocol) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = AppleBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // WARP Tray Menu (like Cloudflare macOS client popover)
        AnimatedVisibility(
            visible = showWarpTrayMenu,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 40.dp, top = 2.dp)
                    .shadow(20.dp, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xF020202A))
                    .border(0.5.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    .width(240.dp)
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "1.1.1.1 with WARP",
                                color = MacTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (connectionState.isConnected) "Connected" else "Disconnected",
                                color = if (connectionState.isConnected) AppleGreen else Color.White.copy(alpha = 0.5f),
                                fontSize = 11.sp
                            )
                        }

                        Switch(
                            checked = connectionState.isConnected,
                            onCheckedChange = {
                                showWarpTrayMenu = false
                                onToggleVpn()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = WarpOrange,
                                uncheckedThumbColor = Color.White.copy(alpha = 0.7f),
                                uncheckedTrackColor = Color.DarkGray
                            ),
                            modifier = Modifier.testTag("tray_warp_switch")
                        )
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "Protocol: ${WarpVpnManager.configFlow.value.protocol.badge}",
                        color = MacTextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "Colo: ${WarpVpnManager.configFlow.value.endpoint.location}",
                        color = MacTextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .clickable {
                                showWarpTrayMenu = false
                                onOpenSettings()
                            }
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (isPersian) "تنظیمات پیشرفته وارپ" else "Preferences...",
                            color = AppleBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuRowItem(
    title: String,
    onClick: () -> Unit
) {
    Text(
        text = title,
        color = MacTextPrimary,
        fontSize = 12.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}
