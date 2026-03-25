package com.payment.app.ui.calendar;

import com.payment.app.domain.usecase.GetMonthlyPaymentsUseCase;
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
public final class CalendarViewModel_Factory implements Factory<CalendarViewModel> {
  private final Provider<GetMonthlyPaymentsUseCase> getMonthlyPaymentsUseCaseProvider;

  public CalendarViewModel_Factory(
      Provider<GetMonthlyPaymentsUseCase> getMonthlyPaymentsUseCaseProvider) {
    this.getMonthlyPaymentsUseCaseProvider = getMonthlyPaymentsUseCaseProvider;
  }

  @Override
  public CalendarViewModel get() {
    return newInstance(getMonthlyPaymentsUseCaseProvider.get());
  }

  public static CalendarViewModel_Factory create(
      Provider<GetMonthlyPaymentsUseCase> getMonthlyPaymentsUseCaseProvider) {
    return new CalendarViewModel_Factory(getMonthlyPaymentsUseCaseProvider);
  }

  public static CalendarViewModel newInstance(GetMonthlyPaymentsUseCase getMonthlyPaymentsUseCase) {
    return new CalendarViewModel(getMonthlyPaymentsUseCase);
  }
}
