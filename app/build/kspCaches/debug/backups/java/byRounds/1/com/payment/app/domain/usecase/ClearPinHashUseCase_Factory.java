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
public final class ClearPinHashUseCase_Factory implements Factory<ClearPinHashUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public ClearPinHashUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public ClearPinHashUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static ClearPinHashUseCase_Factory create(Provider<SettingsDataStore> settingsProvider) {
    return new ClearPinHashUseCase_Factory(settingsProvider);
  }

  public static ClearPinHashUseCase newInstance(SettingsDataStore settings) {
    return new ClearPinHashUseCase(settings);
  }
}
