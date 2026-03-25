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
public final class GetReminderLeadDaysUseCase_Factory implements Factory<GetReminderLeadDaysUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public GetReminderLeadDaysUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public GetReminderLeadDaysUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static GetReminderLeadDaysUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new GetReminderLeadDaysUseCase_Factory(settingsProvider);
  }

  public static GetReminderLeadDaysUseCase newInstance(SettingsDataStore settings) {
    return new GetReminderLeadDaysUseCase(settings);
  }
}
