package com.payment.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.payment.app.data.datastore.SettingsDataStore
import com.payment.app.navigation.EXTRA_LAUNCH_ROUTE
import com.payment.app.navigation.NavGraph
import com.payment.app.security.AuthManager
import com.payment.app.security.AuthUiState
import com.payment.app.domain.usecase.SetUnlockGraceUntilUseCase
import com.payment.app.ui.theme.PaymentAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject lateinit var settingsDataStore: SettingsDataStore
    @Inject lateinit var setUnlockGraceUntil: SetUnlockGraceUntilUseCase

    private val authManager by lazy { AuthManager(this) { timestamp ->
        lifecycleScope.launch { setUnlockGraceUntil(timestamp) }
    } }
    private var launchRoute by mutableStateOf<String?>(null)
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        launchRoute = intent?.getStringExtra(EXTRA_LAUNCH_ROUTE)
        requestNotificationPermissionIfNeeded()

        lifecycleScope.launch {
            settingsDataStore.lockEnabled.collect { enabled ->
                authManager.updateLockEnabled(enabled)
                if (enabled && lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    authManager.onAppForegrounded()
                }
            }
        }
        lifecycleScope.launch {
            settingsDataStore.pinHash.collect { authManager.updatePinHash(it) }
        }
        lifecycleScope.launch {
            settingsDataStore.unlockGraceEnabled.collect { authManager.updateGraceEnabled(it) }
        }
        lifecycleScope.launch {
            settingsDataStore.unlockGraceUntil.collect { authManager.updateGraceUntil(it) }
        }

        setContent {
            val themeMode by settingsDataStore.themeMode.collectAsState(initial = "system")
            val themeAccent by settingsDataStore.themeAccent.collectAsState(initial = "ocean")
            val authUiState by authManager.uiState.collectAsState()

            PaymentAppTheme(themeMode = themeMode, accent = themeAccent) {
                Box(modifier = Modifier.fillMaxSize()) {
                    NavGraph(
                        launchRoute = launchRoute,
                        onLaunchRouteConsumed = { launchRoute = null }
                    )
                    if (authUiState.lockEnabled && authUiState.isLocked) {
                        AuthLockOverlay(
                            uiState = authUiState,
                            onRetry = authManager::requireAuth,
                            onPinSubmit = { authManager.verifyPin(it) }
                        )
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        authManager.onAppForegrounded()
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        launchRoute = intent.getStringExtra(EXTRA_LAUNCH_ROUTE)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

@Composable
private fun AuthLockOverlay(
    uiState: AuthUiState,
    onRetry: () -> Unit,
    onPinSubmit: (String) -> Unit
) {
    var pin by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xCC070B12))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "アプリはロックされています",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.message ?: "指紋または生体認証でロックを解除してください。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))
                if (uiState.isAuthenticating) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = onRetry,
                        enabled = uiState.lockEnabled
                    ) {
                        Text(if (uiState.isBiometricAvailable) "指紋認証で解除" else "生体認証を再確認")
                    }
                }
                if (uiState.hasPin) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "PIN: ${"●".repeat(pin.length).padEnd(6, '○')}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (uiState.pinTemporarilyLocked) "PINは一時ロック中" else "PIN残り試行回数: ${uiState.pinAttemptsLeft}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    KeypadRow("1", "2", "3", enabled = !uiState.pinTemporarilyLocked) { key -> if (pin.length < 6) pin += key }
                    Spacer(modifier = Modifier.height(6.dp))
                    KeypadRow("4", "5", "6", enabled = !uiState.pinTemporarilyLocked) { key -> if (pin.length < 6) pin += key }
                    Spacer(modifier = Modifier.height(6.dp))
                    KeypadRow("7", "8", "9", enabled = !uiState.pinTemporarilyLocked) { key -> if (pin.length < 6) pin += key }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { pin = "" },
                            modifier = Modifier.weight(1f),
                            enabled = !uiState.pinTemporarilyLocked
                        ) { Text("C") }
                        Button(
                            onClick = { if (pin.length < 6) pin += "0" },
                            modifier = Modifier.weight(1f),
                            enabled = !uiState.pinTemporarilyLocked
                        ) { Text("0") }
                        Button(
                            onClick = { if (pin.isNotEmpty()) pin = pin.dropLast(1) },
                            modifier = Modifier.weight(1f),
                            enabled = !uiState.pinTemporarilyLocked
                        ) { Text("←") }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            onPinSubmit(pin)
                            pin = ""
                        },
                        enabled = pin.length in 4..6 && !uiState.pinTemporarilyLocked,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("PINで解除")
                    }
                }
            }
        }
    }
}

@Composable
private fun KeypadRow(
    first: String,
    second: String,
    third: String,
    enabled: Boolean,
    onTap: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = { onTap(first) }, modifier = Modifier.weight(1f), enabled = enabled) { Text(first) }
        Button(onClick = { onTap(second) }, modifier = Modifier.weight(1f), enabled = enabled) { Text(second) }
        Button(onClick = { onTap(third) }, modifier = Modifier.weight(1f), enabled = enabled) { Text(third) }
    }
}
