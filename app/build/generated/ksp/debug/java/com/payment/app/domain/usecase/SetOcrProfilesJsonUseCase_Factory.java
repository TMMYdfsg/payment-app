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
public final class SetOcrProfilesJsonUseCase_Factory implements Factory<SetOcrProfilesJsonUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public SetOcrProfilesJsonUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public SetOcrProfilesJsonUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static SetOcrProfilesJsonUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new SetOcrProfilesJsonUseCase_Factory(settingsProvider);
  }

  public static SetOcrProfilesJsonUseCase newInstance(SettingsDataStore settings) {
    return new SetOcrProfilesJsonUseCase(settings);
  }
}
