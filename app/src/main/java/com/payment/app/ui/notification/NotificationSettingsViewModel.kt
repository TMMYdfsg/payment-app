package com.payment.app.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.payment.app.data.db.entity.NotificationSettingEntity
import com.payment.app.domain.usecase.CreateDriveFolderUseCase
import com.payment.app.domain.usecase.DownloadSharedBackupFromDriveUseCase
import com.payment.app.domain.usecase.DriveMember
import com.payment.app.domain.usecase.ExportBackupJsonUseCase
import com.payment.app.domain.usecase.GetCloudSyncPrefsUseCase
import com.payment.app.domain.usecase.GetDriveAccessTokenUseCase
import com.payment.app.domain.usecase.GetDriveFolderIdUseCase
import com.payment.app.domain.usecase.GetDriveGroupNameUseCase
import com.payment.app.domain.usecase.GetLockEnabledUseCase
import com.payment.app.domain.usecase.GetNotificationSettingsUseCase
import com.payment.app.domain.usecase.GetPinHashUseCase
import com.payment.app.domain.usecase.GetSyncAccountEmailUseCase
import com.payment.app.domain.usecase.GetThemeAccentUseCase
import com.payment.app.domain.usecase.GetThemeModeUseCase
import com.payment.app.domain.usecase.GetUnlockGraceEnabledUseCase
import com.payment.app.domain.usecase.ImportBackupJsonUseCase
import com.payment.app.domain.usecase.InviteDriveMemberUseCase
import com.payment.app.domain.usecase.ListDriveMembersUseCase
import com.payment.app.domain.usecase.RemoveDriveMemberUseCase
import com.payment.app.domain.usecase.SetDriveAccessTokenUseCase
import com.payment.app.domain.usecase.SetDriveFolderIdUseCase
import com.payment.app.domain.usecase.SetDriveGroupNameUseCase
import com.payment.app.domain.usecase.SetCloudSyncEnabledUseCase
import com.payment.app.domain.usecase.SetCloudLastSyncUseCase
import com.payment.app.domain.usecase.SetLockEnabledUseCase
import com.payment.app.domain.usecase.SetPinHashUseCase
import com.payment.app.domain.usecase.SetUnlockGraceEnabledUseCase
import com.payment.app.domain.usecase.ClearPinHashUseCase
import com.payment.app.domain.usecase.SetSyncAccountEmailUseCase
import com.payment.app.domain.usecase.SetThemeAccentUseCase
import com.payment.app.domain.usecase.SetThemeModeUseCase
import com.payment.app.domain.usecase.UpdateDriveMemberRoleUseCase
import com.payment.app.domain.usecase.UpsertNotificationSettingUseCase
import com.payment.app.domain.usecase.UpsertSharedBackupToDriveUseCase
import com.payment.app.notifications.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject

data class InviteStatusItem(
    val email: String,
    val status: String,
    val detail: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

data class BackupSnapshotSummary(
    val exportedAt: Long,
    val cards: Int,
    val payments: Int,
    val accounts: Int,
    val budgets: Int,
    val subscriptions: Int,
    val installments: Int,
    val totalPaymentAmount: Long
)

data class SyncConflictPreview(
    val local: BackupSnapshotSummary,
    val remote: BackupSnapshotSummary
)

data class NotificationUiState(
    val settings: NotificationSettingEntity? = null,
    val themeMode: String = "system",
    val themeAccent: String = "ocean",
    val lockEnabled: Boolean = false,
    val driveAccessToken: String = "",
    val driveFolderId: String = "",
    val driveGroupName: String = "Payment App Shared Group",
    val syncAccountEmail: String = "",
    val hasPin: Boolean = false,
    val unlockGraceEnabled: Boolean = true,
    val cloudSyncEnabled: Boolean = false,
    val cloudLastSyncStatus: String = "",
    val cloudLastSyncAt: Long = 0L,
    val driveMembers: List<DriveMember> = emptyList(),
    val inviteStatuses: List<InviteStatusItem> = emptyList(),
    val memberAuditLogs: List<String> = emptyList(),
    val isDriveMembersLoading: Boolean = false,
    val syncConflictPreview: SyncConflictPreview? = null,
    val syncStatusMessage: String? = null,
    val isSyncing: Boolean = false,
    val isLoading: Boolean = true
)

private data class SyncActionState(
    val syncStatusMessage: String? = null,
    val isSyncing: Boolean = false,
    val driveMembers: List<DriveMember> = emptyList(),
    val inviteStatuses: List<InviteStatusItem> = emptyList(),
    val memberAuditLogs: List<String> = emptyList(),
    val isDriveMembersLoading: Boolean = false,
    val syncConflictPreview: SyncConflictPreview? = null,
    val pendingRemoteJson: String? = null
)

private data class NotificationBaseState(
    val settings: NotificationSettingEntity? = null,
    val themeMode: String = "system",
    val themeAccent: String = "ocean",
    val lockEnabled: Boolean = false,
    val driveAccessToken: String = "",
    val driveFolderId: String = "",
    val driveGroupName: String = "Payment App Shared Group",
    val syncAccountEmail: String = "",
    val hasPin: Boolean = false,
    val unlockGraceEnabled: Boolean = true,
    val cloudSyncEnabled: Boolean = false,
    val cloudLastSyncStatus: String = "",
    val cloudLastSyncAt: Long = 0L
)

private data class DriveSyncState(
    val accessToken: String,
    val folderId: String,
    val groupName: String,
    val accountEmail: String,
    val cloudSyncEnabled: Boolean,
    val cloudLastSyncStatus: String,
    val cloudLastSyncAt: Long
)

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    getSettingsUseCase: GetNotificationSettingsUseCase,
    getThemeModeUseCase: GetThemeModeUseCase,
    getThemeAccentUseCase: GetThemeAccentUseCase,
    getLockEnabledUseCase: GetLockEnabledUseCase,
    getPinHashUseCase: GetPinHashUseCase,
    getUnlockGraceEnabledUseCase: GetUnlockGraceEnabledUseCase,
    getDriveAccessTokenUseCase: GetDriveAccessTokenUseCase,
    getDriveFolderIdUseCase: GetDriveFolderIdUseCase,
    getDriveGroupNameUseCase: GetDriveGroupNameUseCase,
    getSyncAccountEmailUseCase: GetSyncAccountEmailUseCase,
    getCloudSyncPrefsUseCase: GetCloudSyncPrefsUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    private val setThemeAccentUseCase: SetThemeAccentUseCase,
    private val setLockEnabledUseCase: SetLockEnabledUseCase,
    private val setPinHashUseCase: SetPinHashUseCase,
    private val clearPinHashUseCase: ClearPinHashUseCase,
    private val setUnlockGraceEnabledUseCase: SetUnlockGraceEnabledUseCase,
    private val setDriveAccessTokenUseCase: SetDriveAccessTokenUseCase,
    private val setDriveFolderIdUseCase: SetDriveFolderIdUseCase,
    private val setDriveGroupNameUseCase: SetDriveGroupNameUseCase,
    private val setSyncAccountEmailUseCase: SetSyncAccountEmailUseCase,
    private val setCloudSyncEnabledUseCase: SetCloudSyncEnabledUseCase,
    private val setCloudLastSyncUseCase: SetCloudLastSyncUseCase,
    private val upsertNotificationSettingUseCase: UpsertNotificationSettingUseCase,
    private val exportBackupJsonUseCase: ExportBackupJsonUseCase,
    private val importBackupJsonUseCase: ImportBackupJsonUseCase,
    private val createDriveFolderUseCase: CreateDriveFolderUseCase,
    private val inviteDriveMemberUseCase: InviteDriveMemberUseCase,
    private val listDriveMembersUseCase: ListDriveMembersUseCase,
    private val updateDriveMemberRoleUseCase: UpdateDriveMemberRoleUseCase,
    private val removeDriveMemberUseCase: RemoveDriveMemberUseCase,
    private val upsertSharedBackupToDriveUseCase: UpsertSharedBackupToDriveUseCase,
    private val downloadSharedBackupFromDriveUseCase: DownloadSharedBackupFromDriveUseCase,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    private val actionState = MutableStateFlow(SyncActionState())

    private val appearanceState = combine(
        getThemeModeUseCase(),
        getThemeAccentUseCase(),
        getLockEnabledUseCase(),
        getPinHashUseCase(),
        getUnlockGraceEnabledUseCase()
    ) { themeMode, themeAccent, lockEnabled, pinHash, graceEnabled ->
        Quint(themeMode, themeAccent, lockEnabled, pinHash.isNotBlank(), graceEnabled)
    }

    private val driveState = combine(
        getDriveAccessTokenUseCase(),
        getDriveFolderIdUseCase(),
        getDriveGroupNameUseCase(),
        getSyncAccountEmailUseCase(),
        getCloudSyncPrefsUseCase()
    ) { token, folderId, groupName, email, cloudSyncPrefs ->
        DriveSyncState(
            accessToken = token,
            folderId = folderId,
            groupName = groupName,
            accountEmail = email,
            cloudSyncEnabled = cloudSyncPrefs.enabled,
            cloudLastSyncStatus = cloudSyncPrefs.lastSyncStatus,
            cloudLastSyncAt = cloudSyncPrefs.lastSyncAt
        )
    }

    private val baseState = combine(
        getSettingsUseCase(),
        appearanceState,
        driveState
    ) { settings, appearance, drive ->
        NotificationBaseState(
            settings = settings,
            themeMode = appearance.first,
            themeAccent = appearance.second,
            lockEnabled = appearance.third,
            hasPin = appearance.fourth,
            unlockGraceEnabled = appearance.fifth,
            driveAccessToken = drive.accessToken,
            driveFolderId = drive.folderId,
            driveGroupName = drive.groupName,
            syncAccountEmail = drive.accountEmail,
            cloudSyncEnabled = drive.cloudSyncEnabled,
            cloudLastSyncStatus = drive.cloudLastSyncStatus,
            cloudLastSyncAt = drive.cloudLastSyncAt
        )
    }

    val uiState: StateFlow<NotificationUiState> = combine(baseState, actionState) { base, action ->
        NotificationUiState(
            settings = base.settings,
            themeMode = base.themeMode,
            themeAccent = base.themeAccent,
            lockEnabled = base.lockEnabled,
            driveAccessToken = base.driveAccessToken,
            driveFolderId = base.driveFolderId,
            driveGroupName = base.driveGroupName,
            syncAccountEmail = base.syncAccountEmail,
            hasPin = base.hasPin,
            unlockGraceEnabled = base.unlockGraceEnabled,
            cloudSyncEnabled = base.cloudSyncEnabled,
            cloudLastSyncStatus = base.cloudLastSyncStatus,
            cloudLastSyncAt = base.cloudLastSyncAt,
            driveMembers = action.driveMembers,
            inviteStatuses = action.inviteStatuses,
            memberAuditLogs = action.memberAuditLogs,
            isDriveMembersLoading = action.isDriveMembersLoading,
            syncConflictPreview = action.syncConflictPreview,
            syncStatusMessage = action.syncStatusMessage,
            isSyncing = action.isSyncing,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NotificationUiState())

    fun save(
        settings: NotificationSettingEntity,
        themeMode: String,
        themeAccent: String,
        lockEnabled: Boolean,
        unlockGraceEnabled: Boolean,
        driveAccessToken: String,
        driveFolderId: String,
        driveGroupName: String,
        syncAccountEmail: String,
        onSaved: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            upsertNotificationSettingUseCase(settings)
            setThemeModeUseCase(themeMode)
            setThemeAccentUseCase(themeAccent)
            setLockEnabledUseCase(lockEnabled)
            setUnlockGraceEnabledUseCase(unlockGraceEnabled)
            setDriveAccessTokenUseCase(driveAccessToken)
            setDriveFolderIdUseCase(driveFolderId)
            setDriveGroupNameUseCase(driveGroupName)
            setSyncAccountEmailUseCase(syncAccountEmail)
            setCloudSyncEnabledUseCase(syncAccountEmail.isNotBlank())
            reminderScheduler.scheduleDailyChecks()
            reminderScheduler.scheduleMonthlyReminder(settings.monthlyReminderDay)
            setStatus("設定を保存しました")
            onSaved?.invoke()
        }
    }

    fun onGoogleAccountAuthorized(email: String, accessToken: String) {
        viewModelScope.launch {
            if (email.isBlank()) {
                setStatus("Googleアカウント情報の取得に失敗しました。再接続してください。")
                return@launch
            }
            setDriveAccessTokenUseCase(accessToken)
            setSyncAccountEmailUseCase(email)
            setCloudSyncEnabledUseCase(true)
            setStatus("Googleアカウントを接続しました: $email")
        }
    }

    fun disconnectGoogleAccount() {
        viewModelScope.launch {
            setDriveAccessTokenUseCase("")
            setSyncAccountEmailUseCase("")
            setCloudSyncEnabledUseCase(false)
            actionState.update {
                it.copy(
                    driveMembers = emptyList(),
                    inviteStatuses = emptyList(),
                    memberAuditLogs = emptyList(),
                    isDriveMembersLoading = false,
                    syncConflictPreview = null,
                    pendingRemoteJson = null
                )
            }
            setCloudLastSyncUseCase("Google接続解除")
            setStatus("Googleアカウントの接続を解除しました")
        }
    }

    fun showSyncMessage(message: String) {
        setStatus(message)
    }

    fun createDriveGroup(accessToken: String, groupName: String) {
        viewModelScope.launch {
            setSyncing(true, "Google Drive に共有グループを作成中...")
            setDriveAccessTokenUseCase(accessToken)
            runDriveWithRetry(operationLabel = "グループ作成") {
                createDriveFolderUseCase(accessToken, groupName)
            }.fold(
                onSuccess = { folderId ->
                    setDriveFolderIdUseCase(folderId)
                    setDriveGroupNameUseCase(groupName)
                    setCloudLastSyncUseCase("グループ作成成功")
                    setSyncing(false, "グループ作成完了: folderId=$folderId")
                    refreshDriveMembers(accessToken, folderId, notifyOnSuccess = false)
                },
                onFailure = {
                    setCloudLastSyncUseCase("グループ作成失敗")
                    setSyncing(false, "グループ作成失敗: ${it.message}")
                }
            )
        }
    }

    fun inviteMember(accessToken: String, folderId: String, email: String) {
        inviteMembers(
            accessToken = accessToken,
            folderId = folderId,
            inviteEmailsRaw = email
        )
    }

    fun inviteMembers(
        accessToken: String,
        folderId: String,
        inviteEmailsRaw: String,
        onAllSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            if (folderId.isBlank()) {
                setStatus("共有フォルダIDが未入力です")
                return@launch
            }
            val ownerEmail = uiState.value.syncAccountEmail.trim().lowercase()
            val parsed = parseInviteTargets(inviteEmailsRaw)
            val invalidEmails = parsed.filterNot { EMAIL_REGEX.matches(it) }
            val validTargets = parsed
                .filter { EMAIL_REGEX.matches(it) }
                .filterNot { ownerEmail.isNotBlank() && it == ownerEmail }
                .distinct()
            if (validTargets.isEmpty()) {
                val message = when {
                    parsed.isEmpty() -> "招待先メールが未入力です"
                    invalidEmails.isNotEmpty() -> "メール形式が不正です: ${invalidEmails.joinToString()}"
                    else -> "自分自身のメールは招待対象にできません"
                }
                setStatus(message)
                return@launch
            }

            setInviteStatusesPending(validTargets)
            setSyncing(true, "共有招待メールを送信中... (${validTargets.size}件)")
            setDriveAccessTokenUseCase(accessToken)
            val failures = mutableListOf<String>()
            var successCount = 0
            validTargets.forEach { email ->
                runDriveWithRetry(operationLabel = "招待送信") {
                    inviteDriveMemberUseCase(accessToken, folderId, email)
                }.fold(
                    onSuccess = {
                        successCount += 1
                        setInviteStatus(email = email, status = "承認待ち", detail = "招待送信済み")
                    },
                    onFailure = {
                        failures += "$email (${it.message ?: "不明エラー"})"
                        setInviteStatus(
                            email = email,
                            status = "送信失敗",
                            detail = it.message ?: "不明エラー"
                        )
                    }
                )
            }

            val skippedCount = parsed.size - validTargets.size
            val summary = buildString {
                append("招待送信結果: 成功${successCount}件")
                if (failures.isNotEmpty()) append(" / 失敗${failures.size}件")
                if (skippedCount > 0) append(" / 除外${skippedCount}件")
                if (failures.isNotEmpty()) {
                    append("\n失敗: ${failures.joinToString(limit = 3)}")
                    if (failures.size > 3) append(" ...")
                }
            }
            setSyncing(false, summary)
            setCloudLastSyncUseCase(
                if (successCount > 0 && failures.isEmpty()) "招待送信成功" else "招待送信（部分失敗あり）"
            )
            if (successCount > 0) {
                refreshDriveMembers(accessToken, folderId, notifyOnSuccess = false)
                if (failures.isEmpty()) {
                    onAllSuccess?.invoke()
                }
            }
        }
    }

    fun refreshDriveMembers(accessToken: String, folderId: String, notifyOnSuccess: Boolean = true) {
        viewModelScope.launch {
            if (folderId.isBlank()) {
                setStatus("共有フォルダIDが未入力です")
                return@launch
            }
            setDriveAccessTokenUseCase(accessToken)
            actionState.update { it.copy(isDriveMembersLoading = true) }
            runDriveWithRetry(operationLabel = "メンバー一覧取得") {
                listDriveMembersUseCase(accessToken, folderId)
            }.fold(
                onSuccess = { members ->
                    setCloudLastSyncUseCase("メンバー一覧取得成功")
                    actionState.update {
                        val nextInviteStatuses = reconcileInviteStatuses(
                            existing = it.inviteStatuses,
                            members = members
                        )
                        it.copy(
                            driveMembers = members,
                            inviteStatuses = nextInviteStatuses,
                            isDriveMembersLoading = false,
                            syncStatusMessage = if (notifyOnSuccess) "共有メンバーを取得しました (${members.size}件)" else it.syncStatusMessage
                        )
                    }
                },
                onFailure = { error ->
                    setCloudLastSyncUseCase("メンバー一覧取得失敗")
                    actionState.update {
                        it.copy(
                            isDriveMembersLoading = false,
                            syncStatusMessage = "メンバー取得失敗: ${error.message}"
                        )
                    }
                }
            )
        }
    }

    fun toggleDriveMemberRole(accessToken: String, folderId: String, member: DriveMember) {
        viewModelScope.launch {
            if (folderId.isBlank()) {
                setStatus("共有フォルダIDが未入力です")
                return@launch
            }
            if (member.isOwner) {
                setStatus("オーナー権限は変更できません")
                return@launch
            }
            val currentRole = member.role.lowercase()
            if (currentRole !in setOf("reader", "writer")) {
                setStatus("この権限タイプは変更できません: ${member.role}")
                return@launch
            }
            val targetRole = if (currentRole == "writer") "reader" else "writer"
            val targetLabel = if (targetRole == "writer") "編集者" else "閲覧者"
            setSyncing(true, "権限を更新中... (${member.emailAddress.ifBlank { member.displayName }})")
            setDriveAccessTokenUseCase(accessToken)
            runDriveWithRetry(operationLabel = "権限変更") {
                updateDriveMemberRoleUseCase(
                    accessToken = accessToken,
                    folderId = folderId,
                    permissionId = member.permissionId,
                    role = targetRole
                )
            }.fold(
                onSuccess = {
                    setCloudLastSyncUseCase("メンバー権限更新成功")
                    setSyncing(false, "権限を${targetLabel}に変更しました")
                    appendMemberAuditLog("権限変更: ${member.emailAddress.ifBlank { member.displayName }} -> $targetLabel")
                    refreshDriveMembers(accessToken, folderId, notifyOnSuccess = false)
                },
                onFailure = {
                    setCloudLastSyncUseCase("メンバー権限更新失敗")
                    setSyncing(false, "権限変更失敗: ${it.message}")
                    appendMemberAuditLog("権限変更失敗: ${member.emailAddress.ifBlank { member.displayName }}")
                }
            )
        }
    }

    fun removeDriveMember(accessToken: String, folderId: String, member: DriveMember) {
        viewModelScope.launch {
            if (folderId.isBlank()) {
                setStatus("共有フォルダIDが未入力です")
                return@launch
            }
            if (member.isOwner) {
                setStatus("オーナーは共有解除できません")
                return@launch
            }
            setSyncing(true, "共有解除中... (${member.emailAddress.ifBlank { member.displayName }})")
            setDriveAccessTokenUseCase(accessToken)
            runDriveWithRetry(operationLabel = "共有解除") {
                removeDriveMemberUseCase(
                    accessToken = accessToken,
                    folderId = folderId,
                    permissionId = member.permissionId
                )
            }.fold(
                onSuccess = {
                    setCloudLastSyncUseCase("メンバー共有解除成功")
                    setSyncing(false, "共有解除しました: ${member.emailAddress.ifBlank { member.displayName }}")
                    appendMemberAuditLog("共有解除: ${member.emailAddress.ifBlank { member.displayName }}")
                    refreshDriveMembers(accessToken, folderId, notifyOnSuccess = false)
                },
                onFailure = {
                    setCloudLastSyncUseCase("メンバー共有解除失敗")
                    setSyncing(false, "共有解除失敗: ${it.message}")
                    appendMemberAuditLog("共有解除失敗: ${member.emailAddress.ifBlank { member.displayName }}")
                }
            )
        }
    }

    fun uploadSharedSnapshot(accessToken: String, folderId: String, groupName: String) {
        viewModelScope.launch {
            setSyncing(true, "共有データをアップロード中...")
            setDriveAccessTokenUseCase(accessToken)
            val json = exportBackupJsonUseCase.buildJson()
            runDriveWithRetry(operationLabel = "アップロード") {
                upsertSharedBackupToDriveUseCase(
                    UpsertSharedBackupToDriveUseCase.Request(
                        accessToken = accessToken,
                        folderId = folderId,
                        fileName = buildSharedFileName(groupName),
                        jsonBody = json
                    )
                )
            }.fold(
                onSuccess = {
                    setCloudLastSyncUseCase("アップロード成功")
                    setSyncing(false, "最新データをGoogle Driveに同期しました")
                },
                onFailure = {
                    setCloudLastSyncUseCase("アップロード失敗")
                    setSyncing(false, "アップロード失敗: ${it.message}")
                }
            )
        }
    }

    fun downloadSharedSnapshot(accessToken: String, folderId: String, groupName: String) {
        viewModelScope.launch {
            setSyncing(true, "共有データをダウンロード中...")
            setDriveAccessTokenUseCase(accessToken)
            runDriveWithRetry(operationLabel = "ダウンロード") {
                downloadSharedBackupFromDriveUseCase(
                    accessToken = accessToken,
                    folderId = folderId,
                    fileName = buildSharedFileName(groupName)
                )
            }.fold(
                onSuccess = { json ->
                    val localJson = exportBackupJsonUseCase.buildJson()
                    val preview = SyncConflictPreview(
                        local = summarizeBackupJson(localJson),
                        remote = summarizeBackupJson(json)
                    )
                    actionState.update {
                        it.copy(
                            syncConflictPreview = preview,
                            pendingRemoteJson = json
                        )
                    }
                    setCloudLastSyncUseCase("ダウンロード成功（反映待ち）")
                    setSyncing(false, "差分を確認して「リモートを反映」を実行してください")
                },
                onFailure = {
                    setCloudLastSyncUseCase("ダウンロード失敗")
                    setSyncing(false, "ダウンロード失敗: ${it.message}")
                }
            )
        }
    }

    fun applyRemoteSnapshotAfterReview() {
        viewModelScope.launch {
            val payload = actionState.value.pendingRemoteJson
            if (payload.isNullOrBlank()) {
                setStatus("反映対象データがありません。再度ダウンロードしてください。")
                return@launch
            }
            setSyncing(true, "共有データを反映中...")
            runCatching { importBackupJsonUseCase(payload) }.fold(
                onSuccess = {
                    setCloudLastSyncUseCase("ダウンロード/反映成功")
                    actionState.update {
                        it.copy(syncConflictPreview = null, pendingRemoteJson = null)
                    }
                    setSyncing(false, "共有データを反映しました")
                },
                onFailure = {
                    setCloudLastSyncUseCase("ダウンロード後の反映失敗")
                    setSyncing(false, "データ反映失敗: ${it.message}")
                }
            )
        }
    }

    fun dismissSyncConflictPreview() {
        actionState.update { it.copy(syncConflictPreview = null, pendingRemoteJson = null) }
    }

    fun clearStatus() {
        actionState.update { it.copy(syncStatusMessage = null) }
    }

    fun clearMemberAuditLogs() {
        actionState.update { it.copy(memberAuditLogs = emptyList()) }
        setStatus("権限変更ログをクリアしました")
    }

    fun savePin(pin: String) {
        viewModelScope.launch {
            if (pin.length !in 4..6 || pin.any { !it.isDigit() }) {
                setStatus("PINは4〜6桁の数字で設定してください")
                return@launch
            }
            val hash = hashPin(pin)
            setPinHashUseCase(hash)
            setStatus("PINを更新しました")
        }
    }

    fun clearPin() {
        viewModelScope.launch {
            clearPinHashUseCase()
            setStatus("PINを削除しました")
        }
    }

    private fun buildSharedFileName(groupName: String): String {
        val sanitized = groupName.ifBlank { "payment_group" }
            .replace(Regex("[^A-Za-z0-9_-]"), "_")
        return "${sanitized}_latest.json"
    }

    private fun setSyncing(value: Boolean, message: String?) {
        actionState.update { it.copy(isSyncing = value, syncStatusMessage = message) }
    }

    private fun setStatus(message: String) {
        actionState.update { it.copy(syncStatusMessage = message, isSyncing = false) }
    }

    private fun setInviteStatusesPending(emails: List<String>) {
        val now = System.currentTimeMillis()
        actionState.update { state ->
            val existingByEmail = state.inviteStatuses.associateBy { it.email.lowercase() }.toMutableMap()
            emails.forEach { email ->
                existingByEmail[email.lowercase()] = InviteStatusItem(
                    email = email,
                    status = "承認待ち",
                    detail = "招待送信中",
                    updatedAt = now
                )
            }
            state.copy(
                inviteStatuses = existingByEmail.values.sortedByDescending { it.updatedAt }
            )
        }
    }

    private fun setInviteStatus(email: String, status: String, detail: String) {
        val now = System.currentTimeMillis()
        actionState.update { state ->
            val next = state.inviteStatuses
                .filterNot { it.email.equals(email, ignoreCase = true) }
                .plus(
                    InviteStatusItem(
                        email = email,
                        status = status,
                        detail = detail,
                        updatedAt = now
                    )
                )
                .sortedByDescending { it.updatedAt }
                .take(40)
            state.copy(inviteStatuses = next)
        }
    }

    private fun reconcileInviteStatuses(
        existing: List<InviteStatusItem>,
        members: List<DriveMember>
    ): List<InviteStatusItem> {
        val now = System.currentTimeMillis()
        val activeEmails = members.map { it.emailAddress.trim().lowercase() }
            .filter { it.isNotBlank() }
            .toSet()
        if (activeEmails.isEmpty() && existing.isEmpty()) return existing

        val existingByEmail = existing.associateBy { it.email.lowercase() }.toMutableMap()
        activeEmails.forEach { email ->
            existingByEmail[email] = InviteStatusItem(
                email = email,
                status = "有効",
                detail = "共有メンバーとして有効",
                updatedAt = now
            )
        }
        return existingByEmail.values
            .sortedByDescending { it.updatedAt }
            .take(40)
    }

    private fun summarizeBackupJson(json: String): BackupSnapshotSummary {
        val root = runCatching { JSONObject(json) }.getOrDefault(JSONObject())
        val payments = root.optJSONArray("payments")
        val totalAmount = buildList<Long> {
            if (payments != null) {
                for (i in 0 until payments.length()) {
                    val payment = payments.optJSONObject(i) ?: continue
                    add(payment.optLong("amount", 0L))
                }
            }
        }.sum()
        return BackupSnapshotSummary(
            exportedAt = root.optLong("exportedAt", 0L),
            cards = root.optJSONArray("cards")?.length() ?: 0,
            payments = payments?.length() ?: 0,
            accounts = root.optJSONArray("accounts")?.length() ?: 0,
            budgets = root.optJSONArray("budgets")?.length() ?: 0,
            subscriptions = root.optJSONArray("subscriptions")?.length() ?: 0,
            installments = root.optJSONArray("installments")?.length() ?: 0,
            totalPaymentAmount = totalAmount
        )
    }

    private suspend fun <T> runDriveWithRetry(
        operationLabel: String,
        maxAttempts: Int = 3,
        initialDelayMs: Long = 1_000L,
        operation: suspend () -> Result<T>
    ): Result<T> {
        var attempt = 1
        var backoffMs = initialDelayMs
        var lastError: Throwable? = null

        while (attempt <= maxAttempts) {
            val result = operation()
            if (result.isSuccess) return result

            val error = result.exceptionOrNull()
            lastError = error
            val hasNextAttempt = attempt < maxAttempts
            if (!hasNextAttempt || !shouldRetryDriveError(error)) {
                return result
            }

            attempt += 1
            setSyncing(true, "$operationLabel に失敗したため再試行します (${attempt}/${maxAttempts})")
            delay(backoffMs)
            backoffMs = (backoffMs * 2).coerceAtMost(8_000L)
        }

        return Result.failure(lastError ?: IllegalStateException("$operationLabel が失敗しました"))
    }

    private fun shouldRetryDriveError(error: Throwable?): Boolean {
        val message = error?.message.orEmpty().lowercase()
        if (message.isBlank()) return true
        val transientHttp = listOf("429", "500", "502", "503", "504").any {
            message.contains("($it)") || Regex("\\b$it\\b").containsMatchIn(message)
        }
        val transientNetwork = listOf(
            "timeout",
            "timed out",
            "connection reset",
            "network",
            "temporarily unavailable"
        ).any { message.contains(it) }
        return transientHttp || transientNetwork
    }

    private fun appendMemberAuditLog(message: String) {
        val timestamp = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(Date())
        val entry = "[$timestamp] $message"
        actionState.update { state ->
            state.copy(memberAuditLogs = (listOf(entry) + state.memberAuditLogs).take(30))
        }
    }

    private fun parseInviteTargets(raw: String): List<String> {
        return raw
            .split(Regex("[,;\\s]+"))
            .map { it.trim().lowercase() }
            .filter { it.isNotBlank() }
    }

    private companion object {
        val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    }
}

private data class Quint<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
)

private fun hashPin(pin: String): String {
    val md = java.security.MessageDigest.getInstance("SHA-256")
    return md.digest(pin.toByteArray()).joinToString("") { "%02x".format(it) }
}
