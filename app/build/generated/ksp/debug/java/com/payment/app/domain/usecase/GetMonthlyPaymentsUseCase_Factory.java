package com.payment.app.domain.usecase;

import com.payment.app.data.repository.PaymentRepository;
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
public final class GetMonthlyPaymentsUseCase_Factory implements Factory<GetMonthlyPaymentsUseCase> {
  private final Provider<PaymentRepository> repositoryProvider;

  public GetMonthlyPaymentsUseCase_Factory(Provider<PaymentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetMonthlyPaymentsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetMonthlyPaymentsUseCase_Factory create(
      Provider<PaymentRepository> repositoryProvider) {
    return new GetMonthlyPaymentsUseCase_Factory(repositoryProvider);
  }

  public static GetMonthlyPaymentsUseCase newInstance(PaymentRepository repository) {
    return new GetMonthlyPaymentsUseCase(repository);
  }
}
