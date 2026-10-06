package com.example.vpn

enum class WarpProtocol(
    val title: String,
    val titleFa: String,
    val description: String,
    val descriptionFa: String,
    val defaultPort: Int,
    val badge: String
) {
    WIREGUARD(
        title = "WARP Standard (WireGuard)",
        titleFa = "وارپ استاندارد (وایرگارد)",
        description = "Standard Cloudflare WireGuard UDP tunnel over port 2408",
        descriptionFa = "تونل استاندارد WireGuard کلودفلر روی پورت ۲۴۰۸ با کمترین پینگ",
        defaultPort = 2408,
        badge = "WireGuard"
    ),
    WARP_PLUS(
        title = "WARP+ (Argo Routing)",
        titleFa = "وارپ پلاس (شبکه اختصاصی آرگو)",
        description = "Intelligent Cloudflare backbone routing with reduced hops & jitter",
        descriptionFa = "مسیریابی پرسرعت از طریق بک‌بون اختصاصی کلودفلر برای سرعت حداکثری",
        defaultPort = 2408,
        badge = "WARP+"
    ),
    MASQUE_HTTP3(
        title = "WARP MASQUE (HTTP/3)",
        titleFa = "وارپ ماسک (HTTP/3 - QUIC)",
        description = "UDP-in-HTTP/3 protocol used by Zero Trust to bypass UDP filtering",
        descriptionFa = "کپسوله‌سازی UDP در لایه HTTP/3 برای عبور قطعی از محدودیت‌های پورت",
        defaultPort = 443,
        badge = "HTTP/3"
    ),
    DOH_TUNNEL(
        title = "WARP over DoH (1.1.1.1)",
        titleFa = "وارپ روی DoH امن",
        description = "Encrypted TLS/HTTPS tunnel securing DNS and outbound metadata",
        descriptionFa = "رمزگذاری سرتاسری ترافیک دی‌ان‌اس روی HTTPS با امنیت بالا",
        defaultPort = 853,
        badge = "DoH / TLS"
    ),
    NOISE_ANTI_DPI(
        title = "WARP Anti-Censorship Noise",
        titleFa = "وارپ ضد اختلال و فیلترینگ (Noise)",
        description = "Injects randomized header padding to defeat deep packet inspection (DPI)",
        descriptionFa = "تزریق پکت‌های نویز رندوم هدر برای دور زدن فیلترینگ شدید و DPI",
        defaultPort = 2408,
        badge = "Anti-DPI"
    )
}

data class WarpEndpoint(
    val id: String,
    val ip: String,
    val port: Int = 2408,
    val location: String,
    val locationFa: String,
    val countryCode: String,
    val flag: String,
    var latencyMs: Int? = null,
    var isFastest: Boolean = false
)

enum class DnsMode(
    val label: String,
    val labelFa: String,
    val description: String,
    val primaryIp: String,
    val secondaryIp: String
) {
    CLOUDFLARE_DEFAULT(
        label = "1.1.1.1 (Standard)",
        labelFa = "۱.۱.۱.۱ استاندارد کلودفلر",
        description = "Ultra-fast private DNS resolution without logging",
        primaryIp = "1.1.1.1",
        secondaryIp = "1.0.0.1"
    ),
    MALWARE_BLOCKING(
        label = "1.1.1.2 (Malware Block)",
        labelFa = "۱.۱.۱.۲ مسدودسازی بدافزار",
        description = "Automatic blocking of malicious domains and phishing",
        primaryIp = "1.1.1.2",
        secondaryIp = "1.0.0.2"
    ),
    FAMILY_FILTER(
        label = "1.1.1.3 (Family & Adult)",
        labelFa = "۱.۱.۱.۳ فیلتر خانواده و محتوا",
        description = "Blocks both malware and adult content for safe browsing",
        primaryIp = "1.1.1.3",
        secondaryIp = "1.0.0.3"
    ),
    CUSTOM(
        label = "Custom DNS",
        labelFa = "دی‌ان‌اس سفارشی",
        description = "Specify your preferred upstream DNS servers",
        primaryIp = "8.8.8.8",
        secondaryIp = "8.8.4.4"
    )
}

sealed class ConnectionState {
    object Disconnected : ConnectionState()
    data class Connecting(val progressStage: String, val progressStageFa: String) : ConnectionState()
    data class Connected(val connectedTimeMillis: Long) : ConnectionState()
    data class Disconnecting(val message: String = "") : ConnectionState()
    data class Error(val message: String, val messageFa: String) : ConnectionState()

    val isConnected: Boolean get() = this is Connected
    val isConnecting: Boolean get() = this is Connecting
}

data class WarpMetrics(
    val uploadSpeedKbps: Double = 0.0,
    val downloadSpeedKbps: Double = 0.0,
    val totalUploadedBytes: Long = 0L,
    val totalDownloadedBytes: Long = 0L,
    val pingLatencyMs: Int = 29,
    val clientVirtualIp: String = "172.16.0.2",
    val serverColo: String = "FRA (Frankfurt)",
    val warpStatus: String = "warp=on",
    val packetsTransferred: Long = 0L
)

data class WarpSettingsConfig(
    val protocol: WarpProtocol = WarpProtocol.WIREGUARD,
    val endpoint: WarpEndpoint = WarpEndpointsList.defaultEndpoint,
    val dnsMode: DnsMode = DnsMode.CLOUDFLARE_DEFAULT,
    val customDnsPrimary: String = "8.8.8.8",
    val customDnsSecondary: String = "8.8.4.4",
    val mtu: Int = 1280,
    val noisePacketSize: Int = 16,
    val warpPlusKey: String = "",
    val isWarpPlusActive: Boolean = false,
    val killSwitch: Boolean = false,
    val splitTunnelEnabled: Boolean = false,
    val bypassDomesticTraffic: Boolean = true
)

object WarpEndpointsList {
    val defaultEndpoints = listOf(
        WarpEndpoint("fra1", "162.159.192.1", 2408, "Frankfurt, Germany", "فرانکفورت، آلمان", "DE", "🇩🇪", latencyMs = 28),
        WarpEndpoint("ams1", "162.159.193.10", 2408, "Amsterdam, Netherlands", "آمستردام، هلند", "NL", "🇳🇱", latencyMs = 35),
        WarpEndpoint("dxb1", "162.159.195.1", 2408, "Dubai, UAE", "دبی، امارات", "AE", "🇦🇪", latencyMs = 22),
        WarpEndpoint("ist1", "188.114.97.1", 2408, "Istanbul, Turkey", "استانبول، ترکیه", "TR", "🇹🇷", latencyMs = 31),
        WarpEndpoint("sin1", "188.114.96.1", 2408, "Singapore", "سنگاپور", "SG", "🇸🇬", latencyMs = 64),
        WarpEndpoint("lon1", "162.159.192.5", 2408, "London, United Kingdom", "لندن، بریتانیا", "GB", "🇬🇧", latencyMs = 42),
        WarpEndpoint("tyo1", "162.159.193.5", 2408, "Tokyo, Japan", "توکیو، ژاپن", "JP", "🇯🇵", latencyMs = 78),
        WarpEndpoint("zrh1", "162.159.192.7", 2408, "Zurich, Switzerland", "زوریخ، سوئیس", "CH", "🇨🇭", latencyMs = 38)
    )

    val defaultEndpoint: WarpEndpoint get() = defaultEndpoints[0]
}

enum class ActiveWindow {
    NONE,
    WARP_APP,
    SETTINGS,
    TERMINAL,
    ACTIVITY_MONITOR,
    CLEAN_IP_SCANNER
}
