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
public final class SetUnlockGraceEnabledUseCase_Factory implements Factory<SetUnlockGraceEnabledUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public SetUnlockGraceEnabledUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public SetUnlockGraceEnabledUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static SetUnlockGraceEnabledUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new SetUnlockGraceEnabledUseCase_Factory(settingsProvider);
  }

  public static SetUnlockGraceEnabledUseCase newInstance(SettingsDataStore settings) {
    return new SetUnlockGraceEnabledUseCase(settings);
  }
}
