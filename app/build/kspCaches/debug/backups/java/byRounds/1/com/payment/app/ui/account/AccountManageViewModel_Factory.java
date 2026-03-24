package com.payment.app.ui.account;

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
public final class AccountManageViewModel_Factory implements Factory<AccountManageViewModel> {
  private final Provider<PaymentRepository> repositoryProvider;

  public AccountManageViewModel_Factory(Provider<PaymentRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public AccountManageViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static AccountManageViewModel_Factory create(
      Provider<PaymentRepository> repositoryProvider) {
    return new AccountManageViewModel_Factory(repositoryProvider);
  }

  public static AccountManageViewModel newInstance(PaymentRepository repository) {
    return new AccountManageViewModel(repository);
  }
}
