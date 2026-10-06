package com.example.ui.mac

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MacTrafficClose
import com.example.ui.theme.MacTrafficMaximize
import com.example.ui.theme.MacTrafficMinimize
import com.example.ui.theme.MacWindowBg
import com.example.ui.theme.MacWindowBorder

@Composable
fun MacOSWindowFrame(
    title: String,
    modifier: Modifier = Modifier,
    isMaximized: Boolean = false,
    onClose: () -> Unit,
    onMinimize: () -> Unit,
    onToggleMaximize: () -> Unit,
    content: @Composable () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .shadow(
                elevation = 24.dp,
                shape = RoundedCornerShape(if (isMaximized) 0.dp else 16.dp),
                spotColor = Color.Black.copy(alpha = 0.5f)
            )
            .clip(RoundedCornerShape(if (isMaximized) 0.dp else 16.dp))
            .background(MacWindowBg)
            .border(
                width = 1.dp,
                color = MacWindowBorder,
                shape = RoundedCornerShape(if (isMaximized) 0.dp else 16.dp)
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // macOS Title Bar with Traffic Lights
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .background(Color(0xFF1E1E28).copy(alpha = 0.85f))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Traffic Lights
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Close (Red)
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(MacTrafficClose)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onClose
                            )
                            .testTag("window_close_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isHovered) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Window",
                                tint = Color.Black.copy(alpha = 0.6f),
                                modifier = Modifier.size(8.dp)
                            )
                        }
                    }

                    // Minimize (Yellow)
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(MacTrafficMinimize)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onMinimize
                            )
                            .testTag("window_minimize_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isHovered) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Minimize Window",
                                tint = Color.Black.copy(alpha = 0.6f),
                                modifier = Modifier.size(8.dp)
                            )
                        }
                    }

                    // Maximize / Expand (Green)
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(MacTrafficMaximize)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onToggleMaximize
                            )
                            .testTag("window_maximize_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isHovered) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Toggle Maximize",
                                tint = Color.Black.copy(alpha = 0.6f),
                                modifier = Modifier.size(8.dp)
                            )
                        }
                    }
                }

                // Window Title in center
                Text(
                    text = title,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                )

                // Invisible spacer matching traffic lights width for exact center alignment
                Box(modifier = Modifier.size(52.dp, 12.dp))
            }

            // Window Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                content()
            }
        }
    }
}
