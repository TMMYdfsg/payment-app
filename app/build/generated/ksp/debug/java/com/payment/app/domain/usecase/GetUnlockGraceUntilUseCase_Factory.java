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
public final class GetUnlockGraceUntilUseCase_Factory implements Factory<GetUnlockGraceUntilUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public GetUnlockGraceUntilUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public GetUnlockGraceUntilUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static GetUnlockGraceUntilUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new GetUnlockGraceUntilUseCase_Factory(settingsProvider);
  }

  public static GetUnlockGraceUntilUseCase newInstance(SettingsDataStore settings) {
    return new GetUnlockGraceUntilUseCase(settings);
  }
}
