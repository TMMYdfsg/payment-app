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
public final class GetBudgetAlertThresholdUseCase_Factory implements Factory<GetBudgetAlertThresholdUseCase> {
  private final Provider<SettingsDataStore> settingsProvider;

  public GetBudgetAlertThresholdUseCase_Factory(Provider<SettingsDataStore> settingsProvider) {
    this.settingsProvider = settingsProvider;
  }

  @Override
  public GetBudgetAlertThresholdUseCase get() {
    return newInstance(settingsProvider.get());
  }

  public static GetBudgetAlertThresholdUseCase_Factory create(
      Provider<SettingsDataStore> settingsProvider) {
    return new GetBudgetAlertThresholdUseCase_Factory(settingsProvider);
  }

  public static GetBudgetAlertThresholdUseCase newInstance(SettingsDataStore settings) {
    return new GetBudgetAlertThresholdUseCase(settings);
  }
}
