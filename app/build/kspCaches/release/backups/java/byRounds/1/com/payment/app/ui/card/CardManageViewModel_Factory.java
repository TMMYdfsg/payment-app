package com.payment.app.ui.card;

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
public final class CardManageViewModel_Factory implements Factory<CardManageViewModel> {
  private final Provider<PaymentRepository> repositoryProvider;

  public CardManageViewModel_Factory(Provider<PaymentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public CardManageViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static CardManageViewModel_Factory create(Provider<PaymentRepository> repositoryProvider) {
    return new CardManageViewModel_Factory(repositoryProvider);
  }

  public static CardManageViewModel newInstance(PaymentRepository repository) {
    return new CardManageViewModel(repository);
  }
}
