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
public final class GetOcrProfilesJsonUseCase_Factory implements Factory<GetOcrProfilesJsonUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public GetOcrProfilesJsonUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public GetOcrProfilesJsonUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static GetOcrProfilesJsonUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new GetOcrProfilesJsonUseCase_Factory(settingsProvider);
  }

  public static GetOcrProfilesJsonUseCase newInstance(SettingsDataStore settings) {
    return new GetOcrProfilesJsonUseCase(settings);
  }
}
