package com.example.ui.mac

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MacTerminalBg
import com.example.ui.theme.MacTerminalCyan
import com.example.ui.theme.MacTerminalGreen
import com.example.ui.theme.MacTerminalYellow
import com.example.ui.theme.WarpOrange
import com.example.vpn.ConnectionState
import com.example.vpn.WarpVpnManager

data class TerminalLog(
    val text: String,
    val color: Color = Color(0xFFD4D4D4)
)

@Composable
fun TerminalWindow(
    isPersian: Boolean
) {
    val connectionState by WarpVpnManager.stateFlow.collectAsState()
    val metrics by WarpVpnManager.metricsFlow.collectAsState()
    val config by WarpVpnManager.configFlow.collectAsState()

    var inputCmd by remember { mutableStateOf("") }
    val logs = remember {
        mutableStateListOf(
            TerminalLog("Last login: Tue Oct 6 12:45:11 on ttys002", Color(0xFF888888)),
            TerminalLog("Cloudflare WARP CLI Diagnostic Environment v2.5.0-darwin", MacTerminalCyan),
            TerminalLog("Type 'help' or tap shortcuts below to inspect network tunnels.\n", Color(0xFFAAAAAA)),
            TerminalLog("warp@macbook-pro ~ % warp-cli status", MacTerminalGreen),
            TerminalLog("Status update: Connected", Color(0xFF34C759)),
            TerminalLog("Mode: WARP (${config.protocol.badge})", Color(0xFFE0E0E0)),
            TerminalLog("Colocation Edge: ${config.endpoint.location} (${config.endpoint.countryCode})", Color(0xFFE0E0E0)),
            TerminalLog("Endpoint IP: ${config.endpoint.ip}:${config.endpoint.port}", Color(0xFFE0E0E0)),
            TerminalLog("Client Virtual IP: 172.16.0.2", Color(0xFFE0E0E0))
        )
    }

    val listState = rememberLazyListState()

    LaunchedEffect(logs.size) {
        listState.animateScrollToItem(logs.size - 1)
    }

    fun executeCommand(cmd: String) {
        val trimmed = cmd.trim().lowercase()
        logs.add(TerminalLog("warp@macbook-pro ~ % $cmd", MacTerminalGreen))

        when {
            trimmed == "help" -> {
                logs.add(TerminalLog("Available commands:\n  warp-cli status   - Show tunnel status\n  curl trace        - Cloudflare CDN trace\n  wg show           - WireGuard kernel interface info\n  ping              - Test latency to Cloudflare Anycast\n  clear             - Clear terminal window", MacTerminalYellow))
            }
            trimmed.contains("status") -> {
                logs.add(TerminalLog("Status: ${if (connectionState.isConnected) "Connected" else "Disconnected"}", if (connectionState.isConnected) Color(0xFF34C759) else Color(0xFFFF453A)))
                logs.add(TerminalLog("Protocol: ${config.protocol.title} (${config.protocol.badge})", Color.White))
                logs.add(TerminalLog("Colo: ${config.endpoint.location} [${config.endpoint.countryCode}]", Color.White))
                logs.add(TerminalLog("MTU: ${config.mtu} | Noise: ${config.noisePacketSize} bytes", Color(0xFFAAAAAA)))
            }
            trimmed.contains("trace") -> {
                logs.add(TerminalLog("fl=45f123\nh=www.cloudflare.com\nip=172.16.0.2\nts=1696583921.412\nvisit_scheme=https\nuag=Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)\ncolo=${config.endpoint.countryCode}\nsliver=none\nhttp=http/3\nloc=${config.endpoint.countryCode}\ntls=TLSv1.3\nsni=plaintext\nwarp=${if (connectionState.isConnected) "on" else "off"}\ngateway=off\nrwarp=${if (config.isWarpPlusActive) "plus" else "off"}", MacTerminalCyan))
            }
            trimmed.contains("wg") || trimmed.contains("wireguard") -> {
                logs.add(TerminalLog("interface: warp0\n  public key: bm9uY2UtZHVtbXktY2xvdWRmbGFyZS1rZXk=\n  private key: (hidden)\n  listening port: 51820\n\npeer: ${config.endpoint.ip}:${config.endpoint.port}\n  endpoint: ${config.endpoint.ip}:${config.endpoint.port}\n  allowed ips: 0.0.0.0/0, ::/0\n  latest handshake: 12 seconds ago\n  transfer: ${formatBytes(metrics.totalDownloadedBytes)} received, ${formatBytes(metrics.totalUploadedBytes)} sent", Color(0xFFD4D4D4)))
            }
            trimmed.contains("ping") -> {
                logs.add(TerminalLog("PING ${config.endpoint.ip} (56 data bytes):\n64 bytes from ${config.endpoint.ip}: icmp_seq=0 ttl=58 time=${config.endpoint.latencyMs ?: 28}.4 ms\n64 bytes from ${config.endpoint.ip}: icmp_seq=1 ttl=58 time=${(config.endpoint.latencyMs ?: 28) + 1}.1 ms\n64 bytes from ${config.endpoint.ip}: icmp_seq=2 ttl=58 time=${(config.endpoint.latencyMs ?: 28) - 1}.8 ms\n--- ${config.endpoint.ip} ping statistics ---\n3 packets transmitted, 3 packets received, 0.0% packet loss", MacTerminalYellow))
            }
            trimmed == "clear" -> {
                logs.clear()
            }
            else -> {
                logs.add(TerminalLog("zsh: command not found: $cmd. Try 'warp-cli status' or 'help'", Color(0xFFFF453A)))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MacTerminalBg)
            .padding(10.dp)
    ) {
        // Quick Action Command Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            TerminalQuickPill("warp-cli status") { executeCommand("warp-cli status") }
            TerminalQuickPill("curl trace") { executeCommand("curl trace") }
            TerminalQuickPill("wg show") { executeCommand("wg show") }
            TerminalQuickPill("ping") { executeCommand("ping") }
            TerminalQuickPill("clear") { executeCommand("clear") }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Terminal Output Screen
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(logs) { log ->
                Text(
                    text = log.text,
                    color = log.color,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 15.sp
                )
            }
        }

        // Terminal Interactive Input Line
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
                .background(Color(0xFF1A1A22))
                .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "warp % ",
                color = MacTerminalGreen,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )

            BasicTextField(
                value = inputCmd,
                onValueChange = { inputCmd = it },
                textStyle = TextStyle(
                    color = Color.White,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                ),
                cursorBrush = SolidColor(MacTerminalGreen),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                singleLine = true
            )

            Text(
                text = "RETURN ↵",
                color = Color.White.copy(alpha = 0.4f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable {
                    if (inputCmd.isNotBlank()) {
                        executeCommand(inputCmd)
                        inputCmd = ""
                    }
                }
            )
        }
    }
}

@Composable
private fun TerminalQuickPill(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = Color(0xFF00D1FF),
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium
        )
    }
}
