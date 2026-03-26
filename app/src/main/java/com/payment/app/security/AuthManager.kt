package com.payment.app.security

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class AuthUiState(
    val lockEnabled: Boolean = false,
    val isLocked: Boolean = false,
    val isAuthenticating: Boolean = false,
    val isBiometricAvailable: Boolean = true,
    val message: String? = null,
    val hasPin: Boolean = false,
    val pinAttemptsLeft: Int = 5,
    val pinTemporarilyLocked: Boolean = false
)

class AuthManager(
    private val activity: FragmentActivity,
    private val onGraceUpdated: (Long) -> Unit
) {
    private val strongAuthenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG
    private val weakAuthenticators = BiometricManager.Authenticators.BIOMETRIC_WEAK

    private val executor = ContextCompat.getMainExecutor(activity)
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    private var pinHash: String = ""
    private var unlockGraceEnabled: Boolean = true
    private var unlockGraceUntil: Long = 0L
    private var pinFailedCount: Int = 0
    private var pinLockoutUntil: Long = 0L

    private val graceMillis = 5 * 60 * 1000L
    private val maxPinAttempts = 5
    private val pinLockoutMillis = 30_000L

    private val prompt by lazy {
        BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    _uiState.update {
                        it.copy(
                            isLocked = false,
                            isAuthenticating = false,
                            isBiometricAvailable = true,
                            message = null
                        )
                    }
                    val until = System.currentTimeMillis() + graceMillis
                    unlockGraceUntil = until
                    onGraceUpdated(until)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    val message = when (errorCode) {
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                        BiometricPrompt.ERROR_USER_CANCELED,
                        BiometricPrompt.ERROR_CANCELED -> "認証がキャンセルされました。再度指紋認証してください。"
                        BiometricPrompt.ERROR_LOCKOUT,
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> "認証試行回数の上限です。少し待ってから再試行してください。"
                        else -> errString.toString()
                    }
                    _uiState.update {
                        it.copy(
                            isLocked = it.lockEnabled,
                            isAuthenticating = false,
                            message = message
                        )
                    }
                }

                override fun onAuthenticationFailed() {
                    _uiState.update {
                        it.copy(
                            isLocked = true,
                            isAuthenticating = true,
                            message = "指紋を認識できませんでした。再度お試しください。"
                        )
                    }
                }
            }
        )
    }

    fun updateLockEnabled(enabled: Boolean) {
        _uiState.update {
            it.copy(
                lockEnabled = enabled,
                isLocked = if (enabled) it.isLocked else false,
                isAuthenticating = if (enabled) it.isAuthenticating else false,
                message = if (enabled) it.message else null
            )
        }
        if (!enabled) {
            return
        }
        updateAvailabilityState()
    }

    fun updatePinHash(hash: String) {
        pinHash = hash
        if (hash.isBlank()) {
            pinFailedCount = 0
            pinLockoutUntil = 0L
        }
        _uiState.update {
            it.copy(
                hasPin = hash.isNotBlank(),
                pinAttemptsLeft = maxPinAttempts - pinFailedCount,
                pinTemporarilyLocked = isPinLockoutActive()
            )
        }
    }

    fun updateGraceEnabled(enabled: Boolean) {
        unlockGraceEnabled = enabled
    }

    fun updateGraceUntil(timestamp: Long) {
        unlockGraceUntil = timestamp
    }

    fun onAppForegrounded() {
        if (!_uiState.value.lockEnabled) return
        val now = System.currentTimeMillis()
        if (unlockGraceEnabled && now < unlockGraceUntil) {
            _uiState.update {
                it.copy(
                    isLocked = false,
                    isAuthenticating = false,
                    message = null
                )
            }
            return
        }
        _uiState.update { it.copy(isLocked = true, message = null) }
        requireAuth()
    }

    fun requireAuth() {
        val current = _uiState.value
        if (!current.lockEnabled || current.isAuthenticating || !current.isLocked) return

        val now = System.currentTimeMillis()
        if (unlockGraceEnabled && now < unlockGraceUntil) {
            _uiState.update { it.copy(isLocked = false, isAuthenticating = false, message = null) }
            return
        }

        when (val canAuthenticate = biometricStatus()) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                authenticateWithBiometric(
                    authenticators = strongAuthenticators,
                    subtitle = "指紋認証でロックを解除します"
                )
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                _uiState.update {
                    it.copy(
                        isLocked = true,
                        isAuthenticating = false,
                        isBiometricAvailable = false,
                        message = "端末に指紋が登録されていません。端末設定から指紋を登録してください。"
                    )
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                val weakStatus = biometricStatus(weakAuthenticators)
                if (weakStatus == BiometricManager.BIOMETRIC_SUCCESS) {
                    authenticateWithBiometric(
                        authenticators = weakAuthenticators,
                        subtitle = "生体認証でロックを解除します"
                    )
                } else {
                    _uiState.update {
                        it.copy(
                            isLocked = true,
                            isAuthenticating = false,
                            isBiometricAvailable = false,
                            message = "この端末では指紋認証を利用できません。"
                        )
                    }
                }
            }
            else -> {
                _uiState.update {
                    it.copy(
                        isLocked = true,
                        isAuthenticating = false,
                        isBiometricAvailable = false,
                        message = "生体認証の初期化に失敗しました。コード: $canAuthenticate"
                    )
                }
            }
        }
    }

    private fun updateAvailabilityState() {
        _uiState.update {
            val strongAvailable = biometricStatus(strongAuthenticators) == BiometricManager.BIOMETRIC_SUCCESS
            val weakAvailable = biometricStatus(weakAuthenticators) == BiometricManager.BIOMETRIC_SUCCESS
            it.copy(isBiometricAvailable = strongAvailable || weakAvailable)
        }
    }

    private fun biometricStatus(authenticators: Int = strongAuthenticators): Int =
        BiometricManager.from(activity).canAuthenticate(authenticators)

    private fun authenticateWithBiometric(authenticators: Int, subtitle: String) {
        _uiState.update {
            it.copy(
                isAuthenticating = true,
                isBiometricAvailable = true,
                message = null
            )
        }
        prompt.authenticate(
            BiometricPrompt.PromptInfo.Builder()
                .setTitle("ロック解除")
                .setSubtitle(subtitle)
                .setAllowedAuthenticators(authenticators)
                .setConfirmationRequired(false)
                .build()
        )
    }

    fun verifyPin(pin: String) {
        if (pin.isBlank() || pinHash.isBlank()) {
            _uiState.update { it.copy(message = "PINが設定されていません") }
            return
        }
        if (isPinLockoutActive()) {
            val seconds = ((pinLockoutUntil - System.currentTimeMillis()) / 1000L).coerceAtLeast(1L)
            _uiState.update {
                it.copy(
                    pinTemporarilyLocked = true,
                    message = "PINは${seconds}秒後に再試行できます"
                )
            }
            return
        }
        val hashed = hashPin(pin)
        if (hashed == pinHash) {
            pinFailedCount = 0
            pinLockoutUntil = 0L
            val until = System.currentTimeMillis() + graceMillis
            unlockGraceUntil = until
            onGraceUpdated(until)
            _uiState.update {
                it.copy(
                    isLocked = false,
                    isAuthenticating = false,
                    message = "PINで解除しました",
                    hasPin = true,
                    pinAttemptsLeft = maxPinAttempts,
                    pinTemporarilyLocked = false
                )
            }
        } else {
            pinFailedCount += 1
            val left = (maxPinAttempts - pinFailedCount).coerceAtLeast(0)
            if (left == 0) {
                pinFailedCount = 0
                pinLockoutUntil = System.currentTimeMillis() + pinLockoutMillis
                _uiState.update {
                    it.copy(
                        message = "PIN失敗が続いたため30秒ロックしました",
                        isLocked = true,
                        pinAttemptsLeft = 0,
                        pinTemporarilyLocked = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        message = "PINが違います（残り${left}回）",
                        isLocked = true,
                        pinAttemptsLeft = left,
                        pinTemporarilyLocked = false
                    )
                }
            }
        }
    }

    private fun isPinLockoutActive(): Boolean = System.currentTimeMillis() < pinLockoutUntil

    private fun hashPin(pin: String): String {
        val md = java.security.MessageDigest.getInstance("SHA-256")
        val digest = md.digest(pin.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
}
