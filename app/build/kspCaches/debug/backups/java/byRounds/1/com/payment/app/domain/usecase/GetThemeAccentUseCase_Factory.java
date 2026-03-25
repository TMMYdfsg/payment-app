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
public final class GetThemeAccentUseCase_Factory implements Factory<GetThemeAccentUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public GetThemeAccentUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public GetThemeAccentUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static GetThemeAccentUseCase_Factory create(Provider<SettingsDataStore> settingsProvider) {
    return new GetThemeAccentUseCase_Factory(settingsProvider);
  }

  public static GetThemeAccentUseCase newInstance(SettingsDataStore settings) {
    return new GetThemeAccentUseCase(settings);
  }
}
