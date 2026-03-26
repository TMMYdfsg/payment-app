package com.payment.app.ui.notification;

import com.payment.app.domain.usecase.ClearPinHashUseCase;
import com.payment.app.domain.usecase.CreateDriveFolderUseCase;
import com.payment.app.domain.usecase.DownloadSharedBackupFromDriveUseCase;
import com.payment.app.domain.usecase.ExportBackupJsonUseCase;
import com.payment.app.domain.usecase.GetCloudSyncPrefsUseCase;
import com.payment.app.domain.usecase.GetDriveAccessTokenUseCase;
import com.payment.app.domain.usecase.GetDriveFolderIdUseCase;
import com.payment.app.domain.usecase.GetDriveGroupNameUseCase;
import com.payment.app.domain.usecase.GetLockEnabledUseCase;
import com.payment.app.domain.usecase.GetNotificationSettingsUseCase;
import com.payment.app.domain.usecase.GetPinHashUseCase;
import com.payment.app.domain.usecase.GetSyncAccountEmailUseCase;
import com.payment.app.domain.usecase.GetThemeAccentUseCase;
import com.payment.app.domain.usecase.GetThemeModeUseCase;
import com.payment.app.domain.usecase.GetUnlockGraceEnabledUseCase;
import com.payment.app.domain.usecase.ImportBackupJsonUseCase;
import com.payment.app.domain.usecase.InviteDriveMemberUseCase;
import com.payment.app.domain.usecase.ListDriveMembersUseCase;
import com.payment.app.domain.usecase.RemoveDriveMemberUseCase;
import com.payment.app.domain.usecase.SetCloudLastSyncUseCase;
import com.payment.app.domain.usecase.SetCloudSyncEnabledUseCase;
import com.payment.app.domain.usecase.SetDriveAccessTokenUseCase;
import com.payment.app.domain.usecase.SetDriveFolderIdUseCase;
import com.payment.app.domain.usecase.SetDriveGroupNameUseCase;
import com.payment.app.domain.usecase.SetLockEnabledUseCase;
import com.payment.app.domain.usecase.SetPinHashUseCase;
import com.payment.app.domain.usecase.SetSyncAccountEmailUseCase;
import com.payment.app.domain.usecase.SetThemeAccentUseCase;
import com.payment.app.domain.usecase.SetThemeModeUseCase;
import com.payment.app.domain.usecase.SetUnlockGraceEnabledUseCase;
import com.payment.app.domain.usecase.UpdateDriveMemberRoleUseCase;
import com.payment.app.domain.usecase.UpsertNotificationSettingUseCase;
import com.payment.app.domain.usecase.UpsertSharedBackupToDriveUseCase;
import com.payment.app.notifications.ReminderScheduler;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class NotificationSettingsViewModel_Factory implements Factory<NotificationSettingsViewModel> {
  private final Provider<GetNotificationSettingsUseCase> getSettingsUseCaseProvider;

  private final Provider<GetThemeModeUseCase> getThemeModeUseCaseProvider;

  private final Provider<GetThemeAccentUseCase> getThemeAccentUseCaseProvider;

  private final Provider<GetLockEnabledUseCase> getLockEnabledUseCaseProvider;

  private final Provider<GetPinHashUseCase> getPinHashUseCaseProvider;

  private final Provider<GetUnlockGraceEnabledUseCase> getUnlockGraceEnabledUseCaseProvider;

  private final Provider<GetDriveAccessTokenUseCase> getDriveAccessTokenUseCaseProvider;

  private final Provider<GetDriveFolderIdUseCase> getDriveFolderIdUseCaseProvider;

  private final Provider<GetDriveGroupNameUseCase> getDriveGroupNameUseCaseProvider;

  private final Provider<GetSyncAccountEmailUseCase> getSyncAccountEmailUseCaseProvider;

  private final Provider<GetCloudSyncPrefsUseCase> getCloudSyncPrefsUseCaseProvider;

  private final Provider<SetThemeModeUseCase> setThemeModeUseCaseProvider;

  private final Provider<SetThemeAccentUseCase> setThemeAccentUseCaseProvider;

  private final Provider<SetLockEnabledUseCase> setLockEnabledUseCaseProvider;

  private final Provider<SetPinHashUseCase> setPinHashUseCaseProvider;

  private final Provider<ClearPinHashUseCase> clearPinHashUseCaseProvider;

  private final Provider<SetUnlockGraceEnabledUseCase> setUnlockGraceEnabledUseCaseProvider;

  private final Provider<SetDriveAccessTokenUseCase> setDriveAccessTokenUseCaseProvider;

  private final Provider<SetDriveFolderIdUseCase> setDriveFolderIdUseCaseProvider;

  private final Provider<SetDriveGroupNameUseCase> setDriveGroupNameUseCaseProvider;

  private final Provider<SetSyncAccountEmailUseCase> setSyncAccountEmailUseCaseProvider;

  private final Provider<SetCloudSyncEnabledUseCase> setCloudSyncEnabledUseCaseProvider;

  private final Provider<SetCloudLastSyncUseCase> setCloudLastSyncUseCaseProvider;

  private final Provider<UpsertNotificationSettingUseCase> upsertNotificationSettingUseCaseProvider;

  private final Provider<ExportBackupJsonUseCase> exportBackupJsonUseCaseProvider;

  private final Provider<ImportBackupJsonUseCase> importBackupJsonUseCaseProvider;

  private final Provider<CreateDriveFolderUseCase> createDriveFolderUseCaseProvider;

  private final Provider<InviteDriveMemberUseCase> inviteDriveMemberUseCaseProvider;

  private final Provider<ListDriveMembersUseCase> listDriveMembersUseCaseProvider;

  private final Provider<UpdateDriveMemberRoleUseCase> updateDriveMemberRoleUseCaseProvider;

  private final Provider<RemoveDriveMemberUseCase> removeDriveMemberUseCaseProvider;

  private final Provider<UpsertSharedBackupToDriveUseCase> upsertSharedBackupToDriveUseCaseProvider;

  private final Provider<DownloadSharedBackupFromDriveUseCase> downloadSharedBackupFromDriveUseCaseProvider;

  private final Provider<ReminderScheduler> reminderSchedulerProvider;

  public NotificationSettingsViewModel_Factory(
      Provider<GetNotificationSettingsUseCase> getSettingsUseCaseProvider,
      Provider<GetThemeModeUseCase> getThemeModeUseCaseProvider,
      Provider<GetThemeAccentUseCase> getThemeAccentUseCaseProvider,
      Provider<GetLockEnabledUseCase> getLockEnabledUseCaseProvider,
      Provider<GetPinHashUseCase> getPinHashUseCaseProvider,
      Provider<GetUnlockGraceEnabledUseCase> getUnlockGraceEnabledUseCaseProvider,
      Provider<GetDriveAccessTokenUseCase> getDriveAccessTokenUseCaseProvider,
      Provider<GetDriveFolderIdUseCase> getDriveFolderIdUseCaseProvider,
      Provider<GetDriveGroupNameUseCase> getDriveGroupNameUseCaseProvider,
      Provider<GetSyncAccountEmailUseCase> getSyncAccountEmailUseCaseProvider,
      Provider<GetCloudSyncPrefsUseCase> getCloudSyncPrefsUseCaseProvider,
      Provider<SetThemeModeUseCase> setThemeModeUseCaseProvider,
      Provider<SetThemeAccentUseCase> setThemeAccentUseCaseProvider,
      Provider<SetLockEnabledUseCase> setLockEnabledUseCaseProvider,
      Provider<SetPinHashUseCase> setPinHashUseCaseProvider,
      Provider<ClearPinHashUseCase> clearPinHashUseCaseProvider,
      Provider<SetUnlockGraceEnabledUseCase> setUnlockGraceEnabledUseCaseProvider,
      Provider<SetDriveAccessTokenUseCase> setDriveAccessTokenUseCaseProvider,
      Provider<SetDriveFolderIdUseCase> setDriveFolderIdUseCaseProvider,
      Provider<SetDriveGroupNameUseCase> setDriveGroupNameUseCaseProvider,
      Provider<SetSyncAccountEmailUseCase> setSyncAccountEmailUseCaseProvider,
      Provider<SetCloudSyncEnabledUseCase> setCloudSyncEnabledUseCaseProvider,
      Provider<SetCloudLastSyncUseCase> setCloudLastSyncUseCaseProvider,
      Provider<UpsertNotificationSettingUseCase> upsertNotificationSettingUseCaseProvider,
      Provider<ExportBackupJsonUseCase> exportBackupJsonUseCaseProvider,
      Provider<ImportBackupJsonUseCase> importBackupJsonUseCaseProvider,
      Provider<CreateDriveFolderUseCase> createDriveFolderUseCaseProvider,
      Provider<InviteDriveMemberUseCase> inviteDriveMemberUseCaseProvider,
      Provider<ListDriveMembersUseCase> listDriveMembersUseCaseProvider,
      Provider<UpdateDriveMemberRoleUseCase> updateDriveMemberRoleUseCaseProvider,
      Provider<RemoveDriveMemberUseCase> removeDriveMemberUseCaseProvider,
      Provider<UpsertSharedBackupToDriveUseCase> upsertSharedBackupToDriveUseCaseProvider,
      Provider<DownloadSharedBackupFromDriveUseCase> downloadSharedBackupFromDriveUseCaseProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider) {
    this.getSettingsUseCaseProvider = getSettingsUseCaseProvider;
    this.getThemeModeUseCaseProvider = getThemeModeUseCaseProvider;
    this.getThemeAccentUseCaseProvider = getThemeAccentUseCaseProvider;
    this.getLockEnabledUseCaseProvider = getLockEnabledUseCaseProvider;
    this.getPinHashUseCaseProvider = getPinHashUseCaseProvider;
    this.getUnlockGraceEnabledUseCaseProvider = getUnlockGraceEnabledUseCaseProvider;
    this.getDriveAccessTokenUseCaseProvider = getDriveAccessTokenUseCaseProvider;
    this.getDriveFolderIdUseCaseProvider = getDriveFolderIdUseCaseProvider;
    this.getDriveGroupNameUseCaseProvider = getDriveGroupNameUseCaseProvider;
    this.getSyncAccountEmailUseCaseProvider = getSyncAccountEmailUseCaseProvider;
    this.getCloudSyncPrefsUseCaseProvider = getCloudSyncPrefsUseCaseProvider;
    this.setThemeModeUseCaseProvider = setThemeModeUseCaseProvider;
    this.setThemeAccentUseCaseProvider = setThemeAccentUseCaseProvider;
    this.setLockEnabledUseCaseProvider = setLockEnabledUseCaseProvider;
    this.setPinHashUseCaseProvider = setPinHashUseCaseProvider;
    this.clearPinHashUseCaseProvider = clearPinHashUseCaseProvider;
    this.setUnlockGraceEnabledUseCaseProvider = setUnlockGraceEnabledUseCaseProvider;
    this.setDriveAccessTokenUseCaseProvider = setDriveAccessTokenUseCaseProvider;
    this.setDriveFolderIdUseCaseProvider = setDriveFolderIdUseCaseProvider;
    this.setDriveGroupNameUseCaseProvider = setDriveGroupNameUseCaseProvider;
    this.setSyncAccountEmailUseCaseProvider = setSyncAccountEmailUseCaseProvider;
    this.setCloudSyncEnabledUseCaseProvider = setCloudSyncEnabledUseCaseProvider;
    this.setCloudLastSyncUseCaseProvider = setCloudLastSyncUseCaseProvider;
    this.upsertNotificationSettingUseCaseProvider = upsertNotificationSettingUseCaseProvider;
    this.exportBackupJsonUseCaseProvider = exportBackupJsonUseCaseProvider;
    this.importBackupJsonUseCaseProvider = importBackupJsonUseCaseProvider;
    this.createDriveFolderUseCaseProvider = createDriveFolderUseCaseProvider;
    this.inviteDriveMemberUseCaseProvider = inviteDriveMemberUseCaseProvider;
    this.listDriveMembersUseCaseProvider = listDriveMembersUseCaseProvider;
    this.updateDriveMemberRoleUseCaseProvider = updateDriveMemberRoleUseCaseProvider;
    this.removeDriveMemberUseCaseProvider = removeDriveMemberUseCaseProvider;
    this.upsertSharedBackupToDriveUseCaseProvider = upsertSharedBackupToDriveUseCaseProvider;
    this.downloadSharedBackupFromDriveUseCaseProvider = downloadSharedBackupFromDriveUseCaseProvider;
    this.reminderSchedulerProvider = reminderSchedulerProvider;
  }

  @Override
  public NotificationSettingsViewModel get() {
    return newInstance(getSettingsUseCaseProvider.get(), getThemeModeUseCaseProvider.get(), getThemeAccentUseCaseProvider.get(), getLockEnabledUseCaseProvider.get(), getPinHashUseCaseProvider.get(), getUnlockGraceEnabledUseCaseProvider.get(), getDriveAccessTokenUseCaseProvider.get(), getDriveFolderIdUseCaseProvider.get(), getDriveGroupNameUseCaseProvider.get(), getSyncAccountEmailUseCaseProvider.get(), getCloudSyncPrefsUseCaseProvider.get(), setThemeModeUseCaseProvider.get(), setThemeAccentUseCaseProvider.get(), setLockEnabledUseCaseProvider.get(), setPinHashUseCaseProvider.get(), clearPinHashUseCaseProvider.get(), setUnlockGraceEnabledUseCaseProvider.get(), setDriveAccessTokenUseCaseProvider.get(), setDriveFolderIdUseCaseProvider.get(), setDriveGroupNameUseCaseProvider.get(), setSyncAccountEmailUseCaseProvider.get(), setCloudSyncEnabledUseCaseProvider.get(), setCloudLastSyncUseCaseProvider.get(), upsertNotificationSettingUseCaseProvider.get(), exportBackupJsonUseCaseProvider.get(), importBackupJsonUseCaseProvider.get(), createDriveFolderUseCaseProvider.get(), inviteDriveMemberUseCaseProvider.get(), listDriveMembersUseCaseProvider.get(), updateDriveMemberRoleUseCaseProvider.get(), removeDriveMemberUseCaseProvider.get(), upsertSharedBackupToDriveUseCaseProvider.get(), downloadSharedBackupFromDriveUseCaseProvider.get(), reminderSchedulerProvider.get());
  }

  public static NotificationSettingsViewModel_Factory create(
      Provider<GetNotificationSettingsUseCase> getSettingsUseCaseProvider,
      Provider<GetThemeModeUseCase> getThemeModeUseCaseProvider,
      Provider<GetThemeAccentUseCase> getThemeAccentUseCaseProvider,
      Provider<GetLockEnabledUseCase> getLockEnabledUseCaseProvider,
      Provider<GetPinHashUseCase> getPinHashUseCaseProvider,
      Provider<GetUnlockGraceEnabledUseCase> getUnlockGraceEnabledUseCaseProvider,
      Provider<GetDriveAccessTokenUseCase> getDriveAccessTokenUseCaseProvider,
      Provider<GetDriveFolderIdUseCase> getDriveFolderIdUseCaseProvider,
      Provider<GetDriveGroupNameUseCase> getDriveGroupNameUseCaseProvider,
      Provider<GetSyncAccountEmailUseCase> getSyncAccountEmailUseCaseProvider,
      Provider<GetCloudSyncPrefsUseCase> getCloudSyncPrefsUseCaseProvider,
      Provider<SetThemeModeUseCase> setThemeModeUseCaseProvider,
      Provider<SetThemeAccentUseCase> setThemeAccentUseCaseProvider,
      Provider<SetLockEnabledUseCase> setLockEnabledUseCaseProvider,
      Provider<SetPinHashUseCase> setPinHashUseCaseProvider,
      Provider<ClearPinHashUseCase> clearPinHashUseCaseProvider,
      Provider<SetUnlockGraceEnabledUseCase> setUnlockGraceEnabledUseCaseProvider,
      Provider<SetDriveAccessTokenUseCase> setDriveAccessTokenUseCaseProvider,
      Provider<SetDriveFolderIdUseCase> setDriveFolderIdUseCaseProvider,
      Provider<SetDriveGroupNameUseCase> setDriveGroupNameUseCaseProvider,
      Provider<SetSyncAccountEmailUseCase> setSyncAccountEmailUseCaseProvider,
      Provider<SetCloudSyncEnabledUseCase> setCloudSyncEnabledUseCaseProvider,
      Provider<SetCloudLastSyncUseCase> setCloudLastSyncUseCaseProvider,
      Provider<UpsertNotificationSettingUseCase> upsertNotificationSettingUseCaseProvider,
      Provider<ExportBackupJsonUseCase> exportBackupJsonUseCaseProvider,
      Provider<ImportBackupJsonUseCase> importBackupJsonUseCaseProvider,
      Provider<CreateDriveFolderUseCase> createDriveFolderUseCaseProvider,
      Provider<InviteDriveMemberUseCase> inviteDriveMemberUseCaseProvider,
      Provider<ListDriveMembersUseCase> listDriveMembersUseCaseProvider,
      Provider<UpdateDriveMemberRoleUseCase> updateDriveMemberRoleUseCaseProvider,
      Provider<RemoveDriveMemberUseCase> removeDriveMemberUseCaseProvider,
      Provider<UpsertSharedBackupToDriveUseCase> upsertSharedBackupToDriveUseCaseProvider,
      Provider<DownloadSharedBackupFromDriveUseCase> downloadSharedBackupFromDriveUseCaseProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider) {
    return new NotificationSettingsViewModel_Factory(getSettingsUseCaseProvider, getThemeModeUseCaseProvider, getThemeAccentUseCaseProvider, getLockEnabledUseCaseProvider, getPinHashUseCaseProvider, getUnlockGraceEnabledUseCaseProvider, getDriveAccessTokenUseCaseProvider, getDriveFolderIdUseCaseProvider, getDriveGroupNameUseCaseProvider, getSyncAccountEmailUseCaseProvider, getCloudSyncPrefsUseCaseProvider, setThemeModeUseCaseProvider, setThemeAccentUseCaseProvider, setLockEnabledUseCaseProvider, setPinHashUseCaseProvider, clearPinHashUseCaseProvider, setUnlockGraceEnabledUseCaseProvider, setDriveAccessTokenUseCaseProvider, setDriveFolderIdUseCaseProvider, setDriveGroupNameUseCaseProvider, setSyncAccountEmailUseCaseProvider, setCloudSyncEnabledUseCaseProvider, setCloudLastSyncUseCaseProvider, upsertNotificationSettingUseCaseProvider, exportBackupJsonUseCaseProvider, importBackupJsonUseCaseProvider, createDriveFolderUseCaseProvider, inviteDriveMemberUseCaseProvider, listDriveMembersUseCaseProvider, updateDriveMemberRoleUseCaseProvider, removeDriveMemberUseCaseProvider, upsertSharedBackupToDriveUseCaseProvider, downloadSharedBackupFromDriveUseCaseProvider, reminderSchedulerProvider);
  }

  public static NotificationSettingsViewModel newInstance(
      GetNotificationSettingsUseCase getSettingsUseCase, GetThemeModeUseCase getThemeModeUseCase,
      GetThemeAccentUseCase getThemeAccentUseCase, GetLockEnabledUseCase getLockEnabledUseCase,
      GetPinHashUseCase getPinHashUseCase,
      GetUnlockGraceEnabledUseCase getUnlockGraceEnabledUseCase,
      GetDriveAccessTokenUseCase getDriveAccessTokenUseCase,
      GetDriveFolderIdUseCase getDriveFolderIdUseCase,
      GetDriveGroupNameUseCase getDriveGroupNameUseCase,
      GetSyncAccountEmailUseCase getSyncAccountEmailUseCase,
      GetCloudSyncPrefsUseCase getCloudSyncPrefsUseCase, SetThemeModeUseCase setThemeModeUseCase,
      SetThemeAccentUseCase setThemeAccentUseCase, SetLockEnabledUseCase setLockEnabledUseCase,
      SetPinHashUseCase setPinHashUseCase, ClearPinHashUseCase clearPinHashUseCase,
      SetUnlockGraceEnabledUseCase setUnlockGraceEnabledUseCase,
      SetDriveAccessTokenUseCase setDriveAccessTokenUseCase,
      SetDriveFolderIdUseCase setDriveFolderIdUseCase,
      SetDriveGroupNameUseCase setDriveGroupNameUseCase,
      SetSyncAccountEmailUseCase setSyncAccountEmailUseCase,
      SetCloudSyncEnabledUseCase setCloudSyncEnabledUseCase,
      SetCloudLastSyncUseCase setCloudLastSyncUseCase,
      UpsertNotificationSettingUseCase upsertNotificationSettingUseCase,
      ExportBackupJsonUseCase exportBackupJsonUseCase,
      ImportBackupJsonUseCase importBackupJsonUseCase,
      CreateDriveFolderUseCase createDriveFolderUseCase,
      InviteDriveMemberUseCase inviteDriveMemberUseCase,
      ListDriveMembersUseCase listDriveMembersUseCase,
      UpdateDriveMemberRoleUseCase updateDriveMemberRoleUseCase,
      RemoveDriveMemberUseCase removeDriveMemberUseCase,
      UpsertSharedBackupToDriveUseCase upsertSharedBackupToDriveUseCase,
      DownloadSharedBackupFromDriveUseCase downloadSharedBackupFromDriveUseCase,
      ReminderScheduler reminderScheduler) {
    return new NotificationSettingsViewModel(getSettingsUseCase, getThemeModeUseCase, getThemeAccentUseCase, getLockEnabledUseCase, getPinHashUseCase, getUnlockGraceEnabledUseCase, getDriveAccessTokenUseCase, getDriveFolderIdUseCase, getDriveGroupNameUseCase, getSyncAccountEmailUseCase, getCloudSyncPrefsUseCase, setThemeModeUseCase, setThemeAccentUseCase, setLockEnabledUseCase, setPinHashUseCase, clearPinHashUseCase, setUnlockGraceEnabledUseCase, setDriveAccessTokenUseCase, setDriveFolderIdUseCase, setDriveGroupNameUseCase, setSyncAccountEmailUseCase, setCloudSyncEnabledUseCase, setCloudLastSyncUseCase, upsertNotificationSettingUseCase, exportBackupJsonUseCase, importBackupJsonUseCase, createDriveFolderUseCase, inviteDriveMemberUseCase, listDriveMembersUseCase, updateDriveMemberRoleUseCase, removeDriveMemberUseCase, upsertSharedBackupToDriveUseCase, downloadSharedBackupFromDriveUseCase, reminderScheduler);
  }
}
