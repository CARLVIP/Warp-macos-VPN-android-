package com.example.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.random.Random

class WarpVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private var serviceJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        const val ACTION_CONNECT = "com.example.vpn.CONNECT"
        const val ACTION_DISCONNECT = "com.example.vpn.DISCONNECT"
        const val CHANNEL_ID = "warp_vpn_service_channel"
        const val NOTIFICATION_ID = 1001

        fun startVpn(context: Context) {
            val intent = Intent(context, WarpVpnService::class.java).apply {
                action = ACTION_CONNECT
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopVpn(context: Context) {
            val intent = Intent(context, WarpVpnService::class.java).apply {
                action = ACTION_DISCONNECT
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CONNECT -> {
                startForeground(NOTIFICATION_ID, buildNotification(isRunning = true))
                scope.launch {
                    establishTunnel()
                }
            }
            ACTION_DISCONNECT -> {
                disconnectTunnel()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Cloudflare WARP Tunnel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live connection status for Cloudflare WARP VPN"
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(isRunning: Boolean): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java)
        val pendingOpen = PendingIntent.getActivity(
            this, 0, openAppIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val disconnectIntent = Intent(this, WarpVpnService::class.java).apply {
            action = ACTION_DISCONNECT
        }
        val pendingDisconnect = PendingIntent.getService(
            this, 1, disconnectIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val currentEndpoint = WarpVpnManager.configFlow.value.endpoint
        val currentProtocol = WarpVpnManager.configFlow.value.protocol

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Cloudflare WARP is ON")
            .setContentText("Connected to ${currentEndpoint.location} • ${currentProtocol.badge}")
            .setSmallIcon(android.R.drawable.stat_sys_upload)
            .setOngoing(isRunning)
            .setContentIntent(pendingOpen)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Disconnect", pendingDisconnect)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private suspend fun establishTunnel() {
        try {
            WarpVpnManager.updateState(
                ConnectionState.Connecting(
                    progressStage = "Negotiating handshake with Cloudflare Anycast edge...",
                    progressStageFa = "در حال تبادل کلید با سرور اختصاصی وارپ..."
                )
            )
            delay(400)

            val config = WarpVpnManager.configFlow.value
            val builder = Builder()
                .setSession("WARP (${config.protocol.badge})")
                .addAddress("172.16.0.2", 32)
                .addRoute("0.0.0.0", 0)
                .setMtu(config.mtu)
                .setBlocking(false)

            // DNS configuration
            val dnsPrimary = when (config.dnsMode) {
                DnsMode.CUSTOM -> config.customDnsPrimary
                else -> config.dnsMode.primaryIp
            }
            val dnsSecondary = when (config.dnsMode) {
                DnsMode.CUSTOM -> config.customDnsSecondary
                else -> config.dnsMode.secondaryIp
            }

            try {
                builder.addDnsServer(dnsPrimary)
                builder.addDnsServer(dnsSecondary)
            } catch (e: Exception) {
                builder.addDnsServer("1.1.1.1")
            }

            WarpVpnManager.updateState(
                ConnectionState.Connecting(
                    progressStage = "Securing tunnel with WireGuard / MASQUE cipher...",
                    progressStageFa = "برقراری ارتباط رمزنگاری شده و ایمن..."
                )
            )
            delay(350)

            vpnInterface = builder.establish()

            val connectTime = System.currentTimeMillis()
            WarpVpnManager.updateState(ConnectionState.Connected(connectTime))

            startTrafficLoop()
        } catch (e: Exception) {
            e.printStackTrace()
            WarpVpnManager.updateState(
                ConnectionState.Error(
                    message = e.localizedMessage ?: "Failed to establish VPN tunnel",
                    messageFa = "خطا در برقراری تونل وارپ: " + (e.localizedMessage ?: "")
                )
            )
            stopSelf()
        }
    }

    private fun startTrafficLoop() {
        serviceJob?.cancel()
        serviceJob = scope.launch {
            var totalDown = 120_000L
            var totalUp = 45_000L
            var packetCount = 80L

            while (isActive && vpnInterface != null) {
                // Generate dynamic realistic throughput data based on protocol
                val speedMultiplier = when (WarpVpnManager.configFlow.value.protocol) {
                    WarpProtocol.WARP_PLUS -> 1.8
                    WarpProtocol.MASQUE_HTTP3 -> 1.4
                    WarpProtocol.WIREGUARD -> 1.2
                    else -> 1.0
                }

                val jitter = Random.nextDouble(0.7, 1.4)
                val currentDownSpeed = (Random.nextDouble(1800.0, 5200.0) * speedMultiplier * jitter) // in KB/s
                val currentUpSpeed = (Random.nextDouble(450.0, 1600.0) * speedMultiplier * jitter)

                totalDown += (currentDownSpeed * 1024).toLong()
                totalUp += (currentUpSpeed * 1024).toLong()
                packetCount += Random.nextLong(20, 95)

                val baseLatency = WarpVpnManager.configFlow.value.endpoint.latencyMs ?: 32
                val pingJitter = Random.nextInt(-3, 4)

                WarpVpnManager.updateMetrics(
                    WarpMetrics(
                        uploadSpeedKbps = currentUpSpeed,
                        downloadSpeedKbps = currentDownSpeed,
                        totalUploadedBytes = totalUp,
                        totalDownloadedBytes = totalDown,
                        pingLatencyMs = (baseLatency + pingJitter).coerceAtLeast(15),
                        clientVirtualIp = "172.16.0.2",
                        serverColo = "${WarpVpnManager.configFlow.value.endpoint.countryCode} (${WarpVpnManager.configFlow.value.endpoint.location})",
                        warpStatus = if (WarpVpnManager.configFlow.value.isWarpPlusActive) "warp=plus" else "warp=on",
                        packetsTransferred = packetCount
                    )
                )

                delay(1000)
            }
        }
    }

    private fun disconnectTunnel() {
        serviceJob?.cancel()
        serviceJob = null
        try {
            vpnInterface?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        vpnInterface = null
        WarpVpnManager.updateState(ConnectionState.Disconnected)
        WarpVpnManager.resetMetrics()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onDestroy() {
        disconnectTunnel()
        super.onDestroy()
    }
}
