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
public final class GetDriveGroupNameUseCase_Factory implements Factory<GetDriveGroupNameUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public GetDriveGroupNameUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public GetDriveGroupNameUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static GetDriveGroupNameUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new GetDriveGroupNameUseCase_Factory(settingsProvider);
  }

  public static GetDriveGroupNameUseCase newInstance(SettingsDataStore settings) {
    return new GetDriveGroupNameUseCase(settings);
  }
}
