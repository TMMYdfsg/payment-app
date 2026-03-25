package com.payment.app.security

import androidx.activity.ComponentActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class AuthManager(
    private val activity: ComponentActivity
) {
    @Volatile
    private var lockEnabled: Boolean = false

    fun updateLockEnabled(enabled: Boolean) {
        lockEnabled = enabled
    }

    fun requireAuthIfNeeded() {
        if (!lockEnabled) return
        val fragmentActivity = activity as? FragmentActivity ?: return
        val bm = BiometricManager.from(activity)
        val canAuth = bm.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
        if (canAuth != BiometricManager.BIOMETRIC_SUCCESS) return

        val executor = ContextCompat.getMainExecutor(activity)
        val prompt = BiometricPrompt(fragmentActivity, executor, object : BiometricPrompt.AuthenticationCallback() {})
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("ロック解除")
            .setSubtitle("生体認証で解除します")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .setConfirmationRequired(false)
            .build()
        prompt.authenticate(info)
    }
}
