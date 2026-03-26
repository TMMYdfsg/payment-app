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
public final class SetDriveGroupNameUseCase_Factory implements Factory<SetDriveGroupNameUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public SetDriveGroupNameUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public SetDriveGroupNameUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static SetDriveGroupNameUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new SetDriveGroupNameUseCase_Factory(settingsProvider);
  }

  public static SetDriveGroupNameUseCase newInstance(SettingsDataStore settings) {
    return new SetDriveGroupNameUseCase(settings);
  }
}
