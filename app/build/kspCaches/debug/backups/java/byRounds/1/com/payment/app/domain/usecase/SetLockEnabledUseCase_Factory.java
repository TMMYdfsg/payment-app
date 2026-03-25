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
public final class SetLockEnabledUseCase_Factory implements Factory<SetLockEnabledUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public SetLockEnabledUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public SetLockEnabledUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static SetLockEnabledUseCase_Factory create(Provider<SettingsDataStore> settingsProvider) {
    return new SetLockEnabledUseCase_Factory(settingsProvider);
  }

  public static SetLockEnabledUseCase newInstance(SettingsDataStore settings) {
    return new SetLockEnabledUseCase(settings);
  }
}
