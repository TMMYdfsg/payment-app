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
public final class GetThemeModeUseCase_Factory implements Factory<GetThemeModeUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public GetThemeModeUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public GetThemeModeUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static GetThemeModeUseCase_Factory create(Provider<SettingsDataStore> settingsProvider) {
    return new GetThemeModeUseCase_Factory(settingsProvider);
  }

  public static GetThemeModeUseCase newInstance(SettingsDataStore settings) {
    return new GetThemeModeUseCase(settings);
  }
}
