package com.payment.app.ui.notification;

import com.payment.app.domain.usecase.GetNotificationSettingsUseCase;
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

  private final Provider<UpsertNotificationSettingUseCase> upsertNotificationSettingUseCaseProvider;

  private final Provider<ReminderScheduler> reminderSchedulerProvider;

  public NotificationSettingsViewModel_Factory(
      Provider<GetNotificationSettingsUseCase> getSettingsUseCaseProvider,
      Provider<UpsertNotificationSettingUseCase> upsertNotificationSettingUseCaseProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider) {
    this.getSettingsUseCaseProvider = getSettingsUseCaseProvider;
    this.upsertNotificationSettingUseCaseProvider = upsertNotificationSettingUseCaseProvider;
    this.reminderSchedulerProvider = reminderSchedulerProvider;
  }

  @Override
  public NotificationSettingsViewModel get() {
    return newInstance(getSettingsUseCaseProvider.get(), upsertNotificationSettingUseCaseProvider.get(), reminderSchedulerProvider.get());
  }

  public static NotificationSettingsViewModel_Factory create(
      Provider<GetNotificationSettingsUseCase> getSettingsUseCaseProvider,
      Provider<UpsertNotificationSettingUseCase> upsertNotificationSettingUseCaseProvider,
      Provider<ReminderScheduler> reminderSchedulerProvider) {
    return new NotificationSettingsViewModel_Factory(getSettingsUseCaseProvider, upsertNotificationSettingUseCaseProvider, reminderSchedulerProvider);
  }

  public static NotificationSettingsViewModel newInstance(
      GetNotificationSettingsUseCase getSettingsUseCase,
      UpsertNotificationSettingUseCase upsertNotificationSettingUseCase,
      ReminderScheduler reminderScheduler) {
    return new NotificationSettingsViewModel(getSettingsUseCase, upsertNotificationSettingUseCase, reminderScheduler);
  }
}
