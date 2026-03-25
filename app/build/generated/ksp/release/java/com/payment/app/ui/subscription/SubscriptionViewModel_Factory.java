package com.payment.app.ui.subscription;

import com.payment.app.data.repository.PaymentRepository;
import com.payment.app.domain.usecase.GetInstallmentsUseCase;
import com.payment.app.domain.usecase.GetSubscriptionsUseCase;
import com.payment.app.domain.usecase.UpsertInstallmentUseCase;
import com.payment.app.domain.usecase.UpsertSubscriptionUseCase;
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
public final class SubscriptionViewModel_Factory implements Factory<SubscriptionViewModel> {
  private final Provider<PaymentRepository> repositoryProvider;

  private final Provider<GetSubscriptionsUseCase> getSubscriptionsUseCaseProvider;

  private final Provider<GetInstallmentsUseCase> getInstallmentsUseCaseProvider;

  private final Provider<UpsertSubscriptionUseCase> upsertSubscriptionUseCaseProvider;

  private final Provider<UpsertInstallmentUseCase> upsertInstallmentUseCaseProvider;

  public SubscriptionViewModel_Factory(Provider<PaymentRepository> repositoryProvider,
      Provider<GetSubscriptionsUseCase> getSubscriptionsUseCaseProvider,
      Provider<GetInstallmentsUseCase> getInstallmentsUseCaseProvider,
      Provider<UpsertSubscriptionUseCase> upsertSubscriptionUseCaseProvider,
      Provider<UpsertInstallmentUseCase> upsertInstallmentUseCaseProvider) {
    this.repositoryProvider = repositoryProvider;
    this.getSubscriptionsUseCaseProvider = getSubscriptionsUseCaseProvider;
    this.getInstallmentsUseCaseProvider = getInstallmentsUseCaseProvider;
    this.upsertSubscriptionUseCaseProvider = upsertSubscriptionUseCaseProvider;
    this.upsertInstallmentUseCaseProvider = upsertInstallmentUseCaseProvider;
  }

  @Override
  public SubscriptionViewModel get() {
    return newInstance(repositoryProvider.get(), getSubscriptionsUseCaseProvider.get(), getInstallmentsUseCaseProvider.get(), upsertSubscriptionUseCaseProvider.get(), upsertInstallmentUseCaseProvider.get());
  }

  public static SubscriptionViewModel_Factory create(Provider<PaymentRepository> repositoryProvider,
      Provider<GetSubscriptionsUseCase> getSubscriptionsUseCaseProvider,
      Provider<GetInstallmentsUseCase> getInstallmentsUseCaseProvider,
      Provider<UpsertSubscriptionUseCase> upsertSubscriptionUseCaseProvider,
      Provider<UpsertInstallmentUseCase> upsertInstallmentUseCaseProvider) {
    return new SubscriptionViewModel_Factory(repositoryProvider, getSubscriptionsUseCaseProvider, getInstallmentsUseCaseProvider, upsertSubscriptionUseCaseProvider, upsertInstallmentUseCaseProvider);
  }

  public static SubscriptionViewModel newInstance(PaymentRepository repository,
      GetSubscriptionsUseCase getSubscriptionsUseCase,
      GetInstallmentsUseCase getInstallmentsUseCase,
      UpsertSubscriptionUseCase upsertSubscriptionUseCase,
      UpsertInstallmentUseCase upsertInstallmentUseCase) {
    return new SubscriptionViewModel(repository, getSubscriptionsUseCase, getInstallmentsUseCase, upsertSubscriptionUseCase, upsertInstallmentUseCase);
  }
}
