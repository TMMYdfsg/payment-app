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
public final class GetUnlockGraceEnabledUseCase_Factory implements Factory<GetUnlockGraceEnabledUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public GetUnlockGraceEnabledUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public GetUnlockGraceEnabledUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static GetUnlockGraceEnabledUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new GetUnlockGraceEnabledUseCase_Factory(settingsProvider);
  }

  public static GetUnlockGraceEnabledUseCase newInstance(SettingsDataStore settings) {
    return new GetUnlockGraceEnabledUseCase(settings);
  }
}
