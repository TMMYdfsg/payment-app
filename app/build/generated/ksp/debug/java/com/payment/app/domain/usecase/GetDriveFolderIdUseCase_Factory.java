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
public final class GetDriveFolderIdUseCase_Factory implements Factory<GetDriveFolderIdUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public GetDriveFolderIdUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public GetDriveFolderIdUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static GetDriveFolderIdUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new GetDriveFolderIdUseCase_Factory(settingsProvider);
  }

  public static GetDriveFolderIdUseCase newInstance(SettingsDataStore settings) {
    return new GetDriveFolderIdUseCase(settings);
  }
}
