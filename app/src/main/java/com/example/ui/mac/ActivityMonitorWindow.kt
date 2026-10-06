package com.example.ui.mac

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.MacCardBg
import com.example.ui.theme.MacTextPrimary
import com.example.ui.theme.MacTextSecondary
import com.example.vpn.ConnectionState
import com.example.vpn.WarpVpnManager
import kotlinx.coroutines.delay

@Composable
fun ActivityMonitorWindow(
    isPersian: Boolean
) {
    val connectionState by WarpVpnManager.stateFlow.collectAsState()
    val metrics by WarpVpnManager.metricsFlow.collectAsState()
    val config by WarpVpnManager.configFlow.collectAsState()

    // History points for drawing graph
    val downloadHistory = remember { mutableStateListOf<Float>() }
    val uploadHistory = remember { mutableStateListOf<Float>() }

    LaunchedEffect(metrics) {
        if (downloadHistory.size > 28) downloadHistory.removeAt(0)
        if (uploadHistory.size > 28) uploadHistory.removeAt(0)

        val downNorm = if (connectionState.isConnected) (metrics.downloadSpeedKbps / 6000.0).toFloat().coerceIn(0.05f, 0.95f) else 0.02f
        val upNorm = if (connectionState.isConnected) (metrics.uploadSpeedKbps / 2000.0).toFloat().coerceIn(0.05f, 0.95f) else 0.02f

        downloadHistory.add(downNorm)
        uploadHistory.add(upNorm)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF161620))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top summary
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isPersian) "نمودار زنده ترافیک شبکه (Activity Monitor):" else "Live Network Activity Monitor:",
                color = MacTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(8.dp).background(AppleGreen, RoundedCornerShape(2.dp)))
                    Text(text = "Download (${formatSpeed(metrics.downloadSpeedKbps)})", color = AppleGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(8.dp).background(AppleBlue, RoundedCornerShape(2.dp)))
                    Text(text = "Upload (${formatSpeed(metrics.uploadSpeedKbps)})", color = AppleBlue, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Real-Time Canvas Graph
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0F0F14))
                .border(0.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp, vertical = 6.dp)) {
                val w = size.width
                val h = size.height

                // Draw background grid lines
                for (i in 1..3) {
                    val y = h * (i / 4f)
                    drawLine(
                        color = Color.White.copy(alpha = 0.06f),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1f
                    )
                }

                if (downloadHistory.size >= 2) {
                    val step = w / (downloadHistory.size - 1)
                    val downPath = Path()
                    downloadHistory.forEachIndexed { index, norm ->
                        val x = index * step
                        val y = h - (norm * h)
                        if (index == 0) downPath.moveTo(x, y) else downPath.lineTo(x, y)
                    }
                    drawPath(path = downPath, color = AppleGreen, style = Stroke(width = 3.dp.toPx()))

                    val upPath = Path()
                    uploadHistory.forEachIndexed { index, norm ->
                        val x = index * step
                        val y = h - (norm * h)
                        if (index == 0) upPath.moveTo(x, y) else upPath.lineTo(x, y)
                    }
                    drawPath(path = upPath, color = AppleBlue, style = Stroke(width = 2.dp.toPx()))
                }
            }
        }

        // Network Process Stats Table
        Text(
            text = if (isPersian) "فرآیندهای تونل‌سازی فعال:" else "Active Tunnel Daemons & Subsystems:",
            color = MacTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MacCardBg)
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            ProcessRow("warp-svc (daemon)", "51820", "Active", "${formatBytes(metrics.totalDownloadedBytes)}", AppleGreen)
            ProcessRow("wg-quick (wireguard)", "2408", "Established", "${metrics.packetsTransferred} pkts", AppleGreen)
            ProcessRow("masque-proxy (quic)", "443", if (config.protocol == com.example.vpn.WarpProtocol.MASQUE_HTTP3) "Active" else "Standby", "0% drop", AppleBlue)
            ProcessRow("doh-resolver (1.1.1.1)", "853", "Encrypted", "${metrics.pingLatencyMs}ms RTT", Color(0xFF00D1FF))
        }
    }
}

@Composable
private fun ProcessRow(
    name: String,
    port: String,
    status: String,
    stat: String,
    statusColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(text = name, color = MacTextPrimary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            Text(text = ":$port", color = MacTextSecondary, fontSize = 11.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = status, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = stat, color = MacTextSecondary, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        }
    }
}
