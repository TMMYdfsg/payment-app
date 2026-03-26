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
public final class SetCloudSyncEnabledUseCase_Factory implements Factory<SetCloudSyncEnabledUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public SetCloudSyncEnabledUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public SetCloudSyncEnabledUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static SetCloudSyncEnabledUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new SetCloudSyncEnabledUseCase_Factory(settingsProvider);
  }

  public static SetCloudSyncEnabledUseCase newInstance(SettingsDataStore settings) {
    return new SetCloudSyncEnabledUseCase(settings);
  }
}
