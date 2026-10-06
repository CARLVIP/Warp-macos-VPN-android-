package com.example.ui.mac

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppleBlue
import com.example.ui.theme.AppleGreen
import com.example.ui.theme.MacCardBg
import com.example.ui.theme.MacTextPrimary
import com.example.ui.theme.MacTextSecondary
import com.example.ui.theme.WarpCyan
import com.example.ui.theme.WarpOrange
import com.example.vpn.DnsMode
import com.example.vpn.WarpEndpoint
import com.example.vpn.WarpProtocol
import com.example.vpn.WarpVpnManager

@Composable
fun SettingsWindow(
    isPersian: Boolean
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val config by WarpVpnManager.configFlow.collectAsState()
    val endpoints by WarpVpnManager.endpointsList.collectAsState()

    val tabs = if (isPersian) {
        listOf("پروتکل‌ها", "سرورها و آی‌پی", "دی‌ان‌اس", "وارپ پلاس", "پیشرفته")
    } else {
        listOf("Protocols", "Endpoints", "DNS", "WARP+", "Advanced")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF191922))
    ) {
        // macOS Tab Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF15151D))
                .border(0.5.dp, Color.White.copy(alpha = 0.1f))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEachIndexed { index, tabName ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) Color.White.copy(alpha = 0.14f) else Color.Transparent)
                        .clickable { selectedTab = index }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tabName,
                        color = if (isSelected) MacTextPrimary else MacTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1
                    )
                }
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            when (selectedTab) {
                0 -> ProtocolsTab(isPersian = isPersian, selectedProtocol = config.protocol)
                1 -> EndpointsTab(isPersian = isPersian, selectedEndpoint = config.endpoint, endpoints = endpoints)
                2 -> DnsTab(isPersian = isPersian, config = config)
                3 -> WarpPlusTab(isPersian = isPersian, config = config)
                4 -> AdvancedTab(isPersian = isPersian, config = config)
            }
        }
    }
}

@Composable
private fun ProtocolsTab(
    isPersian: Boolean,
    selectedProtocol: WarpProtocol
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = if (isPersian) "انتخاب پروتکل تونل وارپ:" else "Select WARP Tunneling Protocol:",
            color = MacTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        WarpProtocol.values().forEach { proto ->
            val isSelected = selectedProtocol == proto
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) WarpOrange.copy(alpha = 0.18f) else MacCardBg)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) WarpOrange else Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable {
                        WarpVpnManager.setProtocol(proto)
                    }
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { WarpVpnManager.setProtocol(proto) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = WarpOrange,
                            unselectedColor = Color.White.copy(alpha = 0.4f)
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isPersian) proto.titleFa else proto.title,
                                color = MacTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(WarpOrange.copy(alpha = 0.25f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "${proto.badge} • Port ${proto.defaultPort}",
                                    color = WarpOrange,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = if (isPersian) proto.descriptionFa else proto.description,
                            color = MacTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EndpointsTab(
    isPersian: Boolean,
    selectedEndpoint: WarpEndpoint,
    endpoints: List<WarpEndpoint>
) {
    var customIp by remember { mutableStateOf("") }
    var customPort by remember { mutableStateOf("2408") }
    var customName by remember { mutableStateOf("Custom Clean IP") }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isPersian) "سرورهای لبه و آی‌پی تمیز:" else "Anycast Endpoints & Clean IPs:",
                color = MacTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { WarpVpnManager.pingAllEndpoints() },
                    colors = ButtonDefaults.buttonColors(containerColor = WarpOrange),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Ping",
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (isPersian) "تست پینگ همه" else "Ping All",
                        fontSize = 10.sp
                    )
                }

                Button(
                    onClick = { showAddDialog = !showAddDialog },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = if (isPersian) "افزودن" else "Add",
                        fontSize = 10.sp
                    )
                }
            }
        }

        AnimatedVisibility(visible = showAddDialog) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.06f))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(
                    value = customIp,
                    onValueChange = { customIp = it },
                    label = { Text("IP Address (e.g. 162.159.192.1)", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WarpOrange,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = customPort,
                        onValueChange = { customPort = it },
                        label = { Text("Port (2408)", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarpOrange,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        label = { Text("Name", fontSize = 11.sp) },
                        modifier = Modifier.weight(2f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WarpOrange,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }
                Button(
                    onClick = {
                        if (customIp.isNotEmpty()) {
                            WarpVpnManager.addCustomEndpoint(
                                ip = customIp.trim(),
                                port = customPort.toIntOrNull() ?: 2408,
                                name = customName.trim()
                            )
                            showAddDialog = false
                            customIp = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppleGreen),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save Endpoint", fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(endpoints) { ep ->
                val isSelected = selectedEndpoint.id == ep.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) AppleBlue.copy(alpha = 0.2f) else MacCardBg)
                        .border(
                            1.dp,
                            if (isSelected) AppleBlue else Color.White.copy(alpha = 0.08f),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { WarpVpnManager.setEndpoint(ep) }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = ep.flag, fontSize = 16.sp)
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isPersian) ep.locationFa else ep.location,
                                    color = MacTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (ep.isFastest) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(AppleGreen.copy(alpha = 0.3f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "FASTEST",
                                            color = AppleGreen,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${ep.ip}:${ep.port}",
                                color = MacTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${ep.latencyMs ?: 35} ms",
                            color = when {
                                (ep.latencyMs ?: 50) < 30 -> AppleGreen
                                (ep.latencyMs ?: 50) < 60 -> WarpOrange
                                else -> Color.Red
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isSelected) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Active",
                                tint = AppleBlue,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DnsTab(
    isPersian: Boolean,
    config: com.example.vpn.WarpSettingsConfig
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = if (isPersian) "تنظیمات دی‌ان‌اس Cloudflare (1.1.1.1):" else "Cloudflare 1.1.1.1 DNS Configuration:",
            color = MacTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        DnsMode.values().forEach { mode ->
            val isSelected = config.dnsMode == mode
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) AppleBlue.copy(alpha = 0.16f) else MacCardBg)
                    .border(
                        1.dp,
                        if (isSelected) AppleBlue else Color.White.copy(alpha = 0.08f),
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { WarpVpnManager.setDnsMode(mode) }
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { WarpVpnManager.setDnsMode(mode) },
                        colors = RadioButtonDefaults.colors(selectedColor = AppleBlue)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isPersian) mode.labelFa else mode.label,
                            color = MacTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${mode.primaryIp} , ${mode.secondaryIp}",
                            color = AppleBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = mode.description,
                            color = MacTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WarpPlusTab(
    isPersian: Boolean,
    config: com.example.vpn.WarpSettingsConfig
) {
    var keyInput by remember { mutableStateOf(config.warpPlusKey) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (isPersian) "حساب کاربری وارپ پلاس (WARP+ Turbo):" else "WARP+ Account & License Key:",
            color = MacTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (config.isWarpPlusActive) WarpCyan.copy(alpha = 0.12f) else MacCardBg)
                .border(1.dp, if (config.isWarpPlusActive) WarpCyan else Color.White.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VpnKey,
                        contentDescription = "Key",
                        tint = if (config.isWarpPlusActive) WarpCyan else MacTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (config.isWarpPlusActive) "WARP+ License Active (Unlimited Argo Bandwidth)" else "WARP Free Tier",
                        color = if (config.isWarpPlusActive) WarpCyan else MacTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = if (isPersian)
                        "لایسنس وارپ پلاس ترافیک شما را از مسیرهای فیبرنوری اختصاصی آرگو هدایت کرده و پینگ را تا ۳۰٪ کاهش می‌دهد."
                    else
                        "WARP+ sends your internet traffic over Cloudflare optimized private backbone routes, reducing latency and packet loss.",
                    color = MacTextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        OutlinedTextField(
            value = keyInput,
            onValueChange = { keyInput = it },
            label = { Text("License Key (26 characters)", fontSize = 11.sp) },
            placeholder = { Text("xxxxxxxx-xxxxxxxx-xxxxxxxxxx", fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = WarpOrange,
                unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    WarpVpnManager.setWarpPlusKey(keyInput)
                },
                colors = ButtonDefaults.buttonColors(containerColor = WarpOrange),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(if (isPersian) "اعمال کلید" else "Apply Key", fontSize = 11.sp)
            }

            Button(
                onClick = {
                    WarpVpnManager.generateFreeWarpPlusKey()
                    keyInput = WarpVpnManager.configFlow.value.warpPlusKey
                },
                colors = ButtonDefaults.buttonColors(containerColor = AppleBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(if (isPersian) "دریافت کلید وارپ+" else "Generate Free Key", fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun AdvancedTab(
    isPersian: Boolean,
    config: com.example.vpn.WarpSettingsConfig
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = if (isPersian) "تنظیمات پیشرفته مهندسی شبکه وارپ:" else "Advanced Tunnel & MTU Configuration:",
            color = MacTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        // MTU Slider
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "MTU (Maximum Transmission Unit)", color = MacTextPrimary, fontSize = 12.sp)
                Text(text = "${config.mtu} bytes", color = WarpOrange, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = config.mtu.toFloat(),
                onValueChange = { WarpVpnManager.setMtu(it.toInt()) },
                valueRange = 1280f..1420f,
                steps = 14,
                colors = SliderDefaults.colors(
                    thumbColor = WarpOrange,
                    activeTrackColor = WarpOrange
                )
            )
            Text(
                text = if (isPersian) "برای شبکه‌های دارای اختلال، مقدار ۱۲۸۰ یا ۱۳۲۰ پایداری بهتری ایجاد می‌کند." else "Recommended: 1280 for restricted mobile carriers, 1420 for stable broadband.",
                color = MacTextSecondary,
                fontSize = 10.sp
            )
        }

        // Noise packet size (Anti-DPI)
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Anti-DPI Noise Packet Size", color = MacTextPrimary, fontSize = 12.sp)
                Text(text = "${config.noisePacketSize} bytes", color = WarpCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = config.noisePacketSize.toFloat(),
                onValueChange = { WarpVpnManager.setNoise(it.toInt()) },
                valueRange = 0f..50f,
                steps = 10,
                colors = SliderDefaults.colors(
                    thumbColor = WarpCyan,
                    activeTrackColor = WarpCyan
                )
            )
            Text(
                text = if (isPersian) "تزریق پکت‌های نویز در شروع ارتباط جهت فریب سامانه‌های فیلترینگ عمیق (DPI)." else "Injects random header padding to camouflage handshake against Deep Packet Inspection.",
                color = MacTextSecondary,
                fontSize = 10.sp
            )
        }

        // Domestic Traffic Bypass Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(MacCardBg)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isPersian) "عدم عبور سایت‌های داخلی (Bypass Domestic)" else "Bypass Domestic Websites",
                    color = MacTextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (isPersian) "سایت‌های بانکی و سامانه‌های اداری بدون فیلترشکن باز می‌شوند." else "Routes national/local services directly to prevent bank blocking.",
                    color = MacTextSecondary,
                    fontSize = 10.sp
                )
            }
            Switch(
                checked = config.bypassDomesticTraffic,
                onCheckedChange = { WarpVpnManager.toggleDomesticTrafficBypass(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = AppleGreen
                )
            )
        }
    }
}
