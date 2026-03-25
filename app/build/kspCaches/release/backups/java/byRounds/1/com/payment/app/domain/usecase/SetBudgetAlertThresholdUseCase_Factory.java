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
public final class SetBudgetAlertThresholdUseCase_Factory implements Factory<SetBudgetAlertThresholdUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public SetBudgetAlertThresholdUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public SetBudgetAlertThresholdUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static SetBudgetAlertThresholdUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new SetBudgetAlertThresholdUseCase_Factory(settingsProvider);
  }

  public static SetBudgetAlertThresholdUseCase newInstance(SettingsDataStore settings) {
    return new SetBudgetAlertThresholdUseCase(settings);
  }
}
