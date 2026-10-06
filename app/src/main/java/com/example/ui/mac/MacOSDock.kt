package com.example.ui.mac

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.MacDockBg
import com.example.ui.theme.WarpOrange
import com.example.vpn.ActiveWindow

@Composable
fun MacOSDock(
    activeWindow: ActiveWindow,
    onOpenWarp: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenTerminal: () -> Unit,
    onOpenActivityMonitor: () -> Unit,
    onOpenCleanIpScanner: () -> Unit,
    onToggleControlCenter: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Column(
        modifier = modifier
            .padding(bottom = (navBarPadding + 6.dp).coerceAtLeast(10.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Frosted Glass Dock Container
        Box(
            modifier = Modifier
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(22.dp),
                    spotColor = Color.Black.copy(alpha = 0.6f)
                )
                .clip(RoundedCornerShape(22.dp))
                .background(MacDockBg)
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // 1. Cloudflare WARP App
                DockAppIcon(
                    title = "WARP",
                    isActive = activeWindow == ActiveWindow.WARP_APP,
                    onClick = onOpenWarp,
                    testTag = "dock_warp_icon"
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.warp_icon),
                        contentDescription = "WARP App",
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(11.dp))
                    )
                }

                // 2. Settings App
                DockVectorIcon(
                    title = "Settings",
                    icon = Icons.Default.Settings,
                    gradientColors = listOf(Color(0xFF8E8E93), Color(0xFF48484A)),
                    isActive = activeWindow == ActiveWindow.SETTINGS,
                    onClick = onOpenSettings,
                    testTag = "dock_settings_icon"
                )

                // 3. Activity Monitor App
                DockVectorIcon(
                    title = "Activity",
                    icon = Icons.Default.QueryStats,
                    gradientColors = listOf(Color(0xFF34C759), Color(0xFF1E823A)),
                    isActive = activeWindow == ActiveWindow.ACTIVITY_MONITOR,
                    onClick = onOpenActivityMonitor,
                    testTag = "dock_activity_icon"
                )

                // 4. Terminal App
                DockVectorIcon(
                    title = "Terminal",
                    icon = Icons.Default.Terminal,
                    gradientColors = listOf(Color(0xFF2C2C2E), Color(0xFF1C1C1E)),
                    isActive = activeWindow == ActiveWindow.TERMINAL,
                    onClick = onOpenTerminal,
                    testTag = "dock_terminal_icon"
                )

                // 5. Clean IP & Speed Scanner
                DockVectorIcon(
                    title = "Clean IP",
                    icon = Icons.Default.Speed,
                    gradientColors = listOf(Color(0xFF5856D6), Color(0xFF3634A3)),
                    isActive = activeWindow == ActiveWindow.CLEAN_IP_SCANNER,
                    onClick = onOpenCleanIpScanner,
                    testTag = "dock_clean_ip_icon"
                )

                // Divider line inside dock
                Box(
                    modifier = Modifier
                        .height(38.dp)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.2f))
                        .align(Alignment.CenterVertically)
                )

                // 6. iOS Control Center Quick Tile
                DockVectorIcon(
                    title = "Control",
                    icon = Icons.Default.Tune,
                    gradientColors = listOf(Color(0xFF0A84FF), Color(0xFF0056B3)),
                    isActive = false,
                    onClick = onToggleControlCenter,
                    testTag = "dock_control_center_icon"
                )
            }
        }
    }
}

@Composable
private fun DockVectorIcon(
    title: String,
    icon: ImageVector,
    gradientColors: List<Color>,
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    DockAppIcon(
        title = title,
        isActive = isActive,
        onClick = onClick,
        testTag = testTag
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(Brush.linearGradient(gradientColors)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
private fun DockAppIcon(
    title: String,
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String,
    content: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.08f else 1.0f,
        animationSpec = spring(),
        label = "dock_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .testTag(testTag)
    ) {
        content()

        Spacer(modifier = Modifier.height(3.dp))

        // Running indicator dot beneath active apps
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) Color.White.copy(alpha = 0.9f)
                    else Color.Transparent
                )
        )
    }
}
