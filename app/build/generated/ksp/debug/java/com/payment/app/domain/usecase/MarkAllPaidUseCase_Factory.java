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
public final class MarkAllPaidUseCase_Factory implements Factory<MarkAllPaidUseCase> {
  private final Provider<PaymentRepository> repositoryProvider;

  public MarkAllPaidUseCase_Factory(Provider<PaymentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public MarkAllPaidUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static MarkAllPaidUseCase_Factory create(Provider<PaymentRepository> repositoryProvider) {
    return new MarkAllPaidUseCase_Factory(repositoryProvider);
  }

  public static MarkAllPaidUseCase newInstance(PaymentRepository repository) {
    return new MarkAllPaidUseCase(repository);
  }
}
