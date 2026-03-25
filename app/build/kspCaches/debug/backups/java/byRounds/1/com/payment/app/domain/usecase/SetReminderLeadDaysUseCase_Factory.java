package com.payment.app.domain.usecase;

import com.payment.app.data.datastore.SettingsDataStore;
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
public final class SetReminderLeadDaysUseCase_Factory implements Factory<SetReminderLeadDaysUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public SetReminderLeadDaysUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public SetReminderLeadDaysUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static SetReminderLeadDaysUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new SetReminderLeadDaysUseCase_Factory(settingsProvider);
  }

  public static SetReminderLeadDaysUseCase newInstance(SettingsDataStore settings) {
    return new SetReminderLeadDaysUseCase(settings);
  }
}
