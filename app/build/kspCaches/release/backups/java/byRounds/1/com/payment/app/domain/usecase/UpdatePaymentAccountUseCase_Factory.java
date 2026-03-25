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
public final class UpdatePaymentAccountUseCase_Factory implements Factory<UpdatePaymentAccountUseCase> {
  private final Provider<PaymentRepository> repositoryProvider;

  public UpdatePaymentAccountUseCase_Factory(Provider<PaymentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UpdatePaymentAccountUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static UpdatePaymentAccountUseCase_Factory create(
      Provider<PaymentRepository> repositoryProvider) {
    return new UpdatePaymentAccountUseCase_Factory(repositoryProvider);
  }

  public static UpdatePaymentAccountUseCase newInstance(PaymentRepository repository) {
    return new UpdatePaymentAccountUseCase(repository);
  }
}
