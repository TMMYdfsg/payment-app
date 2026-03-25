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
public final class UpsertInstallmentUseCase_Factory implements Factory<UpsertInstallmentUseCase> {
  private final Provider<PaymentRepository> repositoryProvider;

  public UpsertInstallmentUseCase_Factory(Provider<PaymentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UpsertInstallmentUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static UpsertInstallmentUseCase_Factory create(
      Provider<PaymentRepository> repositoryProvider) {
    return new UpsertInstallmentUseCase_Factory(repositoryProvider);
  }

  public static UpsertInstallmentUseCase newInstance(PaymentRepository repository) {
    return new UpsertInstallmentUseCase(repository);
  }
}
