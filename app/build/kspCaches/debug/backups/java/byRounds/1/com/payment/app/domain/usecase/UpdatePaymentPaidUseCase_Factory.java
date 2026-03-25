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
public final class UpdatePaymentPaidUseCase_Factory implements Factory<UpdatePaymentPaidUseCase> {
  private final Provider<PaymentRepository> repositoryProvider;

  public UpdatePaymentPaidUseCase_Factory(Provider<PaymentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UpdatePaymentPaidUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static UpdatePaymentPaidUseCase_Factory create(
      Provider<PaymentRepository> repositoryProvider) {
    return new UpdatePaymentPaidUseCase_Factory(repositoryProvider);
  }

  public static UpdatePaymentPaidUseCase newInstance(PaymentRepository repository) {
    return new UpdatePaymentPaidUseCase(repository);
  }
}
