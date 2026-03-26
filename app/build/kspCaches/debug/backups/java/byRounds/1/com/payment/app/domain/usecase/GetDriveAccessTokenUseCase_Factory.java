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
public final class GetDriveAccessTokenUseCase_Factory implements Factory<GetDriveAccessTokenUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public GetDriveAccessTokenUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public GetDriveAccessTokenUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static GetDriveAccessTokenUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new GetDriveAccessTokenUseCase_Factory(settingsProvider);
  }

  public static GetDriveAccessTokenUseCase newInstance(SettingsDataStore settings) {
    return new GetDriveAccessTokenUseCase(settings);
  }
}
