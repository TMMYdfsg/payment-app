package com.payment.app.ui.notification

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.payment.app.data.db.entity.NotificationSettingEntity
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.payment.app.domain.usecase.DriveMember
import com.payment.app.util.exportAuditLogsToCsv
import com.payment.app.util.exportAuditLogsToJson
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAccountManage: () -> Unit = {},
    onNavigateToCardManage: () -> Unit = {},
    onNavigateToSubscription: () -> Unit = {},
    viewModel: NotificationSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity
    val authorizationClient = remember(context) { Identity.getAuthorizationClient(context) }

    var enabled by remember { mutableStateOf(false) }
    var leadDays by remember { mutableStateOf("3") }
    var budgetThreshold by remember { mutableStateOf("80") }
    var monthlyDay by remember { mutableStateOf("1") }
    var themeMode by remember { mutableStateOf("system") }
    var themeAccent by remember { mutableStateOf("ocean") }
    var lockEnabled by remember { mutableStateOf(false) }
    var driveFolderId by remember { mutableStateOf("") }
    var driveGroupName by remember { mutableStateOf("Payment App Shared Group") }
    var inviteEmail by remember { mutableStateOf("") }
    var unlockGraceEnabled by remember { mutableStateOf(true) }
    var pinInput by remember { mutableStateOf("") }
    var pendingAuthorizedAction by remember { mutableStateOf<((String, String) -> Unit)?>(null) }
    var memberSearchQuery by remember { mutableStateOf("") }
    var memberRoleFilter by remember { mutableStateOf("all") }
    var pendingRemoveMember by remember { mutableStateOf<DriveMember?>(null) }
    val ownerEmail = uiState.syncAccountEmail.trim().lowercase()
    val inviteTargets = parseInviteTargets(inviteEmail)
    val validInviteTargets = inviteTargets.filter { SIMPLE_EMAIL_REGEX.matches(it) }
    val invalidInviteTargets = inviteTargets.filterNot { SIMPLE_EMAIL_REGEX.matches(it) }
    val inviteTargetCount = validInviteTargets
        .filterNot { ownerEmail.isNotBlank() && it == ownerEmail }
        .distinct()
        .size
    val filteredDriveMembers = uiState.driveMembers.filter { member ->
        val query = memberSearchQuery.trim()
        val matchesQuery = query.isBlank() ||
            member.emailAddress.contains(query, ignoreCase = true) ||
            member.displayName.contains(query, ignoreCase = true)
        val matchesRole = when (memberRoleFilter) {
            "owner", "writer", "reader", "commenter" -> member.role.equals(memberRoleFilter, ignoreCase = true)
            else -> true
        }
        matchesQuery && matchesRole
    }

    val authorizationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        val callback = pendingAuthorizedAction
        pendingAuthorizedAction = null
        if (callback == null) return@rememberLauncherForActivityResult
        if (result.resultCode != Activity.RESULT_OK) {
            viewModel.showSyncMessage("Google認証をキャンセルしました")
            return@rememberLauncherForActivityResult
        }
        runCatching { authorizationClient.getAuthorizationResultFromIntent(result.data) }
            .onSuccess { authResult ->
                handleAuthorizationResult(
                    authorizationResult = authResult,
                    fallbackEmail = uiState.syncAccountEmail,
                    onAuthorized = callback,
                    onError = viewModel::showSyncMessage
                )
            }
            .onFailure {
                val message = (it as? ApiException)?.localizedMessage ?: it.message.orEmpty()
                viewModel.showSyncMessage("Google認証に失敗しました: $message")
            }
    }

    LaunchedEffect(
        uiState.settings,
        uiState.themeMode,
        uiState.themeAccent,
        uiState.lockEnabled,
        uiState.driveFolderId,
        uiState.driveGroupName,
        uiState.unlockGraceEnabled
    ) {
        val settings = uiState.settings
        enabled = settings?.enabled ?: false
        leadDays = (settings?.reminderLeadDays ?: 3).toString()
        budgetThreshold = (settings?.budgetAlertThreshold ?: 80).toString()
        monthlyDay = (settings?.monthlyReminderDay ?: 1).toString()
        themeMode = uiState.themeMode
        themeAccent = uiState.themeAccent
        lockEnabled = uiState.lockEnabled
        driveFolderId = uiState.driveFolderId
        driveGroupName = uiState.driveGroupName
        unlockGraceEnabled = uiState.unlockGraceEnabled
    }

    LaunchedEffect(uiState.driveAccessToken, uiState.driveFolderId) {
        if (
            uiState.driveAccessToken.isNotBlank() &&
            uiState.driveFolderId.isNotBlank() &&
            uiState.driveMembers.isEmpty() &&
            !uiState.isDriveMembersLoading
        ) {
            viewModel.refreshDriveMembers(
                accessToken = uiState.driveAccessToken,
                folderId = uiState.driveFolderId,
                notifyOnSuccess = false
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("設定") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        if (uiState.isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        "通知 / 表示 / セキュリティ",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "家計の通知設定",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingToggleRow(
                            icon = Icons.Default.Notifications,
                            title = "通知を有効化",
                            subtitle = "引落前通知・予算通知・催促通知を有効にします",
                            checked = enabled,
                            onCheckedChange = { enabled = it }
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("1", "3", "5", "7").forEach { days ->
                                AssistChip(
                                    onClick = { leadDays = days },
                                    label = { Text("${days}日前") }
                                )
                            }
                        }
                        OutlinedTextField(
                            value = leadDays,
                            onValueChange = { if (it.all(Char::isDigit)) leadDays = it },
                            label = { Text("引落N日前リマインダー") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("80", "100").forEach { threshold ->
                                FilterChip(
                                    selected = budgetThreshold == threshold,
                                    onClick = { budgetThreshold = threshold },
                                    label = { Text("${threshold}%") }
                                )
                            }
                        }
                        OutlinedTextField(
                            value = budgetThreshold,
                            onValueChange = { if (it.all(Char::isDigit)) budgetThreshold = it },
                            label = { Text("予算アラートしきい値(%)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = monthlyDay,
                            onValueChange = { if (it.all(Char::isDigit)) monthlyDay = it },
                            label = { Text("月次催促日(1-28)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudSync, contentDescription = null)
                            Text(
                                "Google同期 / グループ共有",
                                modifier = Modifier.padding(start = 8.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = if (uiState.syncAccountEmail.isBlank()) {
                                "Googleアカウント未接続"
                            } else {
                                "接続済み: ${uiState.syncAccountEmail}"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (uiState.cloudLastSyncStatus.isNotBlank() || uiState.cloudLastSyncAt > 0L) {
                            Text(
                                "最終同期: ${formatSyncTime(uiState.cloudLastSyncAt)} / ${uiState.cloudLastSyncStatus.ifBlank { "状態なし" }}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    beginGoogleAuthorization(
                                        activity = activity,
                                        authorizationClient = authorizationClient,
                                        authorizationLauncher = authorizationLauncher,
                                        onRequireResolution = { pendingAuthorizedAction = it },
                                        onAuthorized = { token, email ->
                                            viewModel.onGoogleAccountAuthorized(email, token)
                                        },
                                        fallbackEmail = uiState.syncAccountEmail,
                                        onError = viewModel::showSyncMessage
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !uiState.isSyncing
                            ) {
                                Text(if (uiState.syncAccountEmail.isBlank()) "Google接続" else "再接続")
                            }
                            Button(
                                onClick = { viewModel.disconnectGoogleAccount() },
                                modifier = Modifier.weight(1f),
                                enabled = !uiState.isSyncing && uiState.syncAccountEmail.isNotBlank()
                            ) {
                                Text("接続解除")
                            }
                        }
                        OutlinedTextField(
                            value = driveGroupName,
                            onValueChange = { driveGroupName = it },
                            label = { Text("グループ名") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = driveFolderId,
                            onValueChange = { driveFolderId = it },
                            label = { Text("共有フォルダID") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    beginGoogleAuthorization(
                                        activity = activity,
                                        authorizationClient = authorizationClient,
                                        authorizationLauncher = authorizationLauncher,
                                        onRequireResolution = { pendingAuthorizedAction = it },
                                        onAuthorized = { token, email ->
                                            viewModel.onGoogleAccountAuthorized(
                                                email = email.ifBlank { uiState.syncAccountEmail },
                                                accessToken = token
                                            )
                                            viewModel.createDriveGroup(token, driveGroupName)
                                        },
                                        fallbackEmail = uiState.syncAccountEmail,
                                        onError = viewModel::showSyncMessage
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !uiState.isSyncing && driveGroupName.isNotBlank()
                            ) {
                                Text("グループ作成")
                            }
                            Button(
                                onClick = {
                                    beginGoogleAuthorization(
                                        activity = activity,
                                        authorizationClient = authorizationClient,
                                        authorizationLauncher = authorizationLauncher,
                                        onRequireResolution = { pendingAuthorizedAction = it },
                                        onAuthorized = { token, email ->
                                            viewModel.onGoogleAccountAuthorized(
                                                email = email.ifBlank { uiState.syncAccountEmail },
                                                accessToken = token
                                            )
                                            viewModel.uploadSharedSnapshot(
                                                accessToken = token,
                                                folderId = driveFolderId,
                                                groupName = driveGroupName
                                            )
                                        },
                                        fallbackEmail = uiState.syncAccountEmail,
                                        onError = viewModel::showSyncMessage
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                enabled = !uiState.isSyncing && driveFolderId.isNotBlank()
                            ) {
                                Text("アップロード")
                            }
                        }
                        Button(
                            onClick = {
                                beginGoogleAuthorization(
                                    activity = activity,
                                    authorizationClient = authorizationClient,
                                    authorizationLauncher = authorizationLauncher,
                                    onRequireResolution = { pendingAuthorizedAction = it },
                                    onAuthorized = { token, email ->
                                        viewModel.onGoogleAccountAuthorized(
                                            email = email.ifBlank { uiState.syncAccountEmail },
                                            accessToken = token
                                        )
                                        viewModel.downloadSharedSnapshot(
                                            accessToken = token,
                                            folderId = driveFolderId,
                                            groupName = driveGroupName
                                        )
                                    },
                                    fallbackEmail = uiState.syncAccountEmail,
                                    onError = viewModel::showSyncMessage
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isSyncing && driveFolderId.isNotBlank()
                        ) {
                            Text("差分確認して同期")
                        }
                        OutlinedTextField(
                            value = inviteEmail,
                            onValueChange = { inviteEmail = it },
                            label = { Text("招待メール（複数可）") },
                            supportingText = {
                                Text(
                                    buildString {
                                        append("カンマ/空白/改行で複数指定できます")
                                        if (inviteTargets.isNotEmpty()) {
                                            append("  有効:${inviteTargetCount}件")
                                        }
                                        if (invalidInviteTargets.isNotEmpty()) {
                                            append("  無効:${invalidInviteTargets.size}件")
                                        }
                                    }
                                )
                            },
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = {
                                beginGoogleAuthorization(
                                    activity = activity,
                                    authorizationClient = authorizationClient,
                                    authorizationLauncher = authorizationLauncher,
                                    onRequireResolution = { pendingAuthorizedAction = it },
                                    onAuthorized = { token, email ->
                                        viewModel.onGoogleAccountAuthorized(
                                            email = email.ifBlank { uiState.syncAccountEmail },
                                            accessToken = token
                                        )
                                        viewModel.inviteMembers(
                                            accessToken = token,
                                            folderId = driveFolderId,
                                            inviteEmailsRaw = inviteEmail,
                                            onAllSuccess = { inviteEmail = "" }
                                        )
                                    },
                                    fallbackEmail = uiState.syncAccountEmail,
                                    onError = viewModel::showSyncMessage
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isSyncing && driveFolderId.isNotBlank() && inviteTargetCount > 0
                        ) {
                            Icon(Icons.Default.GroupAdd, contentDescription = null)
                            Text(
                                if (inviteTargetCount <= 1) "共有招待を送る" else "共有招待を送る (${inviteTargetCount}件)",
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                        if (uiState.inviteStatuses.isNotEmpty()) {
                            Text(
                                "招待状態一覧",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                uiState.inviteStatuses.take(12).forEach { invite ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(invite.email, fontWeight = FontWeight.Bold)
                                                if (invite.detail.isNotBlank()) {
                                                    Text(
                                                        invite.detail,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                            Text(
                                                invite.status,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = when (invite.status) {
                                                    "有効" -> MaterialTheme.colorScheme.primary
                                                    "送信失敗" -> MaterialTheme.colorScheme.error
                                                    else -> MaterialTheme.colorScheme.tertiary
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Button(
                            onClick = {
                                beginGoogleAuthorization(
                                    activity = activity,
                                    authorizationClient = authorizationClient,
                                    authorizationLauncher = authorizationLauncher,
                                    onRequireResolution = { pendingAuthorizedAction = it },
                                    onAuthorized = { token, email ->
                                        viewModel.onGoogleAccountAuthorized(
                                            email = email.ifBlank { uiState.syncAccountEmail },
                                            accessToken = token
                                        )
                                        viewModel.refreshDriveMembers(
                                            accessToken = token,
                                            folderId = driveFolderId
                                        )
                                    },
                                    fallbackEmail = uiState.syncAccountEmail,
                                    onError = viewModel::showSyncMessage
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isSyncing && driveFolderId.isNotBlank()
                        ) {
                            Text("メンバー一覧を更新")
                        }
                        if (uiState.isDriveMembersLoading) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator()
                                Text(
                                    "共有メンバーを取得中...",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        } else if (uiState.driveMembers.isNotEmpty()) {
                            OutlinedTextField(
                                value = memberSearchQuery,
                                onValueChange = { memberSearchQuery = it },
                                label = { Text("メンバー検索（メール/名前）") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                listOf(
                                    "all" to "全員",
                                    "owner" to "オーナー",
                                    "writer" to "編集者",
                                    "reader" to "閲覧者"
                                ).forEach { (value, label) ->
                                    FilterChip(
                                        selected = memberRoleFilter == value,
                                        onClick = { memberRoleFilter = value },
                                        label = { Text(label) }
                                    )
                                }
                            }
                            Text(
                                "共有メンバー (${filteredDriveMembers.size}/${uiState.driveMembers.size}件)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                filteredDriveMembers.forEach { member ->
                                    DriveMemberRow(
                                        member = member,
                                        enabled = !uiState.isSyncing,
                                        onToggleRole = {
                                            beginGoogleAuthorization(
                                                activity = activity,
                                                authorizationClient = authorizationClient,
                                                authorizationLauncher = authorizationLauncher,
                                                onRequireResolution = { pendingAuthorizedAction = it },
                                                onAuthorized = { token, email ->
                                                    viewModel.onGoogleAccountAuthorized(
                                                        email = email.ifBlank { uiState.syncAccountEmail },
                                                        accessToken = token
                                                    )
                                                    viewModel.toggleDriveMemberRole(
                                                        accessToken = token,
                                                        folderId = driveFolderId,
                                                        member = member
                                                    )
                                                },
                                                fallbackEmail = uiState.syncAccountEmail,
                                                onError = viewModel::showSyncMessage
                                            )
                                        },
                                        onRemove = {
                                            pendingRemoveMember = member
                                        }
                                    )
                                }
                            }
                            if (filteredDriveMembers.isEmpty()) {
                                Text(
                                    "検索条件に一致するメンバーがいません",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else if (driveFolderId.isNotBlank()) {
                            Text(
                                "共有メンバー情報は未取得です。「メンバー一覧を更新」を押してください。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (uiState.memberAuditLogs.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "権限変更ログ",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                TextButton(
                                    onClick = viewModel::clearMemberAuditLogs,
                                    enabled = !uiState.isSyncing
                                ) {
                                    Text("ログをクリア")
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val file = exportAuditLogsToCsv(context, uiState.memberAuditLogs)
                                        if (file == null) {
                                            viewModel.showSyncMessage("出力対象ログがありません")
                                        } else {
                                            shareExportFile(
                                                context = context,
                                                file = file,
                                                mimeType = "text/csv",
                                                chooserTitle = "監査ログCSVを共有",
                                                subject = "member_audit_logs.csv"
                                            )
                                            viewModel.showSyncMessage("監査ログCSVの共有シートを開きました")
                                        }
                                    },
                                    enabled = !uiState.isSyncing,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("CSV出力")
                                }
                                Button(
                                    onClick = {
                                        val file = exportAuditLogsToJson(context, uiState.memberAuditLogs)
                                        if (file == null) {
                                            viewModel.showSyncMessage("出力対象ログがありません")
                                        } else {
                                            shareExportFile(
                                                context = context,
                                                file = file,
                                                mimeType = "application/json",
                                                chooserTitle = "監査ログJSONを共有",
                                                subject = "member_audit_logs.json"
                                            )
                                            viewModel.showSyncMessage("監査ログJSONの共有シートを開きました")
                                        }
                                    },
                                    enabled = !uiState.isSyncing,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("JSON出力")
                                }
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                uiState.memberAuditLogs.take(8).forEach { log ->
                                    Text(
                                        log,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                        uiState.syncStatusMessage?.let { message ->
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("表示テーマ", fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("system" to "自動", "light" to "ライト", "dark" to "ダーク").forEach { (value, label) ->
                                FilterChip(
                                    selected = themeMode == value,
                                    onClick = { themeMode = value },
                                    label = { Text(label) }
                                )
                            }
                        }
                        Text("アクセント")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                "ocean" to "Ocean",
                                "emerald" to "Emerald",
                                "sunset" to "Sunset",
                                "slate" to "Slate"
                            ).forEach { (value, label) ->
                                FilterChip(
                                    selected = themeAccent == value,
                                    onClick = { themeAccent = value },
                                    label = { Text(label) }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        SettingActionRow(
                            icon = Icons.Default.Person,
                            title = "引き落とし口座の管理",
                            subtitle = "追加・削除・既定口座の更新",
                            onClick = onNavigateToAccountManage
                        )
                        SettingActionRow(
                            icon = Icons.Default.CreditCard,
                            title = "カード管理",
                            subtitle = "カード追加/削除・還元率の確認",
                            onClick = onNavigateToCardManage
                        )
                        SettingActionRow(
                            icon = Icons.Default.Subscriptions,
                            title = "サブスク/分割払い",
                            subtitle = "月額合算と残回数トラッカー",
                            onClick = onNavigateToSubscription
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingToggleRow(
                            icon = Icons.Default.Lock,
                            title = "起動ロック（生体認証）",
                            subtitle = "起動時と復帰時に認証を要求",
                            checked = lockEnabled,
                            onCheckedChange = { lockEnabled = it }
                        )
                        SettingToggleRow(
                            icon = Icons.Default.Lock,
                            title = "再認証猶予5分を有効化",
                            subtitle = "直近の認証から5分間は再認証をスキップ",
                            checked = unlockGraceEnabled,
                            onCheckedChange = { unlockGraceEnabled = it }
                        )
                        OutlinedTextField(
                            value = pinInput,
                            onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) pinInput = it },
                            label = { Text("PIN (4-6桁)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { viewModel.savePin(pinInput) },
                                modifier = Modifier.weight(1f),
                                enabled = pinInput.length in 4..6 && pinInput.all(Char::isDigit)
                            ) {
                                Text(if (uiState.hasPin) "PINを更新" else "PINを設定")
                            }
                            Button(
                                onClick = { viewModel.clearPin() },
                                modifier = Modifier.weight(1f),
                                enabled = uiState.hasPin
                            ) {
                                Text("PINを削除")
                            }
                        }
                        AssistChip(
                            onClick = { /* reserve for future privacy screen */ },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Savings, contentDescription = null)
                                    Text(
                                        "データ保護は有効",
                                        modifier = Modifier.padding(start = 6.dp)
                                    )
                                }
                            }
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {
                        viewModel.save(
                            settings = NotificationSettingEntity(
                                id = uiState.settings?.id ?: 0,
                                enabled = enabled,
                                reminderLeadDays = leadDays.toIntOrNull()?.coerceAtLeast(0) ?: 3,
                                budgetAlertThreshold = budgetThreshold.toIntOrNull()?.coerceIn(10, 200) ?: 80,
                                monthlyReminderDay = monthlyDay.toIntOrNull()?.coerceIn(1, 28) ?: 1
                            ),
                            themeMode = themeMode,
                            themeAccent = themeAccent,
                            lockEnabled = lockEnabled,
                            unlockGraceEnabled = unlockGraceEnabled,
                            driveAccessToken = uiState.driveAccessToken,
                            driveFolderId = driveFolderId,
                            driveGroupName = driveGroupName,
                            syncAccountEmail = uiState.syncAccountEmail,
                            onSaved = onNavigateBack
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("設定を保存")
                }
            }

            item {
                Text(
                    "保存後に通知スケジュールを再構築します。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }
    }

    pendingRemoveMember?.let { member ->
        AlertDialog(
            onDismissRequest = { pendingRemoveMember = null },
            title = { Text("共有解除の確認") },
            text = {
                Text(
                    buildString {
                        append("次のメンバーを共有解除します。\n")
                        append(member.emailAddress.ifBlank { member.displayName.ifBlank { "不明なメンバー" } })
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val target = pendingRemoveMember
                        pendingRemoveMember = null
                        if (target != null) {
                            beginGoogleAuthorization(
                                activity = activity,
                                authorizationClient = authorizationClient,
                                authorizationLauncher = authorizationLauncher,
                                onRequireResolution = { pendingAuthorizedAction = it },
                                onAuthorized = { token, email ->
                                    viewModel.onGoogleAccountAuthorized(
                                        email = email.ifBlank { uiState.syncAccountEmail },
                                        accessToken = token
                                    )
                                    viewModel.removeDriveMember(
                                        accessToken = token,
                                        folderId = driveFolderId,
                                        member = target
                                    )
                                },
                                fallbackEmail = uiState.syncAccountEmail,
                                onError = viewModel::showSyncMessage
                            )
                        }
                    },
                    enabled = !uiState.isSyncing
                ) {
                    Text("共有解除")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { pendingRemoveMember = null }
                ) {
                    Text("キャンセル")
                }
            }
        )
    }

    uiState.syncConflictPreview?.let { preview ->
        AlertDialog(
            onDismissRequest = viewModel::dismissSyncConflictPreview,
            title = { Text("同期差分の確認") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("リモートを反映するとローカルデータを置き換えます。")
                    Text(
                        "ローカル: ${formatSyncTime(preview.local.exportedAt)} / 支払い${preview.local.payments}件 / 合計${preview.local.totalPaymentAmount}円",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "リモート: ${formatSyncTime(preview.remote.exportedAt)} / 支払い${preview.remote.payments}件 / 合計${preview.remote.totalPaymentAmount}円",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "カード ${preview.local.cards}→${preview.remote.cards} / 口座 ${preview.local.accounts}→${preview.remote.accounts}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = viewModel::applyRemoteSnapshotAfterReview,
                    enabled = !uiState.isSyncing
                ) {
                    Text("リモートを反映")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = viewModel::dismissSyncConflictPreview,
                    enabled = !uiState.isSyncing
                ) {
                    Text("キャンセル")
                }
            }
        )
    }
}

@Composable
private fun SettingToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    androidx.compose.material3.TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null)
                Column {
                    Text(title, fontWeight = FontWeight.Bold)
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Composable
private fun DriveMemberRow(
    member: DriveMember,
    enabled: Boolean,
    onToggleRole: () -> Unit,
    onRemove: () -> Unit
) {
    val canManage = enabled && !member.isOwner && member.permissionId.isNotBlank()
    val currentRole = member.role.lowercase()
    val toggleLabel = if (currentRole == "writer") "閲覧者に変更" else "編集者に変更"
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                member.emailAddress.ifBlank { "メール不明" },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            if (member.displayName.isNotBlank()) {
                Text(
                    member.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                "権限: ${driveRoleLabel(member.role)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onToggleRole,
                    enabled = canManage,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(toggleLabel)
                }
                Button(
                    onClick = onRemove,
                    enabled = canManage,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("共有解除")
                }
            }
            if (member.isOwner) {
                Text(
                    "オーナーはこの画面から変更できません",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private const val DRIVE_FILE_SCOPE = "https://www.googleapis.com/auth/drive.file"
private const val EMAIL_SCOPE = "email"
private const val PROFILE_SCOPE = "profile"
private const val OPENID_SCOPE = "openid"
private val SIMPLE_EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

private fun shareExportFile(
    context: android.content.Context,
    file: File,
    mimeType: String,
    chooserTitle: String,
    subject: String
) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, subject)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, chooserTitle))
}

private fun beginGoogleAuthorization(
    activity: Activity?,
    authorizationClient: com.google.android.gms.auth.api.identity.AuthorizationClient,
    authorizationLauncher: androidx.activity.result.ActivityResultLauncher<IntentSenderRequest>,
    onRequireResolution: (((String, String) -> Unit) -> Unit),
    onAuthorized: (String, String) -> Unit,
    fallbackEmail: String,
    onError: (String) -> Unit
) {
    if (activity == null) {
        onError("Google認証を開始できませんでした")
        return
    }

    val request = AuthorizationRequest.Builder()
        .setRequestedScopes(
            listOf(
                Scope(DRIVE_FILE_SCOPE),
                Scope(EMAIL_SCOPE),
                Scope(PROFILE_SCOPE),
                Scope(OPENID_SCOPE)
            )
        )
        .build()

    authorizationClient.authorize(request)
        .addOnSuccessListener { authorizationResult ->
            if (authorizationResult.hasResolution()) {
                val pendingIntent = authorizationResult.pendingIntent
                if (pendingIntent == null) {
                    onError("Google認証の続行に必要な情報を取得できませんでした")
                    return@addOnSuccessListener
                }
                onRequireResolution { token, email -> onAuthorized(token, email.ifBlank { fallbackEmail }) }
                authorizationLauncher.launch(IntentSenderRequest.Builder(pendingIntent.intentSender).build())
            } else {
                handleAuthorizationResult(
                    authorizationResult = authorizationResult,
                    fallbackEmail = fallbackEmail,
                    onAuthorized = onAuthorized,
                    onError = onError
                )
            }
        }
        .addOnFailureListener { error ->
            val message = (error as? ApiException)?.localizedMessage ?: error.message.orEmpty()
            onError("Google認証に失敗しました: $message")
        }
}

private fun handleAuthorizationResult(
    authorizationResult: AuthorizationResult,
    fallbackEmail: String,
    onAuthorized: (String, String) -> Unit,
    onError: (String) -> Unit
) {
    val accessToken = authorizationResult.accessToken.orEmpty()
    val email = authorizationResult.toGoogleSignInAccount()?.email.orEmpty().ifBlank { fallbackEmail }
    if (accessToken.isBlank()) {
        onError("アクセストークンを取得できませんでした")
        return
    }
    if (email.isBlank()) {
        onError("Googleアカウントのメール取得に失敗しました")
        return
    }
    onAuthorized(accessToken, email)
}

private fun parseInviteTargets(raw: String): List<String> {
    return raw
        .split(Regex("[,;\\s]+"))
        .map { it.trim().lowercase() }
        .filter { it.isNotBlank() }
}

private fun driveRoleLabel(role: String): String {
    return when (role.lowercase()) {
        "owner" -> "オーナー"
        "writer" -> "編集者"
        "reader" -> "閲覧者"
        "commenter" -> "コメント可"
        else -> role.ifBlank { "不明" }
    }
}

private fun formatSyncTime(timestamp: Long): String {
    if (timestamp <= 0L) return "未実行"
    return runCatching {
        SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(timestamp))
    }.getOrDefault("未実行")
}
