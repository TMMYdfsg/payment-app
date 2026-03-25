package com.payment.app.ui.analytics;

import com.payment.app.domain.usecase.GetBudgetUseCase;
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
public final class AnalyticsViewModel_Factory implements Factory<AnalyticsViewModel> {
  private final Provider<GetMonthlyPaymentsOnceUseCase> getMonthlyPaymentsOnceUseCaseProvider;

  private final Provider<GetBudgetUseCase> getBudgetUseCaseProvider;

  public AnalyticsViewModel_Factory(
      Provider<GetMonthlyPaymentsOnceUseCase> getMonthlyPaymentsOnceUseCaseProvider,
      Provider<GetBudgetUseCase> getBudgetUseCaseProvider) {
    this.getMonthlyPaymentsOnceUseCaseProvider = getMonthlyPaymentsOnceUseCaseProvider;
    this.getBudgetUseCaseProvider = getBudgetUseCaseProvider;
  }

  @Override
  public AnalyticsViewModel get() {
    return newInstance(getMonthlyPaymentsOnceUseCaseProvider.get(), getBudgetUseCaseProvider.get());
  }

  public static AnalyticsViewModel_Factory create(
      Provider<GetMonthlyPaymentsOnceUseCase> getMonthlyPaymentsOnceUseCaseProvider,
      Provider<GetBudgetUseCase> getBudgetUseCaseProvider) {
    return new AnalyticsViewModel_Factory(getMonthlyPaymentsOnceUseCaseProvider, getBudgetUseCaseProvider);
  }

  public static AnalyticsViewModel newInstance(
      GetMonthlyPaymentsOnceUseCase getMonthlyPaymentsOnceUseCase,
      GetBudgetUseCase getBudgetUseCase) {
    return new AnalyticsViewModel(getMonthlyPaymentsOnceUseCase, getBudgetUseCase);
  }
}
