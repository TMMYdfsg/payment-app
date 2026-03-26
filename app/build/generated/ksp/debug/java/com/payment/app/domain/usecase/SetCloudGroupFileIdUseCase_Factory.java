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
public final class SetCloudGroupFileIdUseCase_Factory implements Factory<SetCloudGroupFileIdUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public SetCloudGroupFileIdUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public SetCloudGroupFileIdUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static SetCloudGroupFileIdUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new SetCloudGroupFileIdUseCase_Factory(settingsProvider);
  }

  public static SetCloudGroupFileIdUseCase newInstance(SettingsDataStore settings) {
    return new SetCloudGroupFileIdUseCase(settings);
  }
}
