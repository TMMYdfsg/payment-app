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
public final class SetDriveAccessTokenUseCase_Factory implements Factory<SetDriveAccessTokenUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public SetDriveAccessTokenUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public SetDriveAccessTokenUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static SetDriveAccessTokenUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new SetDriveAccessTokenUseCase_Factory(settingsProvider);
  }

  public static SetDriveAccessTokenUseCase newInstance(SettingsDataStore settings) {
    return new SetDriveAccessTokenUseCase(settings);
  }
}
