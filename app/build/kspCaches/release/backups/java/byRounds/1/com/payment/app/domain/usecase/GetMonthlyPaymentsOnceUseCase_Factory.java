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
public final class GetMonthlyPaymentsOnceUseCase_Factory implements Factory<GetMonthlyPaymentsOnceUseCase> {
  private final Provider<PaymentRepository> repositoryProvider;

  public GetMonthlyPaymentsOnceUseCase_Factory(Provider<PaymentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetMonthlyPaymentsOnceUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetMonthlyPaymentsOnceUseCase_Factory create(
      Provider<PaymentRepository> repositoryProvider) {
    return new GetMonthlyPaymentsOnceUseCase_Factory(repositoryProvider);
  }

  public static GetMonthlyPaymentsOnceUseCase newInstance(PaymentRepository repository) {
    return new GetMonthlyPaymentsOnceUseCase(repository);
  }
}
