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
public final class SetSyncAccountEmailUseCase_Factory implements Factory<SetSyncAccountEmailUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public SetSyncAccountEmailUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public SetSyncAccountEmailUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static SetSyncAccountEmailUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new SetSyncAccountEmailUseCase_Factory(settingsProvider);
  }

  public static SetSyncAccountEmailUseCase newInstance(SettingsDataStore settings) {
    return new SetSyncAccountEmailUseCase(settings);
  }
}
