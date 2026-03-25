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
public final class SetThemeAccentUseCase_Factory implements Factory<SetThemeAccentUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public SetThemeAccentUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public SetThemeAccentUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static SetThemeAccentUseCase_Factory create(Provider<SettingsDataStore> settingsProvider) {
    return new SetThemeAccentUseCase_Factory(settingsProvider);
  }

  public static SetThemeAccentUseCase newInstance(SettingsDataStore settings) {
    return new SetThemeAccentUseCase(settings);
  }
}
