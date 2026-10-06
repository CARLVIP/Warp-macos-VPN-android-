package com.example

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.mac.MacOSDesktop
import com.example.ui.theme.MyApplicationTheme
import com.example.vpn.ConnectionState
import com.example.vpn.WarpVpnManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    WarpAppRoot(activity = this)
                }
            }
        }
    }
}

@Composable
fun WarpAppRoot(activity: ComponentActivity) {
    val connectionState by WarpVpnManager.stateFlow.collectAsState()

    // VPN Permission Launcher
    val vpnPrepareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            WarpVpnManager.connect(activity)
        } else {
            Toast.makeText(
                activity,
                "VPN permission required to connect to WARP",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Notification Permission Launcher (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* granted or denied */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    fun handleToggleVpn() {
        if (connectionState.isConnected || connectionState.isConnecting) {
            WarpVpnManager.disconnect(activity)
        } else {
            val prepareIntent = VpnService.prepare(activity)
            if (prepareIntent != null) {
                vpnPrepareLauncher.launch(prepareIntent)
            } else {
                WarpVpnManager.connect(activity)
            }
        }
    }

    MacOSDesktop(
        onToggleVpn = { handleToggleVpn() }
    )
}
