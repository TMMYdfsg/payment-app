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
public final class GetBudgetUseCase_Factory implements Factory<GetBudgetUseCase> {
  private final Provider<PaymentRepository> repositoryProvider;

  public GetBudgetUseCase_Factory(Provider<PaymentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetBudgetUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetBudgetUseCase_Factory create(Provider<PaymentRepository> repositoryProvider) {
    return new GetBudgetUseCase_Factory(repositoryProvider);
  }

  public static GetBudgetUseCase newInstance(PaymentRepository repository) {
    return new GetBudgetUseCase(repository);
  }
}
