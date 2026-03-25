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
public final class UpdatePaymentAmountUseCase_Factory implements Factory<UpdatePaymentAmountUseCase> {
  private final Provider<PaymentRepository> repositoryProvider;

  public UpdatePaymentAmountUseCase_Factory(Provider<PaymentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UpdatePaymentAmountUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static UpdatePaymentAmountUseCase_Factory create(
      Provider<PaymentRepository> repositoryProvider) {
    return new UpdatePaymentAmountUseCase_Factory(repositoryProvider);
  }

  public static UpdatePaymentAmountUseCase newInstance(PaymentRepository repository) {
    return new UpdatePaymentAmountUseCase(repository);
  }
}
