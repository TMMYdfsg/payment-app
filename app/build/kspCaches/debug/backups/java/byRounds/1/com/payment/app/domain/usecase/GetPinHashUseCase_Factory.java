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
public final class GetPinHashUseCase_Factory implements Factory<GetPinHashUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public GetPinHashUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public GetPinHashUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static GetPinHashUseCase_Factory create(Provider<SettingsDataStore> settingsProvider) {
    return new GetPinHashUseCase_Factory(settingsProvider);
  }

  public static GetPinHashUseCase newInstance(SettingsDataStore settings) {
    return new GetPinHashUseCase(settings);
  }
}
