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
public final class UpsertSubscriptionUseCase_Factory implements Factory<UpsertSubscriptionUseCase> {
  private final Provider<PaymentRepository> repositoryProvider;

  public UpsertSubscriptionUseCase_Factory(Provider<PaymentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UpsertSubscriptionUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static UpsertSubscriptionUseCase_Factory create(
      Provider<PaymentRepository> repositoryProvider) {
    return new UpsertSubscriptionUseCase_Factory(repositoryProvider);
  }

  public static UpsertSubscriptionUseCase newInstance(PaymentRepository repository) {
    return new UpsertSubscriptionUseCase(repository);
  }
}
