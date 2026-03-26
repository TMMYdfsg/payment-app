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
public final class GetCloudSyncPrefsUseCase_Factory implements Factory<GetCloudSyncPrefsUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public GetCloudSyncPrefsUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public GetCloudSyncPrefsUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static GetCloudSyncPrefsUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new GetCloudSyncPrefsUseCase_Factory(settingsProvider);
  }

  public static GetCloudSyncPrefsUseCase newInstance(SettingsDataStore settings) {
    return new GetCloudSyncPrefsUseCase(settings);
  }
}
