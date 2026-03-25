package com.payment.app.ui.notification;

import com.payment.app.domain.usecase.GetLockEnabledUseCase;
import com.payment.app.domain.usecase.GetNotificationSettingsUseCase;
import com.payment.app.domain.usecase.GetThemeAccentUseCase;
import com.payment.app.domain.usecase.GetThemeModeUseCase;
import com.payment.app.domain.usecase.SetLockEnabledUseCase;
import com.payment.app.domain.usecase.SetThemeAccentUseCase;
import com.payment.app.domain.usecase.SetThemeModeUseCase;
import com.payment.app.domain.usecase.UpsertNotificationSettingUseCase;
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

  private final Provider<SetThemeModeUseCase> setThemeModeUseCaseProvider;

  private final Provider<SetThemeAccentUseCase> setThemeAccentUseCaseProvider;

  private final Provider<SetLockEnabledUseCase> setLockEnabledUseCaseProvider;

  private final Provider<UpsertNotificationSettingUseCase> upsertNotificationSettingUseCaseProvider;

  private final Provider<ReminderScheduler> reminderSchedulerProvider;

  public NotificationSettingsViewModel_Factory(
      Provider<GetNotificationSettingsUseCase> getSettingsUseCaseProvider,
      Provider<GetThemeModeUseCase> getThemeModeUseCaseProvider,
      Provider<GetThemeAccentUseCase> getThemeAccentUseCaseProvider,
      Provider<GetLockEnabledUseCase> getLockEnabledUseCaseProvider,
      Provider<SetThemeModeUseCase> setThemeModeUseCaseProvider,
      Provider<SetThemeAccentUseCase> setThemeAccentUseCaseProvider,
      Provider<SetLockEnabledUseCase> setLockEnabledUseCaseProvider,
      Provider<UpsertNotificationSettingUseCase> upsertNotificationSettingUseCaseProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider) {
    this.getSettingsUseCaseProvider = getSettingsUseCaseProvider;
    this.getThemeModeUseCaseProvider = getThemeModeUseCaseProvider;
    this.getThemeAccentUseCaseProvider = getThemeAccentUseCaseProvider;
    this.getLockEnabledUseCaseProvider = getLockEnabledUseCaseProvider;
    this.setThemeModeUseCaseProvider = setThemeModeUseCaseProvider;
    this.setThemeAccentUseCaseProvider = setThemeAccentUseCaseProvider;
    this.setLockEnabledUseCaseProvider = setLockEnabledUseCaseProvider;
    this.upsertNotificationSettingUseCaseProvider = upsertNotificationSettingUseCaseProvider;
    this.reminderSchedulerProvider = reminderSchedulerProvider;
  }

  @Override
  public NotificationSettingsViewModel get() {
    return newInstance(getSettingsUseCaseProvider.get(), getThemeModeUseCaseProvider.get(), getThemeAccentUseCaseProvider.get(), getLockEnabledUseCaseProvider.get(), setThemeModeUseCaseProvider.get(), setThemeAccentUseCaseProvider.get(), setLockEnabledUseCaseProvider.get(), upsertNotificationSettingUseCaseProvider.get(), reminderSchedulerProvider.get());
  }

  public static NotificationSettingsViewModel_Factory create(
      Provider<GetNotificationSettingsUseCase> getSettingsUseCaseProvider,
      Provider<GetThemeModeUseCase> getThemeModeUseCaseProvider,
      Provider<GetThemeAccentUseCase> getThemeAccentUseCaseProvider,
      Provider<GetLockEnabledUseCase> getLockEnabledUseCaseProvider,
      Provider<SetThemeModeUseCase> setThemeModeUseCaseProvider,
      Provider<SetThemeAccentUseCase> setThemeAccentUseCaseProvider,
      Provider<SetLockEnabledUseCase> setLockEnabledUseCaseProvider,
      Provider<UpsertNotificationSettingUseCase> upsertNotificationSettingUseCaseProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider) {
    return new NotificationSettingsViewModel_Factory(getSettingsUseCaseProvider, getThemeModeUseCaseProvider, getThemeAccentUseCaseProvider, getLockEnabledUseCaseProvider, setThemeModeUseCaseProvider, setThemeAccentUseCaseProvider, setLockEnabledUseCaseProvider, upsertNotificationSettingUseCaseProvider, reminderSchedulerProvider);
  }

  public static NotificationSettingsViewModel newInstance(
      GetNotificationSettingsUseCase getSettingsUseCase, GetThemeModeUseCase getThemeModeUseCase,
      GetThemeAccentUseCase getThemeAccentUseCase, GetLockEnabledUseCase getLockEnabledUseCase,
      SetThemeModeUseCase setThemeModeUseCase, SetThemeAccentUseCase setThemeAccentUseCase,
      SetLockEnabledUseCase setLockEnabledUseCase,
      UpsertNotificationSettingUseCase upsertNotificationSettingUseCase,
      ReminderScheduler reminderScheduler) {
    return new NotificationSettingsViewModel(getSettingsUseCase, getThemeModeUseCase, getThemeAccentUseCase, getLockEnabledUseCase, setThemeModeUseCase, setThemeAccentUseCase, setLockEnabledUseCase, upsertNotificationSettingUseCase, reminderScheduler);
  }
}
