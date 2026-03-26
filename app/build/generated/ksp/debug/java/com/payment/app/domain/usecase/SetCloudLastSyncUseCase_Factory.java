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
public final class SetCloudLastSyncUseCase_Factory implements Factory<SetCloudLastSyncUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public SetCloudLastSyncUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public SetCloudLastSyncUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static SetCloudLastSyncUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new SetCloudLastSyncUseCase_Factory(settingsProvider);
  }

  public static SetCloudLastSyncUseCase newInstance(SettingsDataStore settings) {
    return new SetCloudLastSyncUseCase(settings);
  }
}
