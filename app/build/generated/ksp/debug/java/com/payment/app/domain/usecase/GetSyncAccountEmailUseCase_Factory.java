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
public final class GetSyncAccountEmailUseCase_Factory implements Factory<GetSyncAccountEmailUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public GetSyncAccountEmailUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public GetSyncAccountEmailUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static GetSyncAccountEmailUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new GetSyncAccountEmailUseCase_Factory(settingsProvider);
  }

  public static GetSyncAccountEmailUseCase newInstance(SettingsDataStore settings) {
    return new GetSyncAccountEmailUseCase(settings);
  }
}
