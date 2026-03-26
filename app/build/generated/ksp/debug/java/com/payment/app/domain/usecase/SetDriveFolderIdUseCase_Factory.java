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
public final class SetDriveFolderIdUseCase_Factory implements Factory<SetDriveFolderIdUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public SetDriveFolderIdUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public SetDriveFolderIdUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static SetDriveFolderIdUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new SetDriveFolderIdUseCase_Factory(settingsProvider);
  }

  public static SetDriveFolderIdUseCase newInstance(SettingsDataStore settings) {
    return new SetDriveFolderIdUseCase(settings);
  }
}
