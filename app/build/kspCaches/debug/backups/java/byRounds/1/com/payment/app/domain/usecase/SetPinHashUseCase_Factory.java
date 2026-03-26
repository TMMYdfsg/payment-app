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
public final class SetPinHashUseCase_Factory implements Factory<SetPinHashUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public SetPinHashUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public SetPinHashUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static SetPinHashUseCase_Factory create(Provider<SettingsDataStore> settingsProvider) {
    return new SetPinHashUseCase_Factory(settingsProvider);
  }

  public static SetPinHashUseCase newInstance(SettingsDataStore settings) {
    return new SetPinHashUseCase(settings);
  }
}
