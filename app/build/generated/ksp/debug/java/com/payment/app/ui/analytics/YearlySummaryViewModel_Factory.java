package com.payment.app.ui.analytics;

import com.payment.app.domain.usecase.GetMonthlyPaymentsOnceUseCase;
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
public final class YearlySummaryViewModel_Factory implements Factory<YearlySummaryViewModel> {
  private final Provider<GetMonthlyPaymentsOnceUseCase> getMonthlyPaymentsOnceUseCaseProvider;

  public YearlySummaryViewModel_Factory(
      Provider<GetMonthlyPaymentsOnceUseCase> getMonthlyPaymentsOnceUseCaseProvider) {
    this.getMonthlyPaymentsOnceUseCaseProvider = getMonthlyPaymentsOnceUseCaseProvider;
  }

  @Override
  public YearlySummaryViewModel get() {
    return newInstance(getMonthlyPaymentsOnceUseCaseProvider.get());
  }

  public static YearlySummaryViewModel_Factory create(
      Provider<GetMonthlyPaymentsOnceUseCase> getMonthlyPaymentsOnceUseCaseProvider) {
    return new YearlySummaryViewModel_Factory(getMonthlyPaymentsOnceUseCaseProvider);
  }

  public static YearlySummaryViewModel newInstance(
      GetMonthlyPaymentsOnceUseCase getMonthlyPaymentsOnceUseCase) {
    return new YearlySummaryViewModel(getMonthlyPaymentsOnceUseCase);
  }
}
